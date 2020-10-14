#!/usr/bin/python
#####################################################
#
# hpuim.py
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
#    Mohammed M. Islam
# 
# Description:
#    UIM service module
#
#####################################################

import win32serviceutil
import win32service
import win32event
#import win32evtlogutil
import servicemanager
#import sys

        
class HPUIM_Service(win32serviceutil.ServiceFramework):
    _svc_name_ = "HPUIM"
    _svc_display_name_ = "HP Insight Control for vCenter UI Manager"
    _svc_description_ = "Integrates HP Insight Control Management with vCenter"
    _svc_deps_ = ["EventLog"]
    
    def __init__(self, args):
        win32serviceutil.ServiceFramework.__init__(self, args)
        self.hWaitStop = win32event.CreateEvent(None, 0, 0, None)

    def SvcDoRun(self):
        self.ReportServiceStatus(win32service.SERVICE_START_PENDING)    
        from uim import UserInterfaceManager
        self.uim = UserInterfaceManager()
        self.uim.start()
        self.ReportServiceStatus(win32service.SERVICE_RUNNING)
        servicemanager.LogMsg(
                servicemanager.EVENTLOG_INFORMATION_TYPE,
                servicemanager.PYS_SERVICE_STARTED,
                (self._svc_name_, ''))
        win32event.WaitForSingleObject(self.hWaitStop, win32event.INFINITE)
        return
        
    def SvcStop(self):
        self.ReportServiceStatus(win32service.SERVICE_STOP_PENDING)
        self.uim.stop()
        win32event.SetEvent(self.hWaitStop)
        servicemanager.LogMsg(
                servicemanager.EVENTLOG_INFORMATION_TYPE,
                servicemanager.PYS_SERVICE_STOPPED,
                (self._svc_name_, ''))
        self.ReportServiceStatus(win32service.SERVICE_STOPPED) 

if __name__ == '__main__':
    win32serviceutil.HandleCommandLine(HPUIM_Service)

# vim: ts=4 sts=4 sw=4:
