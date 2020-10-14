package com.hp.asi.hpic4vc.provider.locale;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Singleton that provides internationalized strings for HPIC4VC Provider.
 * Typical usage will be similar to:
 * <code>I18NProvider.getInstance().getInternationalString(locale, I18NProvider.SOME_CONSTANT)</code>
 * 
 * @author Andrew Khoury
 * 
 */
public class I18NProvider extends I18NCommon {
    public static final String Info_NotAvailable          = "Info.NotAvailable";
    public static final String Info_NoRecords             = "Info.NoRecords";
    public static final String Error_CannotMakeUrl        = "Error.CannotMakeUrl";
    public static final String Error_NoWsResponse         = "Error.NoWsResponse";
    public static final String Error_CannotMapJson        = "Error.CannotMapJson";
    public static final String Uim_ThumbprintNotAvailable = "Error.ThumbprintNotAvailable";
    public static final String Error_PythonException      = "Error.PythonException";
    public static final String Communication_Failure      = "Communication.Failure";

    public static final String HEADER_ENCLOSURE           = "Header.Enclosure";
    public static final String HEADER_BAY                 = "Header.Bay";

    public static final String Health_Status              = "Health.Status";
    public static final String Health_Source              = "Health.Source";
    public static final String Health_Description         = "Health.Description";
    public static final String Health_NoData              = "Health.NoData";
    public static final String Health_Status_Tooltip      = "Health.Status.Tooltip";
    public static final String Health_Source_Tooltip      = "Health.Source.Tooltip";
    public static final String Health_Description_Tooltip = "Health.Description.Tooltip";
    
    public static final String News_Tasks_Status          = "News_Tasks.Status";
    public static final String News_Tasks_Message         = "News_Tasks.Message";
    public static final String News_Tasks_Queued          = "News_Tasks.Queued";
    public static final String News_Tasks_Error           = "News_Tasks.Error";
    public static final String News_Tasks_Success         = "News_Tasks.Success";
    public static final String News_Tasks_Running         = "News_Tasks.Running";
    public static final String News_Tasks_JobStepMessage  = "News_Tasks.JobStepMessage";
    
    public static final String Tasks_Name                 = "Tasks.Name";
    public static final String Tasks_Details              = "Tasks.Details";
    public static final String Tasks_UserName             = "Tasks.UserName";
    public static final String Tasks_StartTime            = "Tasks.StartTime";
    public static final String Tasks_CompletedTime        = "Tasks.CompletedTime";
    public static final String Tasks_NoData               = "Tasks.NoData";
    public static final String Tasks_NoDetails            = "Tasks.NoDetails";
    public static final String Tasks_Target               = "Tasks.Target";

    public static final String News_Status                = "News.Status";
    public static final String News_Message               = "News.Message";
    public static final String News_Source                = "News.Source";
    public static final String News_DateTime              = "News.DateTime";
    public static final String News_NoData                = "News.NoData";

    public static final String FirmwareSummary_SoftwareTableTitle = "FirmwareSummary.SoftwareTableTitle";
    public static final String FirmwareSummary_FirmwareTableTitle = "FirmwareSummary.FirmwareTableTitle";
    public static final String FirmwareSummary_Type               = "FirmwareSummary.Type";
    public static final String FirmwareSummary_Host               = "FirmwareSummary.Host";
    public static final String FirmwareSummary_Name               = "FirmwareSummary.Name";
    public static final String FirmwareSummary_Description        = "FirmwareSummary.Description";
    public static final String FirmwareSummary_Version            = "FirmwareSummary.Version";
    public static final String FirmwareSummary_Software_NA        = "FirmwareSummary.Software.NA";
    public static final String FirmwareSummary_Firmware_NA        = "FirmwareSummary.Firmware.NA";

    public static final String Settings_Version                = "Settings.Version";
    public static final String Settings_Cluster_Properties     = "Settings.Cluster_Properties";
    public static final String Settings_Host_Properties        = "Settings.Host_Properties";
    public static final String Settings_Communication_Status   = "Settings.Communication_Status";
    public static final String Settings_Storage_Admin_Portal   = "Settings.Strage_Admin_Portal";
    public static final String Settings_VASA_Provider_URL      = "Settings.VASA_Provider_URL";

    public static final String Actions_Create_Datastore        = "Actions.Create_Datastore";
    public static final String Actions_Expand_Datastore        = "Actions.Expand_Datastore";
    public static final String Actions_Clone_VM                = "Actions.Clone_VM";
    public static final String Actions_Create_VM_From_Template = "Actions.Create_VM_From_Template";
    public static final String Actions_Delete_Datastore        = "Actions.Delete_Datastore";
    public static final String Actions_Delete_Volume           = "Actions.Delete_Volume";
    public static final String Actions_Firmware                = "Actions.Firmware";
    public static final String Actions_ToggleUID               = "Actions.ToggleUID";
    public static final String Actions_Power_On                = "Actions.PowerControl";
    public static final String Actions_Rediscover_Node         = "Actions.RediscoverNode";
    
    public static final String Help_String         = "Help.Help";
    public static final String Help_Cluster_Page   = "Help.Cluster";
    public static final String Help_Datastore_page = "Help.Datastore";
    public static final String Help_Host_Page      = "Help.Host";
    public static final String Help_VM_Page        = "Help.VM";
    public static final String Help_Default_Page   = "Help.Default";
    public static final String Help_Firmware       = "Help.Firware";

    public static final String About_String        = "About.About";

    public static final String ReloadPage_String   = "Refresh.ReloadPage";
    public static final String RefreshCache_String = "Refresh.RefreshCache";

    public static final String TAB_DisplayName_Details            = "TAB.DisplayName.Details";
    public static final String TAB_DisplayName_Diagram            = "TAB.DisplayName.Diagram";
    public static final String TAB_DisplayName_HBAs               = "TAB.DisplayName.HBAs";
    public static final String TAB_DisplayName_Health             = "TAB.DisplayName.Health";
    public static final String TAB_DisplayName_Host               = "TAB.DisplayName.Host";
    public static final String TAB_DisplayName_Infrastructure     = "TAB.DisplayName.Infrastructure";
    public static final String TAB_DisplayName_Networking         = "TAB.DisplayName.Networking";
    public static final String TAB_DisplayName_Newsfeed           = "TAB.DisplayName.Newsfeed";
    public static final String TAB_DisplayName_Overview           = "TAB.DisplayName.Overview";
    public static final String TAB_DisplayName_Paths              = "TAB.DisplayName.Paths";
    public static final String TAB_DisplayName_RecoveryManager    = "TAB.DisplayName.RecoveryManager";
    public static final String TAB_DisplayName_Replications       = "TAB.DisplayName.Replications";
    public static final String TAB_DisplayName_Server             = "TAB.DisplayName.Server";
    public static final String TAB_DisplayName_SoftwareFirmware   = "TAB.DisplayName.SoftwareFirmware";
    public static final String TAB_DisplayName_StorageVolumes     = "TAB.DisplayName.StorageVolumes";
    public static final String TAB_DisplayName_Summary            = "TAB.DisplayName.Summary";
    public static final String TAB_DisplayName_Tasks              = "TAB.DisplayName.Tasks";
    public static final String TAB_DisplayName_VirtualConnect     = "TAB.DisplayName.VirtualConnect";
    public static final String TAB_DisplayName_VirtualDisks       = "TAB.DisplayName.VirtualDisks";
    public static final String TAB_DisplayName_VMsToVolumes       = "TAB.DisplayName.VMsToVolumes";
    public static final String TAB_DisplayName_Storage            = "TAB.DisplayName.Storage";
    public static final String TAB_DisplayName_GettingStarted     = "TAB.DisplayName.GettingStarted";
    public static final String TAB_DisplayName_Cluster            = "TAB.DisplayName.Cluster";
    public static final String TAB_DisplayName_Cluster_networking = "TAB.DisplayName.Cluster_networking";
    public static final String TAB_DisplayName_Host_Information   = "TAB.DisplayName.Host_Information";

    public static final String VCCredentials_Hostname       = "VCCredentials.Hostname";
    public static final String VCCredentials_Password       = "VCCredentials.Password";
    public static final String VCCredentials_Username       = "VCCredentials.Username";
    public static final String VCCredentials_RowId          = "VCCredentials.RowId";
    public static final String VCCredentials_Type           = "VCCredentials.Type";
    public static final String VCCredentials_Add_Success    = "VCCredentials.Add.Success";
    public static final String VCCredentials_Delete_Success = "VCCredentials.Delete.Success";
    public static final String VCCredentials_Edit_Success   = "VCCredentials.Edit.Success";
    public static final String VCCredentials_NotFound       = "VCCredentials.NotFound";
    public static final String HPOneViewCredentials_Add_Success    = "HPOneViewCredentials.Add.Success";
    public static final String HPOneViewCredentials_Delete_Success = "HPOneViewCredentials.Delete.Success";
    public static final String HPOneViewCredentials_Edit_Success   = "HPOneViewCredentials.Edit.Success";
    
    public static final String CERTIFICATE_SELF_SIGNED_SUCCESS = "CERTIFICATE.SELF.SIGNED.SUCCESS";

    private static final I18NProvider INSTANCE = new I18NProvider();

    private I18NProvider () {
    }

    @Override
    public Class<? extends I18NCommon> getChildClass () {
        return I18NProvider.class;
    }

    public static I18NProvider getInstance () {
        return INSTANCE;
    }

    @Override
    protected ResourceBundle getResourceBundle (Locale locale) {
        return ResourceBundle.getBundle(String
                .format(RESOURCE_BUNDLE_BASE_NAME, getChildClass().getPackage()
                        .getName()), locale);
    }

}
