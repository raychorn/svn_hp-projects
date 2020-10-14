package com.hp.asi.hpic4vc.provider.api;


import com.hp.asi.hpic4vc.provider.model.CertificateModel;
import com.hp.asi.hpic4vc.provider.model.ConfigurationListModel;
import com.hp.asi.hpic4vc.provider.model.FooterModel;
import com.hp.asi.hpic4vc.provider.model.HeaderModel;
import com.hp.asi.hpic4vc.provider.model.MenuModel;
import com.hp.asi.hpic4vc.provider.model.NewsFeedModel;
import com.hp.asi.hpic4vc.provider.model.PageModel;
import com.hp.asi.hpic4vc.provider.model.PortletModel;
import com.hp.asi.hpic4vc.provider.model.StringModel;
import com.hp.asi.hpic4vc.provider.model.TableModel;
import com.hp.asi.hpic4vc.provider.model.TaskSummary;
import com.hp.asi.hpic4vc.provider.model.UserInfoModel;


/**
 * Interface used to test a java service call from the Flex UI.
 *
 * It must be declared as osgi:service with the same name in
 * main/resources/META-INF/spring/bundle-context-osgi.xml
 */
public interface Hpic4vc_provider {
    
    /**
     * @param objectReference Provided by the hpic4vc_ui and contains the
     *                        server Guid and moref.
     *                        
     * @return A table of detailed task data.
     */
    public TaskSummary getTaskSummary(final String objReferenceId);
    
   /**
    * @param objectReference Provided by the hpic4vc_ui and contains the
    *                        server Guid and moref.
    *                        
    * @return A table of detailed task data.
    */
   public TableModel getTaskDetails(final String objReferenceId);
   
   /**
    * @param objectReference Provided by the hpic4vc_ui and contains the
    *                        server Guid and moref.
    *                        
    * @return A table of detailed health data.
    */
   public TableModel getHealthDetails(final String objReferenceId);
   
   /**
    * @param objectReference Provided by the hpic4vc_ui and contains the
    *                        server Guid and moref.
    *                        
    * @return An object of type HeaderModel to contain the health status, IP, 
    *         tasks, actions, etc., of the VMware object referenced by 
    *         objReferenceId.
    */
   public HeaderModel getHeader(final String objReferenceId);
   
   /**
    * @param objectReference Provided by the hpic4vc_ui and contains the
    *                        server Guid and moref.
    *                        
    * @return An object of type FooterModel to contain the array and management 
    *         console and a link to launch the management console if any arrays 
    *         are configured for the VMware object referenced by 
    *         objReferenceId.
    */
   public FooterModel getFooter(final String objReferenceId);
   
   /**
    * @param objectReference Provided by the hpic4vc_ui and contains the
    *                        server Guid and moref.
    *                        
    * @return An object of type TableModel to contain the software and firmware 
    * 		  details of the arrays and management consoles configured for the 
    * 		  VMware object referenced by objReferenceId.
    */
   public TableModel getSoftwareFirmwareSummary(final String objReferenceId);
   
   /**
    * @param objectReference Provided by the hpic4vc_ui and contains the
    *                        server Guid and moref.
    *                        
    * @return An object of type FirmwareModel to contain the software and 
    *         firmware details of the arrays and management consoles configured 
    *         for the VMware object referenced by objReferenceId.
    */
   public TableModel getSoftwareFirmwareDetail(final String objReferenceId);
   
   /**
    * @param objectReference Provided by the hpic4vc_ui and contains the
    *                        server Guid and moref.
    *                        
    * @return An object of type NewsFeedModel that contains a list of NewsItems.
    */
   public NewsFeedModel getNewsFeed(final String objReferenceId);
   
   /**
    * @param objectReference Provided by the hpic4vc_ui and contains the
    *                        server Guid and moref.
    *                        
    * @return An object of type TableModel that contains the detailed
    *         news information.
    */
   public TableModel getNewsDetails(final String objReferenceId);
   
   /**
    * @param objectReference Provided by the hpic4vc_ui and contains the
    *                        server Guid and moref.
    *                        
    * @return An object of type MenuModel that contains the possible actions
    *         that a user may select.
    */
   public MenuModel getGearMenu(final String objReferenceId);
   
   /**
    * @param objectReference Provided by the hpic4vc_ui and contains the
    *                        server Guid and moref.
    *                        
    * @return A PageModel object that contains information about the tabs
    *         and portlets that need to be displayed in the plugin.
    */
   public PageModel getPages(final String objReferenceId);
   
   /**
    * @param objectReference Provided by the hpic4vc_ui and contains the
    *                        server Guid and moref.
    *                        
    * @return A PageModel object that contains information about the tabs
    *         and portlets that need to be displayed in the plugin in 
    *         the VMware monitor tab.
    */   
   public PageModel getMonitorTabPages(final String objReferenceId);
   
   /**
    * @param objectReference Provided by the hpic4vc_ui and contains the
    *                        server Guid and moref.
    *                        
    * @return A PageModel object that contains information about the tabs
    *         and portlets that need to be displayed in the plugin in 
    *         the VMware manage tab.
    */   
   public PageModel getManageTabPages(final String objReferenceId);
   
   /**
    * @param objectReference Provided by the hpic4vc_ui and contains the
    *                        server Guid and moref.
    *                        
    * @return A PortletModel containing a list of TabModel that each contains 
    *         information about a portlet that needs to be displayed in the 
    *         plugin in the vCenter's "Summary" page.
    */
   public PortletModel getSummaryPortlets(final String objReferenceId);
   
   /**
    * @param objReferenceId Provided by the hpic4vc_ui and contains the serverGuid.
    * 
    * @return A UserInfoModel containing the user's name and role Id.
    */
   public UserInfoModel getUserInfo(final String objReferenceId);
   
   /**
    * @param objectReference Provided by the hpic4vc_ui and contains the
    *                        serverGuid.
    *                        
    * @return A list of the links needed for the Getting Started page.  Each
    *         link is associated with the appropriate help link for the topic.
    */
   public ConfigurationListModel getConfigurationLinks(final String objReferenceId);
   
   /**
    * @param objectReference Provided by the hpic4vc_ui and contains the
    *                        serverGuid.
    *                        
    * @return A list of the help links needed for the "Using the product"
    *         portion of the Getting Started page.
    */
   public MenuModel getProductHelpPages(final String objReferenceId);
  
   /**
    * 
    * @param objectReference Provided by the hpic4vc_ui and contains the serverGuid.
    *                        
    * @return An object of type TableModel that contains the vCenter server
    *         host names, user names, and passwords that are registered.
    */
   public TableModel getVCCredentials(final String objReferenceId);

   /**
    * 
    * @param objectReference Provided by the hpic4vc_ui and contains the serverGuid.
    *                        
    * @return An object of type TableModel that contains the HP OneView
    *         host names, user names, and passwords that are registered.
    */
   public TableModel getHPOneViewCredentials(final String objReferenceId);

   /**
    * API to add a vCenter credential to a host by passing the desired
    * user name and password.
    * 
    * @param objectReference Provided by the hpic4vc_ui and contains the serverGuid.
    *                        
    * @param host            The desired host for adding the credentials.
    * 
    * @param username        The user name to add.
    * 
    * @param password        The password associated with the user name being added.
    * 
    * @return                Returns a StringModel that contains success or failure.
    *                        If failure, the StringModel's error message will contain
    *                        the cause of the failure.
    */
   public StringModel addVCCredential (final String objReferenceId,
                                       final String host,
                                       final String username,
                                       final String password);
   
   /**
    * API to add a HP OneView Credential to a host by passing the desired
    * user name and password.
    * 
    * @param objectReference Provided by the hpic4vc_ui and contains the serverGuid.
    *                        
    * @param host            The desired host for adding the credentials.
    * 
    * @param username        The user name to add.
    * 
    * @param password        The password associated with the user name being added.
    * 
    * @return                Returns a StringModel that contains success or failure.
    *                        If failure, the StringModel's error message will contain
    *                        the cause of the failure.
    */
   public StringModel addHPOneViewCredential(  final String objReferenceId, 
											   final String host,
											   final String username,
											   final String password);

   /**
    * API to delete a vCenter credential from a host by passing the unique ID
    * of the credential.
    * 
    * @param objectReference Provided by the hpic4vc_ui and contains the serverGuid.
    * 
    * @param host            The host name of the credential to delete.
    * 
    * @param username        The user name of the credential to delete.
    * 
    * @param id              The id of the credential to delete.
    * 
    * @return                Returns a StringModel that contains success or failure.
    *                        If failure, the StringModel's error message will contain
    *                        the cause of the failure.
    */
   public StringModel deleteVCCredential (final String objReferenceId,
                                          final String host,
                                          final String username,
                                          final String id);
   
   /**
    * API to delete a HP OneVIew Credential from a host by passing the unique ID
    * of the credential.
    * 
    * @param objectReference Provided by the hpic4vc_ui and contains the serverGuid.
    * 
    * @param host            The host name of the credential to delete.
    * 
    * @param username        The user name of the credential to delete.
    * 
    * @param id              The id of the credential to delete.
    * 
    * @return                Returns a StringModel that contains success or failure.
    *                        If failure, the StringModel's error message will contain
    *                        the cause of the failure.
    */
   public StringModel deleteHPOneViewCredential(   final String objReferenceId, 
												   final String host,
												   final String username,
												   final String id);

   /**
    * API to edit an existing vCenter credential from a host by passing the 
    * unique ID of the credential.
    * 
    * @param objectReference Provided by the hpic4vc_ui and contains the serverGuid.
    * 
    * @param host            The host name of the credential to edit.
    * 
    * @param username        The user name of the credential to edit.
    * 
    * @param password        The updated password to use.
    * 
    * @param id              The id of the credential to edit.
    * 
    * @return                Returns a StringModel that contains success or failure.
    *                        If failure, the StringModel's error message will contain
    *                        the cause of the failure.
    */
   public StringModel editVCCredential (final String objReferenceId,
                                        final String host,
                                        final String username,
                                        final String password,
                                        final String id);
      
   /**
    * API to edit an existing HP OneView Credential from a host by passing the 
    * unique ID of the credential.
    * 
    * @param objectReference Provided by the hpic4vc_ui and contains the serverGuid.
    * 
    * @param host            The host name of the credential to edit.
    * 
    * @param username        The user name of the credential to edit.
    * 
    * @param password        The updated password to use.
    * 
    * @param id              The id of the credential to edit.
    * 
    * @return                Returns a StringModel that contains success or failure.
    *                        If failure, the StringModel's error message will contain
    *                        the cause of the failure.
    */
   public StringModel editHPOneViewCredential( final String objReferenceId, 
											   final String host,
											   final String username,
											   final String password,
											   final String id);
	   
   /**
    * Returns the host name where the plug-in is installed.
    * 
    * @param objReferenceId  Provided by the hpic4vc_ui and contains the serverGuid.
    *                        
    * @return                A model to contain the host portion of the URL 
    *                        where the plug-in is installed.
    */
   public StringModel getPluginHostName(final String objReferenceId);
   
   /**
    * Generates a self-signed certificate and installs it.  Returns the generated
    * certificate.
    * 
    * @param objReferenceId Provided by the hpic4vc_ui and contains the serverGuid.
    * 
    * @param args           Array of arguments provided from the UI containing
    *                       information for creating the certificate (ex:
    *                       country, state, location, etc).
    *                       
    * @return               The generated certificate placed in a StringModel.
    */
   public StringModel generateSelfSignedCertificate(final String objReferenceId, 
		   final String[] args);

   /**
    * Generates a self-signed request to use with a certificate signing authority.
    *
    * @param objReferenceId Provided by the hpic4vc_ui and contains the serverGuid.
    * 
    * @param args           Array of arguments provided from the UI containing
    *                       information for creating the certificate (ex:
    *                       country, state, location, etc).
    *                       
    * @return               The generated certificate placed in a StringModel.
    */
   public StringModel generateSignedRequest(final String objReferenceId, 
		   final String[] args);
   
   /**
    * Installs a certificate signed by a signing authority.
    * 
    * @param objReferenceId Provided by the hpic4vc_ui and contains the serverGuid.
    * 
    * @param certificate    The certificate to install.
    * 
    * @return               A StringModel to indicate success or failure.
    */
   public StringModel installSignedCertificate(final String objReferenceId, 
		   final String certificate);
   
   /**
    * Returns the currently installed certificate.  Includes whether the
    * certificate is self-signed or not.
    * 
    * @param objReferenceId Provided by the hpic4vc_ui and contains the serverGuid.
    * @return               Returns a CertificateModel to indicate if a certificate
    *                       is installed, if the certificate is self-signed,
    *                       and provide the certificate. 
    */
   public CertificateModel getInstalledCertificate(final String objReferenceId);
   
   /**
    * Returns product information such as version information.
    * 
    * @param objReferenceId Provided by the hpic4vc_ui and contains the serverGuid.
    */
   public MenuModel getProductAbout (final String objReferenceId);
   
   /**
    * Provides the link to view help for generating a self-signed certificate.
    * 
    * @param objReferenceId Provided by the hpic4vc_ui and contains the serverGuid.
    */
   public String getGenerateSelfSignedHelp(final String objReferenceId);
   
   /**
    * Provides the link to view help for generating a self-signed request to
    * use with a signing authority.
    * 
    * @param objReferenceId Provided by the hpic4vc_ui and contains the serverGuid.
    */
   public String getGenerateSignedRequestHelp(final String objReferenceId);
   
   /**
    * Provides the link to view help for installing a certificate after it
    * has been signed by a signing authority.
    * 
    * @param objReferenceId Provided by the hpic4vc_ui and contains the serverGuid.
    */
   public String getInstallCertificateHelp(final String objReferenceId);
   
   /**
    * Provides the link to view help for provisioning operations.
    * 
    * @param objReferenceId Provided by the hpic4vc_ui and contains the serverGuid.
    */
   public String getICServerProvisioningHelp(final String objReferenceId);
   
   /**
    * Provides the link to view help for network preference.
    * 
    * @param objReferenceId Provided by the hpic4vc_ui and contains the serverGuid.
    */
   public String getNetworkPreferenceHelp(final String objReferenceId);

}
