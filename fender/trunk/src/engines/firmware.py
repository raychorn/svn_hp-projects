#####################################################
#
# firmware.py
#
# Copyright 2009 Hewlett-Packard Development Company, L.P.
#
# Hewlett-Packard and the Hewlett-Packard logo are trademarks of
# Hewlett-Packard Development Company, L.P. in the U.S. and/or other countries.
#
# Confidential computer software. Valid license from Hewlett-Packard required
# for possession, use or copying. Consistent with FAR 12.211 and 12.212,
# Commercial Computer Software, Computer Software Documentation, and Technical
# Data for Commercial Items are licensed to the U.S. Government under
# vendor's standard commercial license.
#
# Author:
#    James Abendroth
# 
# Description:
#    ProLiant firmware objects
#
#####################################################
from threading import Thread, Semaphore
from time import sleep
import time
#from util import password, config
from util.config import config
from logging import getLogger
from uuid import UUID
import string 

import sys
from xml.dom.minidom import Element
import pywbem
from pywbem import *
from urlparse import urlparse
import json
from util.smartcomponent import get_scm

from util.wbemclient import WBEMClient

log = getLogger(__name__)

UPDATE_CODES = {
    4096: "Request successful. Job was initiated.",
    4105: "Dependencies required for the execution of the SmartComponent are missing.",
    4106: "The Smart Component supplied does not match any components in this host.",
    6: "An update of the requested component is currently in progress."
}

# Firmware type IDs
UNKNOWN = 0
STORAGE = 1
NIC = 2
SYSTEM = 3
MANAGEMENT = 4

system_ids = (UNKNOWN, STORAGE, NIC, SYSTEM)
management_ids = (MANAGEMENT,)

# Host firmware class - used to represent the firmware collection
# for a single ESX/ESXi host.  Collects firmware information using
# WBEM. Also updates the firmware information on a periodic basis in
# case of changes.
class HostFirmware(WBEMClient):

    # CIM classes that we're interested in for firmware objects.
    FIRMWARE_CLASS = 'CIM_SoftwareIdentity'
    SC_JOB_CLASS = 'SMX_SCInstallerConcreteJob'

    CSV_FMT_HEADER = "Host,Name,Description,Version,Manufacturer,Type\n"

    # Firmware type categories.
    cat_storage = ['Array Controller Firmware', 'Disk Drive Firmware']
    cat_nic = ['Ethernet Port Controller Firmware']
    cat_system = ['System Firmware']
    cat_management = ['HP Management Processor Firmware', 'HP Server Blade Enclosure Firmware']

    # Component Update variables
    
    
    def __init__(self, host, vc_uuid):
        self.update_url_queue = []
        self.fw_update_sem = Semaphore()
        self.job_queue_sem = Semaphore()
        WBEMClient.__init__(self, host, vc_uuid)

    def discover_firmware(self):

        # Make the WBEM call to get our firmware list.
        instances = self.get_class_instances([self.FIRMWARE_CLASS])
        components = []

        if instances and self.FIRMWARE_CLASS in instances:
            instances = instances[self.FIRMWARE_CLASS]

            # We only want to display firmware components with classifications of 10 or 11.
            cls_list = [inst for inst in instances if 10 in instances[inst]['Classifications'] or 11 in instances[inst]['Classifications']]
            for cls in cls_list:
                component = {}
                component['Name'] = instances[cls]["Name"]
                component['Version'] = instances[cls]["VersionString"]
                component['FirmwareStatus'] = instances[cls]["StatusDescriptions"]
                component['Description'] = instances[cls]["Description"]
                component['Manufacturer'] = instances[cls]["Manufacturer"]
                component['ClassificationID'] = self.determine_classification(instances[cls]["ClassificationDescriptions"])
                component['Classification'] = instances[cls]["ClassificationDescriptions"]
                components.append(component)

        return components

    # Determines the type of firmware for a component. Uses the WBEM
    # ClassificationDescriptions field to determine the type.
    def determine_classification(self, classification_description):
        if not classification_description:
            return UNKNOWN

        if classification_description[0] in self.cat_storage:
            return STORAGE
        elif classification_description[0] in self.cat_nic:
            return NIC
        elif classification_description[0] in self.cat_system:
            return SYSTEM
        elif classification_description[0] in self.cat_management:
            return MANAGEMENT
        else:
            return UNKNOWN

    # Returns components in the overall system category (ROM, Storage, NIC).
    # The components are sorted so they can be displayed in the correct order
    # on the page.
    def get_components_system(self, components):
        component_list = [comp for comp in components if comp['ClassificationID'] in system_ids]
        def fw_prio(component):
            if component['ClassificationID'] == SYSTEM:
                return 0
            elif component['ClassificationID'] == STORAGE:
                return 1
            elif component['ClassificationID'] == NIC:
                return 2
            else:
                return 3
        component_list.sort(lambda a,b: fw_prio(a)-fw_prio(b))
        return component_list or None

    # Gets a list of the management firmware components (OA, iLO)
    def get_components_management(self, components):
        component_list = [comp for comp in components if comp['ClassificationID'] in management_ids]
        return component_list or None

    def get_components(self):
        comp_obj = {}
        components = self.discover_firmware()

        comp_obj['System'] = self.get_components_system(components)
        comp_obj['Management'] = self.get_components_management(components)

        return comp_obj

    # Sends a command to the host to update a single smart component. The package_url parameter
    # specifies a URL where the smart component package can be found.
    def _update_host_component(self, package_url, options):

        installOptions = []
        installOptionValues = []
        for option in options:
            option_val = pywbem.Uint16(option)
            installOptions.append(option_val)
            installOptionValues.append('')

        in_params = {'URI': package_url, 'InstallOptions': installOptions, 'InstallOptionValues': installOptionValues}

        results = self.invoke_method(method_name='InstallFromURI', class_names=['SMX_SCInstallationService'], in_params=in_params)
        # We only requested one class, so our result is the first element.
        if results and results[0]:
            result = results[0]
            resultObj = {}
            resultObj['status'] = result['status']

            # In order to make this object serializable for JSON, we need to convert the out_params to a string using repr().
            resultObj['message'] = repr(result['out_params'])

            # Check for job creation (4096). It may not actually have been successful, so we need to get
            # the job and check its status.
            if result['status'] == 4096:
                resultObj['message'] = UPDATE_CODES[4096]
            else:
                if result['status'] in UPDATE_CODES:
                    resultObj['message'] = UPDATE_CODES[result['status']]
                else:
                    resultObj['message'] = ''

            return resultObj
        return None

    # Returns a JSON representation of the components list.
    def json(self):
        firmware_obj = {}
        components = self.get_components()

        if not self.last_error:
            firmware_obj = components
        else:
            firmware_obj['Error'] = self.last_error

        firmware_obj['isESXi'] = self.is_ESXi_host()
        return json.dumps(firmware_obj)

    # Returns a list of the components in CSV format. If types are specified, only
    # components that match the types in the array are returned.
    def csv(self, include_header=True, types=[]):
        csv_str = ""
        if include_header:
            csv_str = self.CSV_FMT_HEADER
        host = self.host

        components = self.get_components()

        components_system = components['System']
        components_management = components['Management']
        if components_system:
            for component in components_system:
                type = component['Classification'][0]
                if (not types) or (types and type in types):
                    csv_str += self._get_component_csv(component, host)
                    # We only want the host to appear on the first line of the CSV output.
                    host = ""

        if components_management:
            for component in components_management:
                type = component['Classification'][0]
                if (not types) or (types and type in types):
                    csv_str += self._get_component_csv(component, host)
                    # We only want the host to appear on the first line of the CSV output.
                    host = ""

        return csv_str

    def _get_component_csv(self, component, host):
        csv_fmt = "%s,%s,%s,%s,%s,%s\n"
        comp_csv = csv_fmt % (
            host,
            component['Name'],
            component['Description'],
            component['Version'],
            component['Manufacturer'],
            component['Classification'][0]
        )
        return comp_csv

    def get_firmware_status(self):
        status_obj = {}
        results = self.invoke_method(method_name='GetDetailedStatus', class_names=['SMX_SCInstallerConcreteJob'])
        return results and results[0] or None

    def get_firmware_jobs(self):

        self.job_queue_sem.acquire()
        queue = [job for job in self.update_url_queue if not job['run'] and not job['skip']]
        self.job_queue_sem.release()

        def jobdate(job):
            try:
                return int(time.mktime(time.strptime(str(job._CIMDateTime__datetime), '%Y-%m-%d %H:%M:%S+00:00')))
            except:
                return 0

        instances = self.get_class_instances([self.SC_JOB_CLASS])
        if instances and self.SC_JOB_CLASS in instances:
            instances = instances[self.SC_JOB_CLASS]

            # Sort based on the time the job was started.
            jobs = []
            for job in instances:
                jobs.append(instances[job])
            jobs.sort(lambda a,b: jobdate(b['StartTime']) - jobdate(a['StartTime']))

            jobs_list = []
            for job in jobs:
                job_obj = {}
                for prop in job:
                    # Filter out date/time objects since the are not JSON serializable.
                    if not isinstance(job[prop], pywbem.CIMDateTime):
                        job_obj[prop] = job[prop]
                jobs_list.append(job_obj)

            job_obj = {
                'jobs': jobs_list,
                'queue': queue
            }
            return job_obj
        else:
            if not self.last_error:
                return {'jobs':[], 'queue':queue}
            else:
                return {'Error':self.last_error}

    def queue_firmware_job(self, package_url, options):

        # Validate the URL. We currently only accept http URLs.
        # If the URL doesn't begin with HTTP, we'll check to see if the user wants to update with
        # a managed Smart Component. If that fails, return an appropriate error.
        parse_result = urlparse(package_url)
        if parse_result.scheme != 'http':
            if package_url in get_scm().get_sc_file_list():
                cfg = config()
                uim_root = cfg.get_uim_root()
                host = urlparse(uim_root).hostname                

                package_url = 'http://'+host+':63000/static/sc_share/'+package_url
            else:
                return {'status':'-1', 'message':'Invalid HTTP URL.'}

        job_obj = {
            'package_url': package_url,
            'options': options,
            'run': False,
            'skip': False,
            'retry_count': 0
        }

        job = [job for job in self.update_url_queue if job['package_url'] == package_url and not job['run'] and not job['skip']]
        if not job:

            self.job_queue_sem.acquire()
            self.update_url_queue.append(job_obj)
            self.job_queue_sem.release()

            # Only start the thread if the semaphore is unlocked.
            if self.fw_update_sem.acquire(False):
                log.debug("Starting firmware job thread on host %s" % (self.host))
                self.job_thread = Thread(name="poll.firmware_jobs", target=self.run_jobs)
                self.job_thread.daemon = True
                self.job_thread.start()
            else:
                log.debug("Firmware thread already running on host %s" % (self.host))

            return self.get_firmware_jobs()
        else:
            return {'status':'-1', 'message':'The requested job is already in the queue.'}

    def run_jobs(self):

        if len(self.update_url_queue) == 0:
            log.debug("URL queue is empty. Exiting run jobs for host %s" % (self.host))
            self.fw_update_sem.release()
            return

        # Cause a delay before starting so queued job status can be returned.
        sleep(5)

        i = 0
        while True:
            if i == len(self.update_url_queue):
                break;

            log.debug("Acquiring job queue semaphore on host %s" % (self.host))
            self.job_queue_sem.acquire()
            log.debug("Job queue semaphore acquired on host %s" % (self.host))
            job = self.update_url_queue[i]
            if not job['run'] and not job['skip']:
                try:
                    # Try to run the job
                    result = self._update_host_component(job['package_url'], job['options'])
                    if result and 'status' in result:
                        status = result['status']
                    else:
                        # If something bad happened here, we need to stop the process and bail.
                        # Error information will be returned to the client on the next call from self.last_error.
                        log.debug("No valid result found. Stopping update process on host %s" % (self.host))
                        self.job_queue_sem.release() 
                        break

                    # Job is in progress. Wait for 30 seconds and try again.
                    if status == 6:
                        self.job_queue_sem.release() 
                        sleep(30)
                    # Missing dependencies - move the job to the end of the queue and try it again.
                    elif status == 4105:
                        if job['retry_count'] == 0:
                            self.update_url_queue.pop(i)
                            job['retry_count'] += 1
                            self.update_url_queue.append(job)
                            self.job_queue_sem.release() 
                        else:
                            job['run'] = True
                            self.job_queue_sem.release() 
                            i+=1
                            
                    # The job was created successfully.
                    elif status == 4096:
                        job['run'] = True
                        self.job_queue_sem.release() 
                        i+=1
                    else:
                        # TODO: Not sure if other status' will be reflected here or in the job status. Need a real component to know for sure.
                        job['run'] = True
                        self.job_queue_sem.release() 
                        i+=1
                except Exception as err:
                    # Make sure we don't deadlock if we encounter an exception.
                   self.job_queue_sem.release()
                # Wait a minute in case the GUI is requesting data.
                sleep(3)
            else:
                self.job_queue_sem.release()
                i+=1

        # Clear the queue and release the semaphore after we are done running jobs.
        self.update_url_queue = []
        self.fw_update_sem.release()

    # Removes a job from the queue by setting the 'skip' flag. We do this instead of actually
    # removing the job since the run_jobs function might be looping through the array when we
    # delete a job.
    def delete_queued_job(self, job_url):
        self.job_queue_sem.acquire() 
        found = False
        for job in self.update_url_queue:
            # We can't delete a running job. There is a window where the user can interact with the UI
            # and try to delete the job after it has been started by the firmware_jobs thread.
            if job['package_url'] == job_url and not job['run']:
                job['skip'] = True
                found=True
                break
        self.job_queue_sem.release()
        if not found :
            return None
        return self.get_firmware_jobs()
