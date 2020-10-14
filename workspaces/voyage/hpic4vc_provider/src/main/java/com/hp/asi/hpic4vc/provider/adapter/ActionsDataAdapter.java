package com.hp.asi.hpic4vc.provider.adapter;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.asi.hpic4vc.provider.data.ConfigurationData;
import com.hp.asi.hpic4vc.provider.error.InitializationException;
import com.hp.asi.hpic4vc.provider.impl.SessionInfo;
import com.hp.asi.hpic4vc.provider.impl.SessionInfo.VObjectType;
import com.hp.asi.hpic4vc.provider.model.LinkModel;
import com.hp.asi.hpic4vc.provider.model.MenuModel;
import com.vmware.vim25.DynamicProperty;
import com.vmware.vim25.ObjectContent;
import com.vmware.vim25.VirtualMachineConfigInfo;

public class ActionsDataAdapter {
  
    private static final ConfigurationData CONFIGURATION_DATA = ConfigurationData.getInstance();
	private SessionInfo sessionInfo;
	private Log log;
	
    public ActionsDataAdapter (final SessionInfo sessionInfo) {
        this.sessionInfo = sessionInfo;
        this.log         = LogFactory.getLog(this.getClass());
    }

    
    public MenuModel getActionsData() {
        log.info("Getting ActionsData.");
        MenuModel actionsList = new MenuModel();
        if (null != sessionInfo && null != sessionInfo.getVobjectType()) {
            switch (sessionInfo.getVobjectType()) {
                case HOST:
                case DATASTORE:
                case CLUSTER:
                    actionsList = CONFIGURATION_DATA.getVObjectActions(sessionInfo.getVobjectType(), sessionInfo, true);
                    break;
                case VM:
                    if (isTemplate()) {
                        actionsList = CONFIGURATION_DATA.getVObjectActions(VObjectType.VM_TEMPLATE, sessionInfo, true);
                    } else {
                        actionsList = CONFIGURATION_DATA.getVObjectActions(VObjectType.VM, sessionInfo, true);
                    }
                    break;
                default:
                    log.warn("getActionsData doesn't recognize vObjectType " + 
                           sessionInfo.getVobjectType().name());
                    break;
                    
            }
        } else {
            log.warn("getActionsData had a null sessionInfo or sessionInfo.getVobjectType()");
        }
        
        for (LinkModel link : actionsList.getMenuItems()){
            link.url += "&moref=" + sessionInfo.getMoref();
        }
        log.info("Returning " + actionsList.toString());
        return actionsList;
    }



    boolean isTemplate () {
        VirtualMachineConfigInfo config = null;
        List<ObjectContent> objectContents = null;
        try {
            objectContents = sessionInfo
                    .getVMProperties(sessionInfo.getMoref(),
                                     sessionInfo.getSessionId(),
                                     sessionInfo.getServerGuid());
        } catch (InitializationException e) {
            String errorMessage = "Failed to get the objectContents of VM from sessionInfo ";
            log.error(errorMessage);
        }

        // Getting the config property from the VM object contents
        if (objectContents != null) {
            for (ObjectContent content : objectContents) {
                if ("VirtualMachine".equalsIgnoreCase(content.getObj()
                        .getType())) {
                    List<DynamicProperty> dps = content.getPropSet();
                    if (dps != null) {
                        for (DynamicProperty dp : dps) {
                            if ("config".equalsIgnoreCase(dp.getName())) {
                                config = (VirtualMachineConfigInfo) dp.getVal();
                            }
                        }
                    }
                }
            }
            return config.isTemplate();
        }
        return false;
    }
}
