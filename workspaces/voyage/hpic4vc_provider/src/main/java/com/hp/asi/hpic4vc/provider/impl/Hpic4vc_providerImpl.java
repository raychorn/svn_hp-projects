package com.hp.asi.hpic4vc.provider.impl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.asi.hpic4vc.provider.adapter.AboutDataAdapter;
import com.hp.asi.hpic4vc.provider.adapter.ActionsDataAdapter;
import com.hp.asi.hpic4vc.provider.adapter.AddHPOneViewCredentialPostAdapter;
import com.hp.asi.hpic4vc.provider.adapter.AddVCCredentialPostAdapter;
import com.hp.asi.hpic4vc.provider.adapter.CredentialsPostAdapter;
import com.hp.asi.hpic4vc.provider.adapter.DeleteHPOneViewCredentialPostAdapter;
import com.hp.asi.hpic4vc.provider.adapter.DeleteVCCredentialPostAdapter;
import com.hp.asi.hpic4vc.provider.adapter.EditHPOneViewCredentialPostAdapter;
import com.hp.asi.hpic4vc.provider.adapter.EditVCCredentialPostAdapter;
import com.hp.asi.hpic4vc.provider.adapter.FirmwareDetailsDataAdapter;
import com.hp.asi.hpic4vc.provider.adapter.FirmwareSummaryDataAdapter;
import com.hp.asi.hpic4vc.provider.adapter.GenerateCertificateRequestPostAdapter;
import com.hp.asi.hpic4vc.provider.adapter.HPOneViewCredentialsDataAdapter;
import com.hp.asi.hpic4vc.provider.adapter.HealthDetailsDataAdapter;
import com.hp.asi.hpic4vc.provider.adapter.HealthSummaryDataAdapter;
import com.hp.asi.hpic4vc.provider.adapter.HelpMenuMaker;
import com.hp.asi.hpic4vc.provider.adapter.InstallCertificateDataAdapter;
import com.hp.asi.hpic4vc.provider.adapter.InstallCertificatePostAdapter;
import com.hp.asi.hpic4vc.provider.adapter.LaunchLinksDataAdapter;
import com.hp.asi.hpic4vc.provider.adapter.NewsDetailDataAdapter;
import com.hp.asi.hpic4vc.provider.adapter.NewsSummaryDataAdapter;
import com.hp.asi.hpic4vc.provider.adapter.PageDataAdapter;
import com.hp.asi.hpic4vc.provider.adapter.PageType;
import com.hp.asi.hpic4vc.provider.adapter.PostAdapter;
import com.hp.asi.hpic4vc.provider.adapter.ProductConfigurationDataAdapter;
import com.hp.asi.hpic4vc.provider.adapter.ProductHelpDataAdapter;
import com.hp.asi.hpic4vc.provider.adapter.RefreshMenuDataAdapter;
import com.hp.asi.hpic4vc.provider.adapter.SettingsDataAdapter;
import com.hp.asi.hpic4vc.provider.adapter.SummaryPortletListDataAdapter;
import com.hp.asi.hpic4vc.provider.adapter.TaskDetailsDataAdapter;
import com.hp.asi.hpic4vc.provider.adapter.TaskSummaryDataAdapter;
import com.hp.asi.hpic4vc.provider.adapter.UserInfoDataAdapter;
import com.hp.asi.hpic4vc.provider.adapter.VCCredentialsDataAdapter;
import com.hp.asi.hpic4vc.provider.adapter.VObjectNameDataAdapter;
import com.hp.asi.hpic4vc.provider.api.Hpic4vc_provider;
import com.hp.asi.hpic4vc.provider.api.ProviderExecutor;
import com.hp.asi.hpic4vc.provider.model.CertificateModel;
import com.hp.asi.hpic4vc.provider.model.ConfigurationListModel;
import com.hp.asi.hpic4vc.provider.model.FooterModel;
import com.hp.asi.hpic4vc.provider.model.HeaderModel;
import com.hp.asi.hpic4vc.provider.model.HostDataModel;
import com.hp.asi.hpic4vc.provider.model.MenuModel;
import com.hp.asi.hpic4vc.provider.model.NewsFeedModel;
import com.hp.asi.hpic4vc.provider.model.PageModel;
import com.hp.asi.hpic4vc.provider.model.PortletModel;
import com.hp.asi.hpic4vc.provider.model.StringModel;
import com.hp.asi.hpic4vc.provider.model.TableModel;
import com.hp.asi.hpic4vc.provider.model.TaskSummary;
import com.hp.asi.hpic4vc.provider.model.UserInfoModel;
import com.vmware.vise.usersession.UserSessionService;

/**
 * Implementation of the Hpic4vc_provider interface
 */
public class Hpic4vc_providerImpl implements Hpic4vc_provider {
    // Cache the last received vObject data to make loading the header faster.
    private static final ConcurrentMap<String, HostDataModel> VOBJECT_MAP = new ConcurrentHashMap<String, HostDataModel>();

    private Log log;
    private ProviderExecutor executorService;

    public Hpic4vc_providerImpl () {
        this.log = LogFactory.getLog(this.getClass());
        this.executorService = new ProviderExecutorImpl();
        log.info("Hpic4vc_provider has started.");
    }

    public Hpic4vc_providerImpl (UserSessionService userSessionService) {
        this.log = LogFactory.getLog(this.getClass());
        this.executorService = new ProviderExecutorImpl();
        ((SessionManagerImpl) SessionManagerImpl.getInstance())
                .setUserSessionService(userSessionService);
        log.info("Hpic4vc_provider has started.");
    }

    @Override
    public TaskSummary getTaskSummary (final String objReferenceId) {
        log.info("Calling getTaskSummary for objReferenceId " + objReferenceId);
        return this.executorService.execute(new TaskSummaryDataAdapter(null, null),
                                            objReferenceId);
    }
    
    @Override
    public TableModel getTaskDetails (final String objReferenceId) {
        log.info("Calling getTaskDetails for objReferenceId " + objReferenceId);
        return this.executorService.execute(new TaskDetailsDataAdapter(),
                                            objReferenceId);
    }

    @Override
    public TableModel getHealthDetails (final String objReferenceId) {
        log.info("Calling getHealthDetails for objReferenceId "
                + objReferenceId);
        return this.executorService.execute(new HealthDetailsDataAdapter(),
                                            objReferenceId);
    }

    @Override
    public MenuModel getGearMenu (final String objReferenceId) {
        log.info("Calling getGearMenu for objReferenceId " + objReferenceId);
        return this.executorService.execute(new SettingsDataAdapter(),
                                            objReferenceId);
    }

    @Override
    public HeaderModel getHeader (final String objReferenceId) {
        long startTime = System.currentTimeMillis();
        log.info("Calling getHeader for objReferenceId " + objReferenceId);

        SessionInfo sessionInfo  = new SessionInfo(objReferenceId);
        HeaderModel headerModel  = new HeaderModel();        
        int numThreads           = 3;
        CountDownLatch countdown = new CountDownLatch(numThreads);
        this.executorService.executeNoBlock(new VObjectNameDataAdapter(headerModel, countdown), sessionInfo);
        this.executorService.executeNoBlock(new HealthSummaryDataAdapter(headerModel, countdown), sessionInfo);
        this.executorService.executeNoBlock(new TaskSummaryDataAdapter(headerModel, countdown), sessionInfo);

        // headerModel.actions
        MenuModel actionsList = new ActionsDataAdapter(sessionInfo)
                .getActionsData();
        if (null != actionsList) {
            headerModel.actions = actionsList.getMenuItems();
        } else {
            log.info("getHeader encountered a null MenuModel for actionsList.");
        }

        // headerModel.Settings
        MenuModel configList = getGearMenu(objReferenceId);
        if (null != configList) {
            headerModel.configurations = configList.getMenuItems();
        } else {
            log.info("getHeader encountered a null MenuModel for configList.");
        }

        // headerModel.Refresh
        MenuModel refreshList = this.executorService
                .execute(new RefreshMenuDataAdapter(), sessionInfo);
        if (null != refreshList) {
            headerModel.refresh = refreshList.getMenuItems();
            if (refreshList.menuItems.size() > 1) {
                headerModel.showRefreshHover = true;
            }
        } else {
            log.info("getHeader encountered a null MenuModel for refreshList.");
        }

        // headerModel.Help
        // Web-service is not present, therefore, we will build the data
        // ourselves
        headerModel.helpUrl = HelpMenuMaker.makeComponentHelpUrl(sessionInfo);

        int thirtySeconds = 30000;
        try {
            countdown.await(thirtySeconds, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            log.warn("Timeout occurred while getting header data, returning as much as possible.", e);
        } 
        
        log.info("Returning headerModel from the provider: "
                + headerModel.toString());
        long delta = System.currentTimeMillis() - startTime;
        log.info("getHeader took " + delta + " milliseconds.");
        
        return headerModel;

    }

    @Override
    public FooterModel getFooter (String objReferenceId) {
        log.info("Calling getFooter for objReferenceId " + objReferenceId);
        return this.executorService.execute(new LaunchLinksDataAdapter(),
                                            objReferenceId);
    }

    @Override
    public TableModel getSoftwareFirmwareSummary (final String objReferenceId) {
        log.info("Calling getSoftwareFirmwareSummary for objReferenceId "
                + objReferenceId);
        return this.executorService.execute(new FirmwareSummaryDataAdapter(),
                                            objReferenceId);
    }

    @Override
    public TableModel getSoftwareFirmwareDetail (final String objReferenceId) {
        log.info("Calling getSoftwareFirmwareDetail for objReferenceId "
                + objReferenceId);
        return this.executorService.execute(new FirmwareDetailsDataAdapter(),
                                            objReferenceId);
    }

    @Override
    public NewsFeedModel getNewsFeed (final String objReferenceId) {
        log.info("Calling getNewsFeed for objReferenceId " + objReferenceId);
        return this.executorService.execute(new NewsSummaryDataAdapter(),
                                            objReferenceId);
    }

    @Override
    public TableModel getNewsDetails (final String objReferenceId) {
        log.info("Calling getNewsDetails for objReferenceId " + objReferenceId);
        return this.executorService.execute(new NewsDetailDataAdapter(),
                                            objReferenceId);
    }

    @Override
    public PageModel getPages (String objReferenceId) {
        log.info("Calling getPages for objReferenceId " + objReferenceId);
        return findPages(objReferenceId, null);

    }

    @Override
    public PageModel getMonitorTabPages (String objReferenceId) {
        log.info("Calling getPages for objReferenceId " + objReferenceId);
        return findPages(objReferenceId, PageType.monitor);
    }

    @Override
    public PageModel getManageTabPages (String objReferenceId) {
        log.info("Calling getPages for objReferenceId " + objReferenceId);
        return findPages(objReferenceId, PageType.manage);
    }

    private PageModel findPages (String objReferenceId, PageType pageType) {
        HostDataModel vobjectData = getVojbectData(objReferenceId);
        return this.executorService.execute(new PageDataAdapter(vobjectData,
                pageType), objReferenceId);
    }

    @Override
    public PortletModel getSummaryPortlets (final String objReferenceId) {
        log.info("Calling getSummaryPortlets for objReferenceId "
                + objReferenceId);
        return this.executorService
                .execute(new SummaryPortletListDataAdapter(), objReferenceId);
    }

    @Override
    public UserInfoModel getUserInfo (final String objReferenceId) {
        log.info("Calling getUserInfo for objReferenceId " + objReferenceId);
        return this.executorService.execute(new UserInfoDataAdapter(),
                                            objReferenceId);
    }

    @Override
    public ConfigurationListModel getConfigurationLinks (final String objReferenceId) {
        log.info("Calling getConfigurationLinks for serverGuid "
                + objReferenceId);
        return this.executorService
                .execute(new ProductConfigurationDataAdapter(), objReferenceId);
    }

    @Override
    public MenuModel getProductHelpPages (final String objReferenceId) {
        log.info("Calling getProductHelpPages for serverGuid " + objReferenceId);
        return this.executorService.execute(new ProductHelpDataAdapter(),
                                            objReferenceId);
    }

    @Override
    public TableModel getVCCredentials (final String objReferenceId) {
        log.info("Calling getVCCredentials for objReferenceId "
                + objReferenceId);
        return this.executorService.execute(new VCCredentialsDataAdapter(),
                                            objReferenceId);
    }

    @Override
    public StringModel addVCCredential (final String objReferenceId,
                                        final String host,
                                        final String username,
                                        final String password) {
        log.info("Calling addVCCredential for objReferenceId " + objReferenceId);
        PostAdapter<String, StringModel> callable = new AddVCCredentialPostAdapter(
                host, username, password);
        return this.executorService.execute(callable, objReferenceId);
    }

    @Override
    public StringModel deleteVCCredential (final String objReferenceId,
                                           final String host,
                                           final String username,
                                           final String id) {
        log.info("Calling deleteVCCredential for objReferenceId "
                + objReferenceId);
        PostAdapter<String, StringModel> callable = new DeleteVCCredentialPostAdapter(
                host, username, id);
        return this.executorService.execute(callable, objReferenceId);
    }
    
    @Override
    public StringModel editVCCredential (final String objReferenceId,
                                         final String host,
                                         final String username,
                                         final String password,
                                         final String id) {
        log.info("Calling editVCCredential for objReferenceId "
                + objReferenceId);
        PostAdapter<String, StringModel> callable = new EditVCCredentialPostAdapter(
                host, username, password, id);
        return this.executorService.execute(callable, objReferenceId);
    }
    
    @Override
    public TableModel getHPOneViewCredentials (final String objReferenceId) {
        log.info("Calling getHPOneViewCredentials for objReferenceId "
                + objReferenceId);
        return this.executorService.execute(new HPOneViewCredentialsDataAdapter(),
                                            objReferenceId);
    }

    @Override
    public StringModel addHPOneViewCredential (final String objReferenceId,
                                        final String host,
                                        final String username,
                                        final String password) {
        log.info("Calling addHPOneViewCredential for objReferenceId " + objReferenceId);
        CredentialsPostAdapter<String, StringModel> callable = new AddHPOneViewCredentialPostAdapter(
                host, username, password);
        return this.executorService.execute(callable, objReferenceId);
    }

    @Override
    public StringModel deleteHPOneViewCredential (final String objReferenceId,
                                           final String host,
                                           final String username,
                                           final String id) {
        log.info("Calling deleteHPOneViewCredential for objReferenceId "
                + objReferenceId);
        CredentialsPostAdapter<String, StringModel> callable = new DeleteHPOneViewCredentialPostAdapter(
                host, username, id);
        return this.executorService.execute(callable, objReferenceId);
    }
    
    @Override
    public StringModel editHPOneViewCredential (final String objReferenceId,
                                         final String host,
                                         final String username,
                                         final String password,
                                         final String id) {
        log.info("Calling editHPOneViewCredential for objReferenceId "
                + objReferenceId);
        CredentialsPostAdapter<String, StringModel> callable = new EditHPOneViewCredentialPostAdapter(
                host, username, password, id);
        return this.executorService.execute(callable, objReferenceId);
    }

    @Override
    public StringModel getPluginHostName (final String objReferenceId) {
        log.info("Calling getCommonName for objReferenceId " + objReferenceId);
        StringModel model = new StringModel();
        SessionInfo sessionInfo = new SessionInfo(objReferenceId);
        try {
            model.data = SessionManagerImpl.getInstance()
                    .getWSURLHostname(sessionInfo.getSessionId(),
                                      sessionInfo.getServerGuid());
        } catch (Exception e) {
            model.errorMessage = e.getMessage();
        }
        return model;
    }

    @Override
    public StringModel generateSelfSignedCertificate (final String objReferenceId,
                                                      final String[] args) {
        log.info("Calling generateSelfSignedCertificate for objReferenceId "
                + objReferenceId);
        PostAdapter<String, StringModel> callable = new GenerateCertificateRequestPostAdapter(
                args, true);
        return this.executorService.execute(callable, objReferenceId);
    }

    @Override
    public StringModel generateSignedRequest (final String objReferenceId,
                                              final String[] args) {
        log.info("Calling generateSignedCertificate for objReferenceId "
                + objReferenceId);
        PostAdapter<String, StringModel> callable = new GenerateCertificateRequestPostAdapter(
                args, false);
        return this.executorService.execute(callable, objReferenceId);
    }

    @Override
    public StringModel installSignedCertificate (final String objReferenceId,
                                                 final String certificate) {
        log.info("Calling installSignedCertificate for objReferenceId "
                + objReferenceId + " with certificate = \"" + certificate
                + "\"");
        PostAdapter<String, StringModel> callable = new InstallCertificatePostAdapter(
                certificate);
        return this.executorService.execute(callable, objReferenceId);
    }

    @Override
    public CertificateModel getInstalledCertificate(final String objReferenceId) {
        log.info("Calling getInstalledCertificate for objReferenceId " + objReferenceId);
        return this.executorService.execute(new InstallCertificateDataAdapter(), objReferenceId);
    }
    
    @Override
    public String getGenerateSelfSignedHelp (final String objReferenceId) {
        SessionInfo sessionInfo = new SessionInfo(objReferenceId);
        return HelpMenuMaker
                .makeHelpTopicUrl(sessionInfo,
                                  "s_Generate_Self-signed_Certificate.html");
    }

    @Override
    public String getGenerateSignedRequestHelp (final String objReferenceId) {
        SessionInfo sessionInfo = new SessionInfo(objReferenceId);
        return HelpMenuMaker
                .makeHelpTopicUrl(sessionInfo,
                                  "s_Generate_Self-signed_Certificate.html");
    }

    @Override
    public String getInstallCertificateHelp (final String objReferenceId) {
        SessionInfo sessionInfo = new SessionInfo(objReferenceId);
        return HelpMenuMaker.makeHelpTopicUrl(sessionInfo,
                                              "s_Certificate_Upload.html");
    }
    
    @Override
    public MenuModel getProductAbout (final String objReferenceId) {
        log.info("Calling getProductAbout for objReferenceId " + objReferenceId);
        return this.executorService.execute(new AboutDataAdapter(), objReferenceId);
    }
    
    @Override
    public String getICServerProvisioningHelp(final String objReferenceId) {
    	SessionInfo sessionInfo = new SessionInfo(objReferenceId);
    	return HelpMenuMaker.makeHelpTopicUrl(sessionInfo, "configuring_server_provisioning.html");
    }
    
    @Override
    public String getNetworkPreferenceHelp(final String objReferenceId) {
        SessionInfo sessionInfo = new SessionInfo(objReferenceId);
        return HelpMenuMaker.makeHelpTopicUrl(sessionInfo, "setting_network_preferences.html");
    }

    private HostDataModel getVojbectData (final String objReferenceId) {
        if (null == objReferenceId) {
            log.error("getVobjectData was passed a null objReferenceId - returning a NULL HostDataModel.");
            return null;
        }

        HostDataModel model = VOBJECT_MAP.get(objReferenceId);
        if (null == model) {
            log.info("VOBJECT_MAP did not have vObject data for "
                    + objReferenceId + ".  Calling the VObjectNameDataAdapter.");
            model = this.executorService.execute(new VObjectNameDataAdapter(null, null),
                                                 objReferenceId);
            updateVObjectDataMap(objReferenceId, model);
        }
        return model;
    }
    
    public static void updateVObjectDataMap(final String objReferenceId, final HostDataModel vObjectData) {
        if (null != objReferenceId && null != vObjectData) {
            VOBJECT_MAP.put(objReferenceId, vObjectData);
        }
    }
}
