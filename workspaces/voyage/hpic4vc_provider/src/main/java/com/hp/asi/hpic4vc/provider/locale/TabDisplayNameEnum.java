package com.hp.asi.hpic4vc.provider.locale;

import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public enum TabDisplayNameEnum {

    DETAILS(I18NProvider.TAB_DisplayName_Details), DIAGRAM(
            I18NProvider.TAB_DisplayName_Diagram), HBAS(
            I18NProvider.TAB_DisplayName_HBAs), HEALTH(
            I18NProvider.TAB_DisplayName_Health), HOST(
            I18NProvider.TAB_DisplayName_Host), INFRASTRUCTURE(
            I18NProvider.TAB_DisplayName_Infrastructure), NETWORKING(
            I18NProvider.TAB_DisplayName_Networking), NEWSFEED(
            I18NProvider.TAB_DisplayName_Newsfeed), OVERVIEW(
            I18NProvider.TAB_DisplayName_Overview), PATHS(
            I18NProvider.TAB_DisplayName_Paths), RECOVERY_MANAGER(
            I18NProvider.TAB_DisplayName_RecoveryManager), REPLICATIONS(
            I18NProvider.TAB_DisplayName_Replications), SERVER(
            I18NProvider.TAB_DisplayName_Server), SOFTWARE_FIRMWARE(            
            I18NProvider.TAB_DisplayName_SoftwareFirmware), GETTING_STARTED(
            I18NProvider.TAB_DisplayName_GettingStarted),STORAGE_VOLUMES(
            I18NProvider.TAB_DisplayName_StorageVolumes), SUMMARY(
            I18NProvider.TAB_DisplayName_Summary), TASKS(
            I18NProvider.TAB_DisplayName_Tasks), VIRTUAL_CONNECT(
            I18NProvider.TAB_DisplayName_VirtualConnect), VIRTUAL_DISKS(
            I18NProvider.TAB_DisplayName_VirtualDisks), VMS_TO_VOLUMES(
            I18NProvider.TAB_DisplayName_VMsToVolumes), STORAGE(
            I18NProvider.TAB_DisplayName_Storage), CLUSTER(I18NProvider.TAB_DisplayName_Cluster),CLUSTER_NETWORKING(I18NProvider.TAB_DisplayName_Cluster_networking),
            HOST_INFORMATION(I18NProvider.TAB_DisplayName_Host_Information), UNKNOWN("UNKNOWN");

    private String I18N_Name;
    private Log log = LogFactory.getLog(this.getClass());

    private TabDisplayNameEnum (String I18N_Name) {
        this.I18N_Name = I18N_Name;
    }

    public static TabDisplayNameEnum getTabDisplayNameEnum (String keyName) {
        TabDisplayNameEnum keyNameEnum;

        try {
            keyNameEnum = TabDisplayNameEnum.valueOf(keyName.toUpperCase());
        } catch (IllegalArgumentException e) {
            LogFactory.getLog("TabDisplayNameEnum")
                    .info("Invalid tab display name recieved: " + keyName);
            keyNameEnum = UNKNOWN;
        }

        return keyNameEnum;
    }

    public String getDisplayNameValue (Locale locale) {

        if (this.I18N_Name.equalsIgnoreCase("UNKNOWN")) {
            log.info("Invalid tab display name recieved.  Null is returned for the tab name.");
            return null;
        } else {
            return I18NProvider.getInstance().getInternationalString(locale,
                                                                     I18N_Name);
        }
    }

}
