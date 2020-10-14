#
# deployment_connector.py
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
#    Mohammed M. Islam
# 
# Description:
#    AresLite deployment connector class. Connects to an RDP instance using the 
#    AresLite SOAP web service.
#
#    mxnodesecurity -a -p dsc_rdp -c userid:pw -n <ip-address>
#
#####################################################

from util import catalog
from util.config import config, crypy
#from util import config, catalog, password
#from util.error import error
from suds.client import Client
from suds import WebFault
import suds
import suds.sax.parser
import suds.umx.basic
from logging import getLogger
from threading import Thread, Semaphore
from M2Crypto import SSL
#from vmware import VirtualCenter
#from hp.proliant.cluster import Cluster
from time import sleep
from util.resource import resource
from vmware.vcenter import vCenter, ManagedObjectRef

log = getLogger(__name__)

JOB_STATUS_MESSAGES = {
    '-5': 'Job Removed',
    '-4': 'Unknown',
    '-3': 'Not scheduled',
    '-2': 'Scheduled but not started',
    '-1': 'In progress',
    '0': 'Successful',
    '1': 'Failed'
}

DC_STATUS_NO_SIM = 0
DC_STATUS_NO_CREDENTIALS = 1
DC_STATUS_NO_ARESLITE = 2
DC_STATUS_UNKNOWN = 3
DC_STATUS_LOGIN_FAILED = 4
DC_STATUS_INSUFFICIENT_PRIVILEGE = 5
DC_STATUS_CONNECT_FAILED = 6
DC_STATUS_DC_CREDENTIALS = 7
DC_STATUS_DEPLOYMENT_RUNNING = 8
DC_STATUS_DEPLOYMENT_FINISHED = 9
DC_STATUS_CONNECTION_LOST = 10

DC_MESSAGES = (
    'HP SIM settings must be correctly configured before deployment can be used.',
    'HP SIM credentials must be configured before deployment can be used.',
    'The HP Deployment Connector must be installed on the HP SIM server.',
    'An unknown error occurred while contacting the deployment server.',
    'Deployment server login failed. Please check the HP SIM credentials.',
    'The HP SIM credentials supplied do not have sufficient privileges to access the deployment server.',
    'HP Insight Control for vCenter was unable to connect to HP SIM.',
    'The deployment connector username and password is has not been created in HP SIM.',
    'Deployment is in progress.',
    'The deployment process has finished.',
    'Insight Control for vCenter has lost connection to the deployment server. Please check the deployment console.',
)

# This is a list of errors from the AresLite spec. The messages have been updated to be more user-friendly.
DC_ERRORS = {
    '1020':'Insufficient privileges',
    '1021':'Invalid logon token',
    '1022':'Invalid session',
    '1111':'Bad deployment server type',
    '1112':'Bad deployment server IP address',
    '1100':'No deployment server',
    '1101':'Deployment server non-responsive',
    '1102':'Deployment server timeout',
    '1105':'Deployment server access problem',
    '1106':'Deployment server returned nothing',
    '1107':'Deployment server credentials not found',
    '1110':'Deployment server credentials were not accepted',
    '1111':'Bad deployment server type',
    '1112':'Bad deployment server IP address',
    '1150':'Deployment server xml format error',
    '1151':'Deployment server parsing error',
    '1152':'Deployment server unexpected error',
    '1300':'Target system not found',
    '1301':'Target system not responsive',
    '1302':'Target system UUID bad',
    '1307':'Target system UUID in bad format',
    '1330':'Target system in wrong lifecycle state',
    '1400':'Folder not found',
    '1402':'An empty folder was submitted',
    '1403':'Some jobs in the folder encountered a problem',
    '1404':'All jobs in the folder encountered a problem',
    '1550':'Bad folder ID',
    '1600':'Bad personalization data',
    '1601':'Personalization data was ignored by the deployment server',
    '1703':'Schedule ID could not be created',
    '1900':'Input parameter bad',
    '1910':'Input parameter bad',
    '1911':'Cannot resolve deployment server',
    '2000':'Unknown error',
    '2001':'Unexpected error',
    '2010':'Interface not supported by access layer',
    '2020':'Deployment connector doesn\'t support deployment server version'
}

ADDTOVC_NOT_STARTED = 'Not started'
RECONFIGURE_NOT_SPECIFIED = 'Reconfigure not specified'

from suds.plugin import MessagePlugin

class MyPlugin(MessagePlugin):
    def sending(self, context):
        #log.debug( context.envelope )
        pass
        
    def marshalled(self, context):
        #log.debug("***************** marshalled Before Pruning")
        #log.debug( context.envelope )
        context.envelope[1].prune()
        #log.debug("***************** marshalled After Pruning")
        #log.debug( context.envelope )
        
    def parsed(self, context):
        #log.debug("----------------- parsed Before Pruning")
        #log.debug( context.reply )
        context.reply.prune()
        #context.reply.getChild('dnsSuffixes').prune()
        #context.reply.getChild('winsServers').prune()
        #context.reply.getChild('dnsServers').prune()
        #log.debug("----------------- parsed Received After Pruning")
        #log.debug( context.reply )
        
class ALDeploymentConnector:

    def __init__(self):
        self._client = None
        self.sk = None
        self.host = None
        self.deployment_sem = Semaphore(value=1)
        self.monitor_sem = Semaphore(value=1)
        self.failures = 0
        # Deployment server type - right now all we support is "RDP"
        self.ds_type = "RDP"
        # Keep track of what state we are in (i.e. no SIM config, no credentials, no AL connector).
        self.dc_status = None

        # Items that we want to keep track of from the service. It takes a while to get information,
        # so we need to cache some data locally.
        self.managedNodes = []
        self.jobFolders = None

        # TODO: Get SIM/RDP info from config file.
        #sim_cfg = config.get().sim
        cfg = config()
        srvcfg = cfg.get_server_config()
        self.simport = srvcfg['hpsim']['port']
        self.simpw = cfg.get_simpw()
        if self.simpw and self.simport:
            self.host = self.simpw['host']
        #if sim_cfg and 'host' in sim_cfg[0]:
        #    self.host = sim_cfg[0].host
        else:
            # We can't go any further without a SIM host.
            self.dc_status = DC_STATUS_NO_SIM
            self._dc_error("SIM not configured.")
            return

        try:
            # Import the WSDL and create the service.
            wsdl = 'file://' + resource('Alc1_0.wsdl')
            #wsdl = ALDeploymentConnector.alc_service(self.host, str(int(self.simport) + 1)) + '?wsdl'
            log.debug(wsdl)
            #self._client = Client(wsdl)
            self._client = Client(wsdl, plugins=[MyPlugin()])

            service_url = ALDeploymentConnector.alc_service(self.host, str(int(self.simport) + 1))
            self._client.set_options(location=service_url)

            self.login()
            if self.sk:
                log.info("calling discoverDeploymentServer")
                self.discoverDeploymentServer()
                log.info("Deployment server adapter created.")

        except Exception as err:
            self.dc_status = DC_STATUS_UNKNOWN
            self._dc_error(err)

    def getDcStatus(self):
        obj = {}
        # First check to see if deployment is running.
        if self.deployment_running():
            self.dc_status = DC_STATUS_DEPLOYMENT_RUNNING 
        obj['errno'] = self.dc_status
        try:
            obj['message'] = DC_MESSAGES[self.dc_status]
        except:
            obj['message'] = ''
        return obj

    @staticmethod
    def alc_service(host, port='50001'):
        return ("https://%s:%s/mxsoap/services/Alc1_0" % (host, port))

    def _dc_error(self, obj):
        try:
            #TODO            
            log. error("Deployment Connector Error: %s", (obj))
        except:
            pass

    def destroy(self):
        catalog.remove(self)
        try:
            self._client.service.logout(self.sk)
        except Exception as ex:
            pass

    @staticmethod
    def create():
        def new():
            dc = [x for x in catalog.get_all() if isinstance(x, ALDeploymentConnector)]
            if dc:
                # Don't do anything if deployment is running.
                if dc[0].deployment_running():
                    return
                dc[0].destroy()
            log.info("Creating ALDeploymentConnector()")
            dc = ALDeploymentConnector()
            url = ALDeploymentConnector.alc_service(dc.host, dc.simport)
            catalog.insert(dc, url)
        t = Thread(target=new)
        t.daemon = True
        t.start()

    # AresLite web service methods #
    
    def discoverDeploymentServer(self):
        log.info("discoverDeploymentServer(), calling getJobFolders()")
        self.getJobFolders()
        self.getManagedNodes()
        #for node in self.managedNodes:
        #    self.getManagedNodeDetails(node['uuid'])

    def _get_node(self, uuid):
        try:
            node = [x for x in self.managedNodes if x['uuid'] == uuid]
            return node[0]
        except:
            return None
            

    def login(self):
        #TODO: SIM credentials from settings?
        #entry = [x for x in config.get().sim if x.host == self.host][0]
        #if (not entry.enabled):
        #    return

        #user = entry.username or '*'
        #pw = password.find(user, entry.host, password.SIM)
        # TODO
        cfg = config()
        simpw = cfg.get_simpw()
        try:
            # TODO
            #loginResult = self._client.service.login(pw.username, pw.password)
            loginResult = self._client.service.login(simpw['username'], crypy().decode(simpw['epassword']))
            if loginResult.returnCode == 0:
                self.sk = loginResult.token
            elif loginResult.returnCode in (1001, 1010, 1011):
                self.dc_status = DC_STATUS_LOGIN_FAILED 
            elif loginResult.returnCode == 1020:
                self.dc_status = DC_STATUS_INSUFFICIENT_PRIVILEGE
            else:
                self.dc_status = DC_STATUS_UNKNOWN

        except Exception as ex:
            # Check to see if we received a soap fault back from the service.  This means that we are probably talking
            # to a SIM server, but we don't have the deployment connector installed.
            if hasattr(ex, 'fault'):
                self.dc_status = DC_STATUS_NO_ARESLITE
                self._dc_error(ex.fault.faultstring)
                log.error('Unable to login to SIM due to fault: %s', ex.fault.faultstring)
                return
                
            if hasattr(ex, 'reason'):
                self.dc_status = DC_STATUS_CONNECT_FAILED
                self._dc_error(ex.reason)
                log.error('Unable to login to SIM: %s', ex.reason)
                return
                
            log.exception('Unable to login to SIM')    
            self.dc_status = DC_STATUS_UNKNOWN

    def _parseJobFolders(self, folders):
        self.jobFolders = []
        for folder in folders:

            # Filter out the default RDP top-level folders. These folders cannot be run.
            if folder.name in ("System Jobs", "HP Deployment Toolbox"):
                continue

            folder_obj = {}
            folder_obj['name'] = folder.name
            folder_obj['id'] = folder.id

            # TODO: Saved folders need to go in the config file.
            #saved_folders = config.get().rdpjobfolders
            #saved_folder = [x for x in saved_folders if x['name']==folder.name]
            saved_folder = None
            if saved_folder:
                saved_folder = saved_folder[0]
                folder_obj['type'] = saved_folder['type']
            else:
                if folder.name.lower().find('esxi') > -1:
                    folder_obj['type'] = 'ESXi'
                else:
                    folder_obj['type'] = 'ESX'

            self.jobFolders.append(folder_obj)

    def getJobFolders(self, refresh=False):
        if self._client == None or self.sk == None:
            return None
        try:
            if not self.jobFolders or refresh:
                #import logging
                #cur_level = logging.getLogger('suds').level
                #logging.getLogger('suds').setLevel(10)
                response = self._client.service.listFolders(self.sk, self.host, self.ds_type)
                log.debug("got listFolders()")
                log.debug(response)
                #logging.getLogger('suds').setLevel(cur_level)
                if response.returnCode == 0:
                    log.debug('Parsing jobFolders')
                    self._parseJobFolders(response.folders)
                elif response.returnCode in (1021, 1022):
                    self.login()
                    return self.getJobFolder(refresh)
                else:
                    if hasattr(response, 'errorCode'):
                        self._dc_error(response.errorCode)
            return self.jobFolders
        except WebFault as wf:
            self._dc_error(wf)
            return None

    # This function is called from the wizard - it updates the folder types based on the user's selection.
    def updateJobFolders(self, folders):
        if folders:
            self.jobFolders = folders
            for folder in folders:
                # TODO
                #config.get().update_rdp_folder(folder)
                pass

            # Remove any config references to folders that no longer exist.
            """
            for folder in config.get().rdpjobfolders:
                f = [x for x in self.jobFolders if x['name'] == folder.name]
                if not f:
                    config.get().rdpjobfolders.remove(folder)

            config.save()
            """

    def _parseNodeList(self, nodeList):
        # Wipe out the current list so we can rebuild.
        self.managedNodes = []
        for node in nodeList:
            entry = {}
            entry['uuid'] = node.uuid
            entry['name'] = node.name
            entry['id'] = node.id
            entry['jobs'] = []
            entry['personalization'] = None
            entry['overall_job_status'] = 0
            entry['add_to_vcenter'] = ADDTOVC_NOT_STARTED
            entry['reconfigure'] = {'id':-1, 'message':RECONFIGURE_NOT_SPECIFIED}
            self.managedNodes.append(entry)

    def _parseNodeDetails(self, details):
        log.debug("Detail: %s", details)
        node = self._get_node(details['uuid'])
        log.debug(node)
        if not node:
            return

        node['details'] = {}
        node['details']['processorArchitecture'] = details.processorArchitecture
        node['details']['processorCount'] = details.processorCount
        node['details']['processorDescription'] = details.processorDescription
        node['details']['processorSpeed'] = details.processorSpeed

        node['details']['networkInterfaces'] = []
        interfaces = node['details']['networkInterfaces']
        try:
            for netif in details.networkInterfaces:
                if_details = {}
                if_details['id'] = netif.id
                if_details['dhcp'] = netif.dhcp
                if_details['dnsDomain'] = netif.dnsDomain
                if_details['ipAddress'] = netif.ipAddress
                if_details['ipMask'] = netif.ipMask
                if_details['macAddress'] = netif.macAddress

                if getattr(netif, 'dnsServers', None):
                    if_details['dnsServers'] = netif.dnsServers.dnsServers
                else:
                    if_details['dnsServers'] = []

                if getattr(netif, 'gateways', None):
                    if_details['gateways'] = netif.gateways.gateways
                else:
                    if_details['gateways'] = []

                interfaces.append(if_details)
        except:
            log.error('error parsing networkInterfaces', exc_info=1)

    def getManagedNodes(self, refresh=False):
        if self._client == None or self.sk == None:
            return None

        if not self.managedNodes or refresh:
            try:
                result = self._client.service.listManagedNodesPerDS(self.sk, self.host, self.ds_type)
                if result.returnCode == 0:
                    nodeList = result.nodeList
                    self._parseNodeList(nodeList)
                    for node in self.managedNodes:
                        self.getManagedNodeDetails(node['uuid'])
                elif result.returnCode in (1021, 1022):
                    self.login()
                    return self.getManagedNodes(refresh)
                else:
                    if hasattr(result, 'errorCode'):
                        self._dc_error(result.errorCode)
                        #if result.errorCode == 1021:
                        #    self.dc_status = DC_STATUS_DC_CREDENTIALS
            except:
                log.error('error getManagedNodes', exc_info=1)
                pass

        return self.managedNodes

    def getManagedNodeDetails(self, uuid):
        if self._client == None:
            return None
        try:
            details = self._client.service.getManagedNodeDetails(self.sk, self.host, self.ds_type, uuid)
            self._parseNodeDetails(details);
            return details
        except Exception as ex:
            log.info('exception: ', exc_info=1)
            return None

    # This function calls submitFolder in a way that only sends networking information and no folder name.
    def reconfigure(self, uuid, personalization, os_type):
        node = self._get_node(uuid)
        try:
            personalizationData = self._getPersonalizationXML(uuid, personalization, os_type)
            result = self._client.service.submitFolder(self.sk, self.host, self.ds_type, None, uuid, None, personalizationData)
            if result.returnCode == 0:
                log.debug('Submitted reconfigure...')
                node['reconfigure'] = {'id':result.scheduleIds[0], 'message':JOB_STATUS_MESSAGES['-1']}
            # Invalid token or session.  Re-login and try the call again.
            elif result.returnCode in (1021, 1022):
                log.debug('Error submitting reconfigure...Re-logging...')
                self.login()
                return self.reconfigure(uuid, personalization, os_type)
            else:
                log.debug('Reconfigure failed...')
                node['reconfigure'] = {'id':-1, 'message':'Reconfigure failed (%d)' % (result.returnCode)}
                node['overall_job_status'] = 1

            return result

        except WebFault as wf:
            log.info('exception in reconfigre: ', exc_info=1)
            node['overall_job_status'] = 1
            self._dc_error(wf)
            return None

    def submitFolder(self, folderName, uuid):
        if self._client == None:
            return None
        try:
            result = self._client.service.submitFolder(self.sk, self.host, self.ds_type, None, uuid, folderName, None)
            if result.returnCode == 0 and hasattr(result, 'scheduleIds'):
                node = self._get_node(uuid)
                job = {}
                job['schedule_id'] = result.scheduleIds[0]
                job['folder_name'] = folderName
                job['finished'] = False
                node['jobs'].append(job)
            # Invalid token or session.  Re-login and try the call again.
            elif result.returnCode in (1021, 1022):
                self.login()
                return self.submitFolder(folderName, uuid)

            return result

        except WebFault as wf:
            self._dc_error(wf)
            return None

    # Gets an XML string for the PersonalizationData parameter of the submitFolder and submitFolders calls.
    def _getPersonalizationXML(self, uuid, personalizationData, os_type):

        px_fmt = """
<root>
  <computer id="%s" name="%s">
  %s
  <hostname>%s</hostname>
  <nics>%s</nics>
  </computer>
</root>
        """

        nic_fmt_static = """
  <nic id="%s">
    <dhcp>No</dhcp>
    <ipaddress>%s</ipaddress>
    <mask>%s</mask>
    %s
    %s
    %s
  </nic>
        """

        nic_fmt_dhcp = """
  <nic id="%s">
    <dhcp>Yes</dhcp>
  </nic>
        """

        node = self._get_node(uuid)
        if not node:
            return ""

        data = personalizationData
        if not data:
            return ""

        nics = []
        for nic in data['nics']:
            nics.append(data['nics'][nic])
        nics.sort(lambda a,b: int(a['id'])-int(b['id']))

        if data['dnsdomain'] != '':
            domain_str = '<dnsdomain>%s</dnsdomain>' % (data['dnsdomain'])
        else:
            domain_str = ''

        nic_str = ""
        for nic in nics:
            if not nic:
                continue
            if nic['useDhcp']:
                nic_str += nic_fmt_dhcp % (nic['id'])
            else:
                ip = nic['ipAddress']
                netmask = nic['netmask']

                if nic['gateway'] and nic['gateway'] != "":
                    gateway = '<gateway>'+nic['gateway']+'</gateway>'
                else:
                    gateway = ""

                if nic['dns']:
                    dns = '<dns>'+','.join(nic['dns'])+'</dns>'
                else:
                    dns = ""

                nic_str += nic_fmt_static % (nic['id'], ip, netmask, gateway, dns, domain_str)

        agent_str = ''
        if os_type == 'ESXi':
            agent_str = "<agent>no</agent>"

        px_str = px_fmt % (node['id'], node['name'], agent_str, data['hostname'], nic_str)
        log.debug("Personalization for node %s: %s" % (uuid, px_str))
        return px_str

    def _parseScheduleStatus(self, status):
        status_items = []
        for item in status:
            # Hack for bad RDP information
            if item.status == -3:
                item.status = -1
            status_item = {}
            status_item['id'] = getattr(item, 'id', '')
            status_item['status'] = getattr(item, 'status', '')
            status_item['error_text'] = getattr(item, 'errorText', '')
            status_item['failure_mode'] = getattr(item, 'failureMode', '')
            if str(item.status) in JOB_STATUS_MESSAGES:
                status_item['status_message'] = JOB_STATUS_MESSAGES[str(getattr(item, 'status', ''))]
            else:
                status_item['status_message'] = "Unknown"

            status_items.append(status_item)
        return status_items

    def getScheduleStatusX(self, schedule_ids):

        if self._client == None:
            return None

        #ids = self._client.factory.create("ArrayOf_xsd_string")
        #ids.value = schedule_ids
        ids = [schedule_ids]

        try:
            result = self._client.service.getScheduleStatusX(self.sk, self.host, self.ds_type, ids)
            if result and result.returnCode == 0:
                return self._parseScheduleStatus(result.scheduleStatusSet)
            elif result.returnCode in (1021, 1022):
                self.login()
                return self.getScheduleStatusX(schedule_ids)
            return result
        except WebFault as wf:
            log.info('exception in getScheduleStatusX: ', exc_info=1)
            self._dc_error(wf)
            return None

    def getNodeScheduleStatus(self, uuid):
        status_list = []
        node = self._get_node(uuid)
        if not node:
            return None
        for job in node['jobs']:
            if not 'schedule_id' in job or job['finished']:
                continue
            job_obj = {}
            job_obj['folder_name'] = job['folder_name']
            status = self.getScheduleStatusX(job['schedule_id'])
            if status and type(status).__name__=='list':
                job_obj['status'] = status[0]
                status_list.append(job_obj)
                # Update the job status
                job['status'] = status[0]
            else:
                log.debug("Failed getNodeScheduleStatus: %d" % (status.returnCode))
                if status.returnCode == 1701:
                    # Manually set the job's status to removed.
                    job['status']['status'] = -5
                    job['status']['status_message'] = JOB_STATUS_MESSAGES['-5']
                    job['finished'] = True
                    node['overall_job_status'] = 1
        log.debug('Returning from getNodeScheduleStatus')
        return status_list

    def _getHostThumbprint(self, host, port=443, md='sha1'):
        try:
            ssl = SSL.Connection(SSL.Context())
            ssl.postConnectionCheck = None
            
            try:
                ssl.socket.settimeout(None)
                ssl.connect((host, port))
            except Exception as ex:
                log.exception('Unable to connect to host %s', host)
                return ""
            
            try:
                fp = ssl.get_peer_cert().get_fingerprint(md)
            except Exception as ex:
                log.exception('Unable to get certificate and fingerprint %s', host)
                ssl.close()
                return ""
            
            # Got certification without exception, now close the connection
            ssl.close()

            # Sometimes the leading zero is dropped which causes an Odd-length string exception.
            # When this happens we'll pad the beginning of the string with a zero.

            if len(fp) > 0 and len(fp) % 2 != 0:
                fp = '0'+fp

            # Return the fingerprint as colon separated hex digits
            return ':'.join(['%02x'%ord(x) for x in fp.decode('hex')])
        except Exception as ex:
            log.exception("Exception processing thumbprint for host %s", host)
            return ""

    def _getClusterMob(self, name):
        vc = self._getvCenter()
        clusters = vc.retreive_cluster_list()
        for cluster in clusters:
            try:
                for prop in cluster.propSet:
                    if prop.name == 'name' and prop.val == name:
                        return cluster.obj
            except:
                pass
        return None

    def _getDatacenterMob(self, name):
        vc = self._getvCenter()
        datacenters = vc.retrieve_datacenter_list()
        for dc in datacenters:
            try:
                for prop in dc.propSet:
                    if prop.name == 'name' and prop.val == name:
                        dc_host_folder = vc.retrieve_dc_host_folder(dc.obj.value)
                        log.debug(dc_host_folder)
                        return dc_host_folder
            except:
                log.exception('Error iterating throught the propSet')

        return None

    def _getvCenter(self):
        vc = [vc for vc in catalog.get_all() if isinstance(vc, vCenter)]
        return vc and vc[0] or None

    def _getHostConnectSpec(self, host, thumbprint, username=None, password=None, vc_username=None, vc_password=None):

        vc = self._getvCenter()

        spec = vc.client.factory.create("ns0:HostConnectSpec")
        spec.userName = username
        spec.password = password
        spec.hostName = host
        delattr(spec, 'vmFolder')

        # vCenter credentials
        spec.vimAccountName = vc_username
        spec.vimAccountPassword = vc_password
        spec.force = True
        spec.sslThumbprint = thumbprint

        return spec

    def addHostToVCenter(self, host, uuid="", cluster="", esx_credentials=None):
        isCluster = True
        # TODO: Correct vCenter by UUID
        #vc = [vc for vc in catalog.get_all() if isinstance(vc, VirtualCenter) and vc.uuid.lower()==uuid.lower()]
        vc = self._getvCenter()
        if not vc:
            log.debug("vCenter instance not found. Returning None.")
            return None

        # First see if the user selected a cluster.
        mob = self._getClusterMob(cluster)
        if not mob:
            log.debug("Cluster entry not found for %s. Checking for datacenter." % (cluster))
            # If we didn't find a cluster, look for a datacenter entry.
            mob = self._getDatacenterMob(cluster)
            if not mob:
                log.debug("Datacenter entry not found. Returning.")
                return None
            isCluster = False

        thumbprint = self._getHostThumbprint(host)
        if thumbprint == "":
            log.debug("Failed to get SSL thumbprint from host %s" % (host))
            return None

        if esx_credentials == None:
            # Default to root/password if the user didn't provide any credentials. These are the credentials
            # for a new ESX installation (not sure if this works for ESXi).
            esx_credentials = ("root", "password")

        # TODO: get correct vCenter password
        username = vc.decode(vc.username)
        password = vc.decode(vc.password)

        spec = self._getHostConnectSpec(host, thumbprint, esx_credentials[0], esx_credentials[1], username, password)
        # log.debug( spec )
        if not spec:
            log.debug("Spec returned none. Returning.")
            return None

        mob = ManagedObjectRef(mob._type, mob.value)
        if isCluster:
            return vc.vim.AddHost_Task(mob, spec, True)
        else:
            return vc.vim.AddStandaloneHost_Task(mob, spec, None, True)

    # Check to see if deployment is running by acquiring the semaphore.
    def deployment_running(self):
        if not self.deployment_sem.acquire(False):
            return True
        else:
            self.deployment_sem.release()
            return False

    # Loops through all jobs for each node and removes them.
    def clear_finished_jobs(self):
        for node in self.managedNodes:
            if node['jobs']:
                for job in node['jobs']:
                    node['jobs'].remove(job)
            node['add_to_vcenter'] = ADDTOVC_NOT_STARTED
            node['overall_job_status'] = 0
            node['reconfigure'] = {'id':-1, 'message':RECONFIGURE_NOT_SPECIFIED}
        self.monitor_sem.acquire()
        self.failures  = 0
        self.monitor_sem.release()
        self.dc_status = None

    # Task states: error, queued, running, success
    def monitorAddHostTasks(self, tasks, uuid):
        # TODO: Get the correct vCenter
        vc = self._getvCenter()
        if not vc:
            return None

        add_tasks = [task['result'].value for task in tasks]
        while True:
            vc_tasks = []
            # Create a new collector each time around.
            collector = vc.client.service.CreateCollectorForTasks(vc.sc.taskManager, None)
            while True:
                # Read tasks in 100 page blocks. The stupid vSphere API filtering doesn't work, so we have to just get everything.
                next_tasks = vc.client.service.ReadNextTasks(collector, 100)
                if not next_tasks:
                    break;
                vc_tasks = vc_tasks + next_tasks

            # Get only tasks we care about.
            vc_tasks = [task for task in vc_tasks if task.key in add_tasks]
            has_running = False
            for task in vc_tasks:
                log.debug( task )
                node_uuid = [t for t in tasks if task.key == task.key]
                if node_uuid:
                    node_uuid = node_uuid[0]['uuid']
                node = self._get_node(node_uuid)
                if task.state == 'running' or task.state == 'queued':
                    node['add_to_vcenter'] = 'In progress'
                    has_running = True
                elif task.state == 'error':
                    node['add_to_vcenter'] = task.error.localizedMessage
                elif task.state == 'success':
                    node['add_to_vcenter'] = 'Successful'
                else:
                    node['add_to_vcenter'] = 'Unknown'

            if not has_running:
                break
            else:
                sleep(30)

    def update_comm_loss(self):
        self.monitor_sem.acquire()
        self.failures += 1
        self.monitor_sem.release()
        log.debug("Comm loss value updated: %d" % (self.failures))

    # This function combines the submitFolder and the reconfigure above.  This is used so that we can run
    # the two calls in serial to accomodate the ESXi behavior.
    def deploy_esxi(self, uuid, folderName, deployment_obj, vcUuid):

        node = self._get_node(uuid)
        type = self._findJobType(folderName)
        exceptionCount = 0

        # For ESXi, the personalization must be sent before the deployment job is started.
        if deployment_obj['personalization']:
            log.debug("ESXi: Reconfiguring host %s" % (uuid))
            result = self.reconfigure(uuid, deployment_obj['personalization'], type)

        # Deploy the selected folder.
        result = self.submitFolder(folderName, uuid)
        if result and result.returnCode == 0:
            # Monitor the deployment job to completion.
            has_jobs = True
            while has_jobs:
                has_jobs = False
                try:
                    status = self.getNodeScheduleStatus(uuid)
                    log.debug(status)
                    for status_obj in status:
                        status_code = status_obj['status']['status']
                        log.debug("Status code for node %s: %d" % (uuid, status_code))
                        if status_code > -1:
                            for job in node['jobs']:
                                if job['schedule_id'] == status_obj['status']['id']:
                                    job['finished'] = True

                            node['overall_job_status'] = status_code
                        else:
                            has_jobs = True
                except Exception as ex:
                    log.debug('Exception in ESXi monitoring loop: %s' % (str(ex)))
                    exceptionCount += 1
                    if exceptionCount == 5:
                        log.debug("Encountered too many exceptions in ESXi monitor loop. Canceling.")
                        self.update_comm_loss()
                        return
                    # keep the loop going to try again
                    has_jobs = True

                if has_jobs:
                    log.debug("Sleeping 120 seconds.")
                    sleep(120)
        else:
            if result:
                log.debug("Folder submit for %s failed. Code: %d" % (folderName, result.returnCode))
                job = {}
                job['schedule_id'] = -1
                job['folder_name'] = folderName
                job['finished'] = True
                if str(result.returnCode) in DC_ERRORS: 
                    message = DC_ERRORS[str(result.returnCode)]
                else:
                    message = 'An unknown error occurred at the deployment server: %d' % (result.returnCode)
                job['error'] = {'errno':result.returnCode, 'message':message}
                node['jobs'].append(job)
            else:
                log.debug("Result is None for folder submit %s" % (folderName))

            # Don't add to vCenter if the submitFolder process failed.
            return

        if node['overall_job_status'] != 0:
            log.debug("Deployment failed. Exiting.")
            return

        # Now that the deployment job is finished, begin the add to vCenter process.  For ESXi, we have to
        # wait for a bit until the ESXi installation finishes.  RDP tells us the process is done before it
        # actually finishes.  We'll try and get the thumbprint for a while until we can contact the server.
        add_host_tasks = []
        try:
            if 'addToVCenter' in deployment_obj and deployment_obj['addToVCenter']['interface']:
                interface = deployment_obj['addToVCenter']['interface']
                if interface != '' and interface != 'DHCP':
                    log.debug("Adding to vCenter using: %s" % (interface))
                    if deployment_obj['addToVCenter']['username'] == "" and deployment_obj['addToVCenter']['password'] == "":
                        esx_credentials = None
                    else:
                        esx_credentials = (deployment_obj['addToVCenter']['username'], deployment_obj['addToVCenter']['password'])
                        log.debug("Credentials: %s, ************" % (deployment_obj['addToVCenter']['username']))

                    # Use the getThumbprint function to determine when the host is available. Try once every 5 minutes 6 times.
                    # (basically wait for 30 minutes for the ESXi installation to finish)
                    thumbprint = ''
                    count = 0
                    while thumbprint == '' and count < 6:
                        thumbprint = self._getHostThumbprint(interface)
                        if thumbprint == '':
                            node['add_to_vcenter'] = 'Waiting for ESXi host at %s to become available.' % interface
                            log.debug("Received empty thumbprint for host %s. Waiting for 5 minutes." % (uuid))
                            count = count + 1
                            sleep(60*5)

                    log.debug("Thumbprint for host %s after polling: %s" % (uuid, thumbprint))

                    if thumbprint != '':
                        log.debug("Before addHostToVCenter: %s, %s, %s" % (interface, vcUuid, deployment_obj['addToVCenter']['cluster_name']))
                        result = self.addHostToVCenter(interface, vcUuid, deployment_obj['addToVCenter']['cluster_name'], esx_credentials)
                        if result and result._type == 'Task':
                            add_host_tasks.append({'uuid':uuid, 'result':result})
                        else:
                            log.debug("Result: %s" % str(result))
                            node['add_to_vcenter'] = 'Error adding host %s to vCenter. The vCenter server did not start the task. The host must be added manually.' % (interface)
                    else:
                        log.debug("Thumbprint not found. Unable to add host to vCenter.")
                        node['add_to_vcenter'] = 'Unable to contact the host at %s. Add to vCenter aborted.' % (interface)
                else:
                    log.debug("Invalid interface for node %s: %s" % (uuid, interface))
                    if interface == 'DHCP':
                        node['add_to_vcenter'] = 'An interface configured for DHCP was specified. This host must be added to vCenter manually.'
                    else:
                        node['add_to_vcenter'] = 'No valid interface found. Add to vCenter aborted.'
            else:
                node['add_to_vcenter'] = 'No interface specified to add to vCenter.'

        except Exception as ex:
            node['add_to_vcenter'] = 'An error occurred while attempting to add host %s to vCenter. See the log for details.' % (interface)
            log.exception("Exception while attempting vCenter add: %s" % (str(ex)))
            raise ex

        # Monitor each of the add host tasks that were just started.
        self.monitorAddHostTasks(add_host_tasks, vcUuid)


    # Handles the ESX deployment process for a single node.
    def deploy_esx(self, uuid, folderName, deployment_obj, vcUuid):

        node = self._get_node(uuid)
        type = self._findJobType(folderName)
        exceptionCount = 0

        result = self.submitFolder(folderName, uuid)
        if result and result.returnCode == 0:
            # Monitor the deployment job to completion.
            has_jobs = True
            while has_jobs:
                has_jobs = False
                # Check to make sure that our main loop hasn't found a connection problem.
                if self.dc_status == DC_STATUS_CONNECTION_LOST:
                    log.debug("Connection lost - exiting ESX deployment thread.")
                    return 
                try:
                    status = self.getNodeScheduleStatus(uuid)
                    for status_obj in status:
                        status_code = status_obj['status']['status']
                        log.debug("Status code for node %s: %d" % (uuid, status_code))
                        if status_code > -1:
                            for job in node['jobs']:
                                if job['schedule_id'] == status_obj['status']['id']:
                                    job['finished'] = True

                            node['overall_job_status'] = status_code
                        else:
                            has_jobs = True
                except Exception as ex:
                    log.debug('Exception in ESX monitoring loop: %s' % (str(ex)))
                    exceptionCount += 1
                    if exceptionCount == 5:
                        log.debug("Encountered too many exceptions in ESX monitor loop. Canceling.")
                        return
                    # keep the loop going to try again
                    has_jobs = True

                if has_jobs:
                    log.debug("Sleeping 120 seconds.")
                    sleep(120)
        else:
            if result:
                log.debug("Folder submit for %s failed. Code: %d" % (folderName, result.returnCode))
                job = {}
                job['schedule_id'] = -1
                job['folder_name'] = folderName
                job['finished'] = True
                if str(result.returnCode) in DC_ERRORS: 
                    message = DC_ERRORS[str(result.returnCode)]
                else:
                    message = 'An unknown error occurred at the deployment server: %d' % (result.returnCode)
                job['error'] = {'errno':result.returnCode, 'message':message}
                node['jobs'].append(job)
            else:
                log.debug("Result is None for folder submit %s" % (folderName))

            # Don't add to vCenter if the submitFolder process failed.
            return

        if node['overall_job_status'] != 0:
            log.debug("Deployment failed. Exiting.")
            return
            
        try:
            # For ESX, the personalization must be sent after the image is deployed and the agent is available.
            if deployment_obj['personalization']:
                log.debug("ESX: Reconfiguring host %s" % (uuid))
                result = self.reconfigure(uuid, deployment_obj['personalization'], type)
                if result.returnCode == 0:
                    id = result.scheduleIds[0]
                    log.debug("Reconfigure successful for host %s (schedule ID: %s)" % (uuid, id))

                    reconfiguring = True
                    while reconfiguring:
                        log.debug("In reconfigure loop...")
                        status = self.getScheduleStatusX(id)
                        if status:
                            status = status[0]
                            if hasattr(status, 'returnCode'):
                                log.debug("Received non-zero return code from getScheduleStatus for ID %s: %d" % (id, status.returnCode))
                                reconfiguring = False
                            elif 'status' in status and status['status'] >= 0:
                                log.debug("Reconfigure finished for host %s" % (uuid))
                                reconfiguring = False
                            else:
                                log.debug("Waiting for reconfigure to finish (host %s)" % (uuid))
                                sleep(30)
        except:
            log.info('exception in deploy_esx, reconfigre section: ', exc_info=1)

        # Begin adding the host to vCenter. This process is now the same for ESX as it is for ESXi.
        # The 6.3.1 ESX agent introduced an additional reboot that is not tracked by RDP.  Therefore,
        # we have to wait for the host to appear on the network before we can add it.
        add_host_tasks = []
        try:
            if 'addToVCenter' in deployment_obj and deployment_obj['addToVCenter']['interface']:
                interface = deployment_obj['addToVCenter']['interface']
                if interface != '' and interface != 'DHCP':
                    log.debug("Adding to vCenter using: %s" % (interface))
                    if deployment_obj['addToVCenter']['username'] == "" and deployment_obj['addToVCenter']['password'] == "":
                        esx_credentials = None
                    else:
                        esx_credentials = (deployment_obj['addToVCenter']['username'], deployment_obj['addToVCenter']['password'])
                        log.debug("Credentials: %s, ************" % (deployment_obj['addToVCenter']['username']))

                    # Use the getThumbprint function to determine when the host is available. Try once every 5 minutes 6 times.
                    # (basically wait for 30 minutes for the ESX installation to finish)
                    thumbprint = ''
                    count = 0
                    while thumbprint == '' and count < 12:
                        thumbprint = self._getHostThumbprint(interface)
                        if thumbprint == '':
                            node['add_to_vcenter'] = 'Waiting for ESX host at %s to become available.' % interface
                            log.debug("Received empty thumbprint for host %s. Waiting for 5 minutes." % (uuid))
                            count = count + 1
                            sleep(60*5)

                    log.debug("Thumbprint for host %s after polling: %s" % (uuid, thumbprint))

                    if thumbprint != '':
                        log.debug("Before addHostToVCenter: %s, %s, %s" % (interface, vcUuid, deployment_obj['addToVCenter']['cluster_name']))
                        result = self.addHostToVCenter(interface, vcUuid, deployment_obj['addToVCenter']['cluster_name'], esx_credentials)
                        if result and result._type == 'Task':
                            add_host_tasks.append({'uuid':uuid, 'result':result})
                        else:
                            log.debug("Result: %s" % str(result))
                            node['add_to_vcenter'] = 'Error adding host %s to vCenter. The vCenter server did not start the task. The host must be added manually.' % (interface)
                    else:
                        log.debug("Thumbprint not found. Unable to add host to vCenter.")
                        node['add_to_vcenter'] = 'Unable to contact the host at %s. Add to vCenter aborted.' % (interface)
                else:
                    log.debug("Invalid interface for node %s: %s" % (uuid, interface))
                    if interface == 'DHCP':
                        node['add_to_vcenter'] = 'An interface configured for DHCP was specified. This host must be added to vCenter manually.'
                    else:
                        node['add_to_vcenter'] = 'No valid interface found. Add to vCenter aborted.'
            else:
                node['add_to_vcenter'] = 'No interface specified to add to vCenter.'

        except Exception as ex:
            node['add_to_vcenter'] = 'An error occurred while attempting to add host %s to vCenter. See the log for details.' % (interface)
            log.exception("Exception while attempting vCenter add: %s" % (str(ex)))

        # Monitor each of the add host tasks that were just started.
        self.monitorAddHostTasks(add_host_tasks, vcUuid)

    def _findJobType(self, job):
        type = [x['type'] for x in self.jobFolders if x['name'] == job]
        if type:
            return type[0]
        # Default to ESX since we only have two choices.
        return "ESX"


    def begin_run_deployment(self, deployment_collection):
        # Don't even start the thread if the deployment process is already running.
        if not self.deployment_sem.acquire(False):
            return

        t = Thread(target=self._run_deployment, name="run_deployment", args=(deployment_collection,))
        t.daemon = True
        t.start()

    # The main deployment function.  Once a deployment batch has been started, it must run to completion
    # before another batch is started.
    # The jobs parameter should be a list of server GUIDs and jobs to be applied to each server according to the following format:

    # deployment_collection = [
    #     {
    #      serverUuid: <uuid>,
    #      jobs: [
    #               {name:'job folder name 1', type:'ESX'},
    #               {name:'job folder name 2', type:'ESXi'}
    #            ],
    #      addToVCenter: {
    #           interface: '172.17.99.100',
    #           cluster: 'my-cluster'
    #      },
    #     },
    # ]

    # This function should not be called directly! It should be started using the
    # begin_run_deployment function.
    def _run_deployment(self, deployment_object):

        add_host_tasks = []
        threads = []
        deployment_collection = deployment_object['deploymentList']
        vcUuid = deployment_object['vcUuid']

        log.debug("Running deployment (vCenter: %s)" % (vcUuid))

        # Helper function to monitor an array of running threads.
        def wait_threads(threads):
            while True:
                alive_count = 0
                for thread in threads:
                    if thread.is_alive():
                        alive_count += 1
                if not alive_count:
                    break
                sleep(1)

        # First reset overall status so we start with a clean slate.
        for obj in deployment_collection:
            uuid = obj['serverUuid']
            node = self._get_node(uuid)
            node['overall_job_status'] = 0
            if obj['personalization'] and 'nics' in obj['personalization']:
                node['reconfigure'] = {'id':-1, 'message':ADDTOVC_NOT_STARTED}

        threads = []
        for obj in deployment_collection:
            uuid = obj['serverUuid']
            node = self._get_node(uuid)
            job = obj['jobs'][0]

            # If the job has been flagged as an 'ESXi' job, assume it's an ESXi image and treat it differently.
            type = self._findJobType(job)
            log.debug("Found job type %s for job %s" % (type, job))

            # Set up the thread args.
            args = (uuid, job, obj, vcUuid)

            if type == 'ESXi':

                log.debug("ESXi: Attempting to start job folder %s for server %s (reconfigure: %s)" % (job, uuid, str(obj['personalization'])))
                t = Thread(target=self.deploy_esxi, args=args)
                t.daemon = True
                t.start()
                threads.append(t)

            else:

                log.debug("ESX: Attempting to start job folder %s for server %s" % (job, uuid))
                t = Thread(target=self.deploy_esx, args=args)
                t.daemon = True
                t.start()
                threads.append(t)

        # Wait for each of the individual deployment threads to complete.
        fail_count = 0
        if threads:
            while True:
                alive_count = 0
                for thread in threads:
                    if thread.is_alive():
                        alive_count += 1
                if not alive_count:
                    break
                sleep(120)

        # Check to see if our threads failed (due to comm loss)
        if self.failures == len(threads):
            self.dc_status = DC_STATUS_CONNECTION_LOST 
        else:
            self.dc_status = DC_STATUS_DEPLOYMENT_FINISHED

        # Reset the failure value since we're done.
        self.monitor_sem.acquire()
        self.failures  = 0
        self.monitor_sem.release()

        self.deployment_sem.release()
        log.debug("Deployment finished")
