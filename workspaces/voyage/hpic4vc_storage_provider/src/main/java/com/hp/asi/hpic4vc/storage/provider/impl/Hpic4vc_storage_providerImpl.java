package com.hp.asi.hpic4vc.storage.provider.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.asi.hpic4vc.provider.adapter.ActionsDataAdapter;
import com.hp.asi.hpic4vc.provider.adapter.HelpMenuMaker;
import com.hp.asi.hpic4vc.provider.adapter.RefreshMenuDataAdapter;
import com.hp.asi.hpic4vc.provider.api.ProviderExecutor;
import com.hp.asi.hpic4vc.provider.impl.ProviderExecutorImpl;
import com.hp.asi.hpic4vc.provider.impl.SessionInfo;
import com.hp.asi.hpic4vc.provider.model.FooterModel;
import com.hp.asi.hpic4vc.provider.model.FullSummaryModel;
import com.hp.asi.hpic4vc.provider.model.HeaderModel;
import com.hp.asi.hpic4vc.provider.model.MenuModel;
import com.hp.asi.hpic4vc.provider.model.PageModel;
import com.hp.asi.hpic4vc.provider.model.RefreshCacheModel;
import com.hp.asi.hpic4vc.provider.model.StorageOverviewModel;
import com.hp.asi.hpic4vc.provider.model.StringModel;
import com.hp.asi.hpic4vc.provider.model.SummaryPortletModel;
import com.hp.asi.hpic4vc.provider.model.TableModel;
import com.hp.asi.hpic4vc.storage.provider.adapter.BriefSummaryDataAdapter;
import com.hp.asi.hpic4vc.storage.provider.adapter.CancelRefreshCacheDataAdapter;
import com.hp.asi.hpic4vc.storage.provider.adapter.FullSummaryDataAdapter;
import com.hp.asi.hpic4vc.storage.provider.adapter.HBADataAdapter;
import com.hp.asi.hpic4vc.storage.provider.adapter.PathsDataAdapter;
import com.hp.asi.hpic4vc.storage.provider.adapter.RefreshCacheStatusDataAdapter;
import com.hp.asi.hpic4vc.storage.provider.adapter.ReplicationsDataAdapter;
import com.hp.asi.hpic4vc.storage.provider.adapter.RestartRefreshCacheDataAdapter;
import com.hp.asi.hpic4vc.storage.provider.adapter.StartRefreshCacheDataAdapter;
import com.hp.asi.hpic4vc.storage.provider.adapter.StoragePageDataAdapter;
import com.hp.asi.hpic4vc.storage.provider.adapter.StorageVolumesDataAdapter;
import com.hp.asi.hpic4vc.storage.provider.adapter.SummaryPortletDataAdapter;
import com.hp.asi.hpic4vc.storage.provider.adapter.VirtualDiskDataAdapter;
import com.hp.asi.hpic4vc.storage.provider.adapter.VmsToVolumesDataAdapter;
import com.hp.asi.hpic4vc.storage.provider.adapter.storagesystem.ArrayBasicInfoDataAdapter;
import com.hp.asi.hpic4vc.storage.provider.adapter.storagesystem.FooterDataAdapter;
import com.hp.asi.hpic4vc.storage.provider.adapter.storagesystem.HeaderDataAdapter;
import com.hp.asi.hpic4vc.storage.provider.adapter.storagesystem.StorageSetDetailsDataAdapter;
import com.hp.asi.hpic4vc.storage.provider.adapter.storagesystem.StorageSystemDetailsDataAdapter;
import com.hp.asi.hpic4vc.storage.provider.adapter.storagesystem.StorageSystemOverviewDataAdapter;
import com.hp.asi.hpic4vc.storage.provider.adapter.storagesystem.StorageSystemSettingsDataAdapter;
import com.hp.asi.hpic4vc.storage.provider.api.Hpic4vc_storage_provider;
import com.hp.asi.hpic4vc.storage.provider.dam.model.AllStorageSystems;
import com.hp.asi.hpic4vc.storage.provider.dam.model.HierarchialDataModel;
import com.hp.asi.hpic4vc.storage.provider.dam.model.StorageSystemOverviewModel;

/**
 * Implementation of the Hpic4vc_storage_provider interface
 */
public class Hpic4vc_storage_providerImpl implements Hpic4vc_storage_provider {
    private Log log;
    private ProviderExecutor executorService;
    
	public Hpic4vc_storage_providerImpl() {
        this.log             = LogFactory.getLog(this.getClass());
        this.executorService = new ProviderExecutorImpl();
        log.info("Hpic4vc_storage_provider has started.");
	}

	public String echo(String message) {
	    log.info("Calling echo for message " + message);
		return message;
	}

	@Override
	public TableModel getHbas(final String objReferenceId) {
	    log.info("Calling getHbas for objReferenceId " + objReferenceId);
	    return this.executorService.execute(new HBADataAdapter(), objReferenceId);
	}

	@Override
	public TableModel getStorageVolumes(final String objReferenceId) {
	    log.info("Calling getStorageVolumes for objReferenceId " + objReferenceId);
	    return this.executorService.execute(new StorageVolumesDataAdapter(), objReferenceId);
	}

	@Override
	public TableModel getPaths(final String objReferenceId) {
	    log.info("Calling getPaths for objReferenceId " + objReferenceId);
		return this.executorService.execute(new PathsDataAdapter(), objReferenceId);
	}

	@Override
	public TableModel getReplications(final String objReferenceId) {
	    log.info("Calling getReplications for objReferenceId " + objReferenceId);
		return this.executorService.execute(new ReplicationsDataAdapter(), objReferenceId);
	}

	@Override
	public TableModel getVirtualDisks(final String objReferenceId) {
	    log.info("Calling getVirtualDisks for objReferenceId " + objReferenceId);
		return this.executorService.execute(new VirtualDiskDataAdapter(), objReferenceId);
	}

	@Override
	public TableModel getVmsToVolumes(final String objReferenceId) {
	    log.info("Calling getVmsToVolumes for objReferenceId " + objReferenceId);
		return this.executorService.execute(new VmsToVolumesDataAdapter(), objReferenceId);
	}
	
    @Override
    public FullSummaryModel getFullSummary (String objReferenceId) {
        log.info("Calling getFullSummary for objReferenceId " + objReferenceId);
        return this.executorService.execute(new FullSummaryDataAdapter(), objReferenceId);
    }
    
    @Override
    public SummaryPortletModel getSummaryPortletData(final String objReferenceId) {
        log.info("Calling getSummaryPortletData for objReferenceId " + objReferenceId);
        return this.executorService.execute(new SummaryPortletDataAdapter(), objReferenceId);
    }
  
    @Override
    public StorageOverviewModel getStorageOverview (String objReferenceId) {
        log.info("Calling storageOverview for objReferenceId " + objReferenceId);
        return this.executorService.execute(new BriefSummaryDataAdapter(), objReferenceId);
    }
    
	@Override
    public RefreshCacheModel startRefreshCache(final String objReferenceId) {
	    log.info("Calling startRefreshCache for objReferenceId " + objReferenceId);
        return this.executorService.execute(new StartRefreshCacheDataAdapter(),objReferenceId);                
    }

	@Override
    public void cancelRefreshCache(final String objReferenceId) {
	    log.info("Calling cancelRefreshCache for objReferenceId " + objReferenceId);
        this.executorService.execute(new CancelRefreshCacheDataAdapter(), objReferenceId);                
        return;
    }

    @Override
    public RefreshCacheModel restartRefreshCache(final String objReferenceId) {
        log.info("Calling restartRefreshCache for objReferenceId " + objReferenceId);
        return this.executorService.execute(new RestartRefreshCacheDataAdapter(), objReferenceId);               
    }

    @Override
    public RefreshCacheModel getRefreshCacheStatus(final String objReferenceId) {
        log.info("Calling getRefreshCacheStatus for objReferenceId " + objReferenceId);
        return this.executorService.execute(new RefreshCacheStatusDataAdapter(), objReferenceId);                
    }

    @Override
    public PageModel getStoragePages(final String objReferenceId) {
        log.info("Calling getStoragePages for objReferenceId " + objReferenceId);
        return this.executorService.execute(new StoragePageDataAdapter(), objReferenceId);
    }
    
    @Override
    public MenuModel getProvisioningTasks(final String objReferenceId) {
        log.info("Calling getProvisioningTasks for objReferenceId " + objReferenceId);
        SessionInfo sessionInfo = new SessionInfo(objReferenceId);
        return new ActionsDataAdapter(sessionInfo).getActionsData();
    }
    
    @Override
    public AllStorageSystems getArraysBasicInfo(final String objReferenceId) {
        log.info("Calling getArraysBasicInfo for objReferenceId " + objReferenceId);
        return this.executorService.execute(new ArrayBasicInfoDataAdapter(), objReferenceId);
    }
    
    public MenuModel getStorageSystemGearMenu(final String objReferenceId) {
        log.info("Calling getStorageSystemGearMenu for objReferenceId " + objReferenceId);
        return this.executorService.execute(new StorageSystemSettingsDataAdapter(), objReferenceId);
    }
    
    @Override
    public HeaderModel getStorageSystemHeader(final String objReferenceId,
                                              final String storageSystemUid) {
        log.info("Calling getStorageSystemHeader for storage system UID " + storageSystemUid);
        HeaderModel headerModel = this.executorService.execute
                (new HeaderDataAdapter(storageSystemUid), objReferenceId);
        
        // HeaderModel.configurations
        MenuModel configList = getStorageSystemGearMenu(objReferenceId);
        headerModel.configurations = configList.getMenuItems();
        
        // HeaderModel.Refresh
        MenuModel refreshList = this.executorService.execute
                (new RefreshMenuDataAdapter(), objReferenceId);
        headerModel.refresh = refreshList.getMenuItems();
        if (refreshList.menuItems.size() > 1) {
            headerModel.showRefreshHover = true;
        }
        
        // HeaderModel.helpUrl
        SessionInfo sessionInfo = new SessionInfo(objReferenceId);
        headerModel.helpUrl = HelpMenuMaker.makeHelpTopicUrl(sessionInfo, "HP_Infrastructure_page.html");
        
        return headerModel;
    }
    
    @Override
    public StringModel getDAMHelp(final String objReferenceId) {
    	SessionInfo sessionInfo = new SessionInfo(objReferenceId);
        StringModel stringModel = new StringModel();
        stringModel.data = HelpMenuMaker.makeHelpTopicUrl(sessionInfo, "HP_Infrastructure_page.html");
        return stringModel;
    }
    @Override
    public FooterModel getStorageSystemFooter(final String objReferenceId,
                                              final String storageSystemUid) {
        log.info("Calling getStorageSystemFooter for storage system UID " + storageSystemUid);
        return this.executorService.execute(new FooterDataAdapter(storageSystemUid), objReferenceId);
    }

    @Override
    public StorageSystemOverviewModel getStorageSystemOverview (final String objReferenceId,
                                                   final String storageSystemUid) {
        log.info("Calling getStorageSystemOverview for UID " + storageSystemUid);
        return this.executorService.execute
                (new StorageSystemOverviewDataAdapter(storageSystemUid), objReferenceId);
    }
    
    @Override
    public HierarchialDataModel getStorageSystemDetails(final String objReferenceId,
                                                           final String storageSystemUid) {
        log.info("Calling getStorageSystemDetails for storage system UID " + storageSystemUid);
        return this.executorService.execute
                (new StorageSystemDetailsDataAdapter(storageSystemUid), objReferenceId);
    }
    
    @Override
    public HierarchialDataModel getStorageSetDetails(final String objReferenceId,
    												 final String storageSystemUid) {
    	log.info("Calling getStorageSetDetails for storage system UID " + storageSystemUid);
        return this.executorService.execute
                (new StorageSetDetailsDataAdapter(storageSystemUid), objReferenceId);
    }
    
}
