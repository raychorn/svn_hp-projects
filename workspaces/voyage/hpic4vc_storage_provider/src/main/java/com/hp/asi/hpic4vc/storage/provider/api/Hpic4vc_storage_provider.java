package com.hp.asi.hpic4vc.storage.provider.api;

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
import com.hp.asi.hpic4vc.storage.provider.dam.model.AllStorageSystems;
import com.hp.asi.hpic4vc.storage.provider.dam.model.HierarchialDataModel;
import com.hp.asi.hpic4vc.storage.provider.dam.model.StorageSystemOverviewModel;


/**
 * Interface used to test a java service call from the Flex UI.
 *
 * It must be declared as osgi:service with the same name in
 * main/resources/META-INF/spring/bundle-context-osgi.xml
 */
public interface Hpic4vc_storage_provider {
   /**
    * @return the same message it received
    */
   String echo(String message);

   /**
    * @param objtReferenceId Provided by the hpic4vc_ui and contains the
    *                        uid of the object that provides the session 
    *                        key, vCenter URL, locale, etc.
    *                        
    * @return A table of HBAs.
    */
   public TableModel getHbas(final String objReferenceId);

   /**
    * @param objectReference Provided by the hpic4vc_ui and contains the
    *                        uid of the object that provides the session
    *                        key, vCenter URL, locale, etc.
    *                        
    * @return A table of storage volume data.
    */
   public TableModel getStorageVolumes(final String objReferenceId);

   /**
    * @param objectReference Provided by the hpic4vc_ui and contains the
    *                        uid of the object that provides the session
    *                        key, vCenter URL, locale, etc.
    *                        
    * @return A table of paths data.
    */
   public TableModel getPaths(final String objReferenceId);

   /**
    * @param objectReference Provided by the hpic4vc_ui and contains the
    *                        uid of the object that provides the session
    *                        key, vCenter URL, locale, etc.
    *                        
    * @return A table of replication data.
    */
   public TableModel getReplications(final String objReferenceId);

   /**
    * @param objectReference Provided by the hpic4vc_ui and contains the
    *                        uid of the object that provides the session
    *                        key, vCenter URL, locale, etc.
    *                        
    * @return A table of vms to volumes data.
    */
   public TableModel getVmsToVolumes(final String objReferenceId);

   /**
    * @param objectReference Provided by the hpic4vc_ui and contains the
    *                        uid of the object that provides the session
    *                        key, vCenter URL, locale, etc.
    *                        
    * @return A table of virtual disks data.
    */
   public TableModel getVirtualDisks(final String objReferenceId);
   
   /**
    * @param objectReference Provided by the hpic4vc_ui and contains the
    *                        uid of the object that provides the session
    *                        key, vCenter URL, locale, etc.
    *                        
    * @return A model with the full summary data.
    */
   public FullSummaryModel getFullSummary(final String objReferenceId);

   /**
    * @param objectReference Provided by the hpic4vc_ui and contains the
    *                        uid of the object that provides the session
    *                        key, vCenter URL, locale, etc.
    *                        
    * @return A portlet with summary data for the summary box.
    */
   public SummaryPortletModel getSummaryPortletData(final String objReferenceId); 
   
   /**
    * @param objectReference Provided by the hpic4vc_ui and contains the
    *                        uid of the object that provides the session
    *                        key, vCenter URL, locale, etc.
    *                        
    * @return A portlet with array summary information for the given vObject.
    *         This data is used for the "Overview" page on the "Manage" tab.
    */
   public StorageOverviewModel getStorageOverview(final String objReferenceId);  
   
   /**
    * @param objectReference Provided by the hpic4vc_ui and contains the
    *                        uid of the object that provides the session
    *                        key, vCenter URL, locale, etc.
    *                         
    */
   public RefreshCacheModel startRefreshCache(final String objReferenceId);
   
   /**
    * @param objectReference Provided by the hpic4vc_ui and contains the
    *                        uid of the object that provides the session
    *                        key, vCenter URL, locale, etc.
    *    
    * @return null (the refresh operation in progress is cancelled)
    */
   public void cancelRefreshCache(final String objReferenceId);
   
   /**
    * @param objectReference Provided by the hpic4vc_ui and contains the
    *                        uid of the object that provides the session
    *                        key, vCenter URL, locale, etc.
    *    
    * @return RefreshCacheModel 
    */
   public RefreshCacheModel restartRefreshCache(final String objReferenceId);

   /**
    * @param objectReference Provided by the hpic4vc_ui and contains the
    *                        uid of the object that provides the session
    *                        key, vCenter URL, locale, etc.
    *    
    * @return RefreshCacheModel (only isPopulating field will be used)
    */
   public RefreshCacheModel getRefreshCacheStatus(final String objReferenceId);

   /**
    * @param objectReference Provided by the hpic4vc_ui and contains the
    *                        uid of the object that provides the session
    *                        key, vCenter URL, locale, etc.
    *                        
    * @return A list objects of type PageModel that contains the detailed
    *         Storage pages information that needs to be displayed in the
    *         plugin.
    */
   public PageModel getStoragePages(final String objReferenceId);
   
   /**
    * @param objectReference Provided by the hpic4vc_ui and contains the
    *                        uid of the object that provides the session
    *                        key, vCenter URL, locale, etc.
    *                        
    * @return A MenuModel containing all of the available provisioning tasks
    *                     for the vObject.
    */
   public MenuModel getProvisioningTasks(final String objReferenceId);
   
   /**
    * @param objectReference Provided by the hpic4vc_ui and contains the
    *                        uid of the object that provides the session
    *                        key, vCenter URL, locale, etc.
    *                        
    * @return A list of arrays and their uids.
    */
   public AllStorageSystems getArraysBasicInfo(final String objReferenceId);   
   
   /**
    * @param objectReference  Provided by the hpic4vc_ui and contains the
    *                         server guid.
    *                        
    * @param storageSystemUid Provided by the hpic4vc_ui and contains the
    *                         uid of the storage system for which to collect data.
    *                        
    * @return An object of type HeaderModel that contains the following
    *         information for a storage system: name, id, type, health status, 
    *         etc...
    */
   public HeaderModel getStorageSystemHeader(final String objReferenceId,
                                             final String storageSystemUid);
   
   /**
    * @param objectReference  Provided by the hpic4vc_ui and contains the
    *                         server guid.
    *                        
    * @return An object of type StringModel that contains the 
    * DAM online help url.
    */
   public StringModel getDAMHelp(final String objReferenceId);
   
   /**
    * Returns a Foot Model specific to a selected Storage System in the
    * "HP Infrastructure" DAM pages.
    * @param objReferenceId    The serverGuid
    * @param storageSystemUid  The unique identifier of the selected storage system.
    * @return
    */
   public FooterModel getStorageSystemFooter(final String objReferenceId,
                                             final String storageSystemUid);

    /**
     * @param objectReference
     *            Provided by the hpic4vc_ui and contains the server guid.
     * 
     * @param storageSystemUid
     *            Provided by the hpic4vc_ui and contains the uid of the storage
     *            system for which to collect data.
     * 
     * @return The overview data about a specific storage system.  This data
     *         was designed to populate the portlet directly below the header
     *         on the DAM "Summary" page.
     */
    public StorageSystemOverviewModel getStorageSystemOverview(String objReferenceId,
                                                      String storageSystemUid);
    
    /**
     * @param objectReference  Provided by the hpic4vc_ui and contains the
     *                         server guid.
     *                        
     * @param storageSystemUid Provided by the hpic4vc_ui and contains the
     *                         uid of the array for which to collect data.
     *                        
     * @return Detailed information about a specific array.
     */
    public HierarchialDataModel getStorageSystemDetails(final String objReferenceId,
                                                           final String storageSystemUid);
    
    public HierarchialDataModel getStorageSetDetails(final String objReferenceId,
			 final String storageSystemUid);
}
