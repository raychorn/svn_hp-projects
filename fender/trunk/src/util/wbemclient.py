#####################################################
#
# wbemclient.py
#
# Copyright 2007 Hewlett-Packard Development Company, L.P.
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
#    A generic WBEM client used to gather information from ESXi hosts. Utilizes
#    functionality from the pywbem library.
#
#    Some code taken from Barbara Craig's python scripts (barbara.craig@hp.com):
#        installURI.py
#        getFirmware.py
#        getConfig.py
#    
#####################################################
import logging
from vmware.vcenter import vCenter, ManagedObjectRef
from util import catalog
import pywbem
from pywbem import *

class WBEMClient():

    HP_NAMESPACE = 'root/hpq'
    MAX_TICKET_RETRIES = 3
    
    ERR_CREDENTIALS = (0, 'No ESXi host credentials were provided.')
    ERR_AUTHORIZATION = (1, 'The Insight Control for vCenter plugin was unable to login to the ESXi host.')
    ERR_BUNDLE = (2, 'The HP ESXi Offline Bundle for VMware is not installed on this host.')
    ERR_OTHER = (3, 'An error occurred while discovering information from the host.')
    ERR_HOST_NOT_ESXI = (4, 'This feature is only supported on hosts running ESXi.')
    ERR_HOST_TICKET = (5, 'Insight Control for vCenter was unable to acquire an authentication ticket from the vCenter Server.')
    ERR_OPERATION = (6, 'The requested operation is not supported. This command is only supported on ESXi 5.0 and above.')
    ERR_CONNECT = (7, 'Insight Control for vCenter was not able to connect to the host.')

    def __init__(self, host, vc_uuid):
        self.last_error = None
        self.ticket_retries = 0
        self.host = host.name
        self.vc = self.get_vc(vc_uuid)
        # TODO
        #self.esx_name = host.config.product.name
        self.esx_name = "ESXi"

        self.mob = ManagedObjectRef(host.moref().split(':')[0], host.moref().split(':')[1])
        self.ticket = ""

        self.get_cim_service_ticket()
        self.connection = self.get_connection()

    def get_vc(self, uuid):
        vcs = [vc for vc in catalog.get_all() if isinstance(vc, vCenter) and vc.sc.about.instanceUuid.lower()==uuid.lower()]
        if len(vcs) == 1:
            return vcs[0]
        else:
            return None

    def is_ESXi_host(self):
        return (self.esx_name.find('ESXi') != -1)

    def get_cim_service_ticket(self):
        if not self.mob:
            self.last_error = self.ERR_HOST_TICKET
            return

        host_system = ManagedObjectRef(self.mob._type, self.mob.value)
        if self.vc == None:
            self.last_error = self.ERR_HOST_TICKET
            return

        try:
            service_ticket = self.vc.vim.AcquireCimServicesTicket(host_system)
            if 'sessionId' in service_ticket:
                self.ticket = service_ticket['sessionId']
        except:
            self.last_error = self.ERR_HOST_TICKET
            pass

    def get_connection(self):
        if self.ticket == "":
            self.last_error = self.ERR_HOST_TICKET
            return None
        return WBEMConnection('https://%s' % self.host, (self.ticket, self.ticket), self.HP_NAMESPACE)

    # CIM service tickets have a finite life. We may have to get a new one if authorization fails.
    def reconnect(self):
        self.get_cim_service_ticket()
        self.connection = self.get_connection()

    def get_instance_names(self, classes):

        if self.connection == None:
            return None

        instances = []
        for cls in classes:
            try:
                instances += self.connection.EnumerateInstanceNames(cls, namespace=self.HP_NAMESPACE)
            except:
                pass

        return instances

    def get_class_instances(self, classes):

        if self.connection == None:
            return None

        # Create an empty object to hold our class instances.
        wbemObj = {}
        self.last_error = None
        for cls in classes:
            try:
                instances = self.connection.EnumerateInstances(cls)
                wbemObj[cls] = {}

                i = 0
                for instance in instances:
                    # Give each instance a unique ID since some of them have the same name.
                    instance_id = instance.classname + "." + str(i)
                    i += 1

                    wbemObj[cls][instance_id] = {}
                    for name, value in instance.items():
                        wbemObj[cls][instance_id][name] = value

            except pywbem.CIMError as cim_err:
                if not self.is_ESXi_host():
                    self.last_error = self.ERR_HOST_NOT_ESXI
                else:
                    if cim_err[0] == 5:
                        self.last_error = self.ERR_OPERATION
                    elif cim_err[0] == 0:
                        self.last_error = self.ERR_CONNECT 
                    else:
                        self.last_error = self.ERR_BUNDLE
            except cim_http.AuthError as auth_err:
                # Retry if our authentication fails. Tickets have a finite life, so we may need to get a new one.
                # If we still fail more than the max number of retries, stop because we don't want to retry forever.
                if self.ticket_retries < self.MAX_TICKET_RETRIES:
                    self.ticket_retries += 1
                    self.reconnect()
                    return self.get_class_instances(classes)
                else:
                    # Reset the retry counter for the next call
                    self.ticket_retries = 0
                    self.last_error = self.ERR_AUTHORIZATION
            except Exception as err:
                self.last_error = self.ERR_OTHER

        return wbemObj

    # Invokes a specific method on a list of classes. Returns the status code
    # and the out parameters from the method invocation.
    #def invoke_method(self, method_name, class_names, **in_params):
    def invoke_method(self, **kwargs):

        method_name = kwargs.get('method_name')
        class_names = kwargs.get('class_names')

        if self.connection == None:
            return None

        instances = self.get_instance_names(class_names)
        if not instances:
            return None

        results = []
        for instance in instances:
            try:
                if 'in_params' in kwargs:
                    in_params = kwargs.get('in_params')
                    in_params['Target'] = instance
                    status, out_params = self.connection.InvokeMethod(method_name, instance, **in_params)
                else:
                    status, out_params = self.connection.InvokeMethod(method_name, instance)

                result = {'status': status, 'out_params': out_params}
                results.append(result)
            except Exception as ex:
                pass

        return results
