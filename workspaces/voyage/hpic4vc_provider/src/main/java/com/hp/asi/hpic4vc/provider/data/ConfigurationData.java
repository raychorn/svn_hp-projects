package com.hp.asi.hpic4vc.provider.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hp.asi.hpic4vc.provider.adapter.VCPageLinksDataAdapter;
import com.hp.asi.hpic4vc.provider.impl.ProviderExecutorImpl;
import com.hp.asi.hpic4vc.provider.impl.SessionInfo;
import com.hp.asi.hpic4vc.provider.model.BaseModel;
import com.hp.asi.hpic4vc.provider.model.LinkModel;
import com.hp.asi.hpic4vc.provider.model.MenuModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <i>Singleton</i> that caches all basic configuration data for plug-in. This
 * object will only store configuration data that doesn't change while the
 * product is running. For example: things that are set once during initial
 * setup of the product.
 * 
 * @author Andrew Khoury
 */
public class ConfigurationData extends BaseModel {

    private static final ConfigurationData INSTANCE         = new ConfigurationData();
    
    // These two strings are hard coded. May consider getting them from
    // storage_config.json in the future
    private static final String DELETE_VOLUME_PATH          = "/ui_mgmtjsp/pages/DeleteVolume.jsp";
    private static final String DELETE_VOLUME_DISPLAY_NAME  = "Delete Volume";

    /**
     * Stores the Name and Web_Root+Path information for all vObject actions
     * given a vObjectType (does not include parameters)
     */
    private Map<SessionInfo.VObjectType, MenuModel> vObjectActions;
    
    /**
     * URL for storage_root
     */
    private String                                  storageRoot;
    
    private Log                                     log;

    private ConfigurationData () {
        clearCache();
    }
    
    /**
     * Used to clear all data cached in the singleton.
     */
    public void clearCache(){
        vObjectActions            = new HashMap<SessionInfo.VObjectType, MenuModel>();
        storageRoot               = "";
        
        log                       = LogFactory.getLog(this.getClass());
    }

    /**
     * Returns static instance of ConfigurationData
     * 
     * @return
     */
    public static ConfigurationData getInstance () {
        return INSTANCE;
    }

    /**
     * Used to pre-populate the Singleton with the configuration data
     * information so it is more responsive the first time any getter is called.
     * This method call is completely optional.
     * 
     * @param sessionInfo
     *            the session information
     */
    public synchronized void prePopulateCache (final SessionInfo sessionInfo) {
        if (sessionInfo != null) {
            
            //All future populate methods should be listed here
            if(!hasVObjectActions() || storageRoot.compareTo("") == 0){
                populateVObjectActions(sessionInfo);
            }
        }
    }
    
    private synchronized void populateVObjectActions(final SessionInfo sessionInfo){
        log.debug("Populating VObjectActions data");
        new ProviderExecutorImpl().execute(new VCPageLinksDataAdapter(), sessionInfo);
    }

    /**
     * Sets a map of vObjects to vObjectActions (MenuModel). This map should not
     * include parameters for the URL, such as the SessionID as that will be
     * appended in the getter.
     * 
     * @param vObjectActions
     */
    public void setVObjectActions (final Map<SessionInfo.VObjectType, MenuModel> vObjectActions) {
        if (vObjectActions != null) {
            this.vObjectActions = vObjectActions;
            
            if (this.errorMessage != null) {
                log.error(this.errorMessage);
            }
            if (this.informationMessage != null) {
                log.debug(this.informationMessage);
            }
        }
    }

    /**
     * Sets the URL for storage_root
     * @param storageRoot
     */
    public void setStorageRoot (final String storageRoot) {
        //ensures storageRoot will not be set to null
        if (storageRoot != null) {
            this.storageRoot = storageRoot;
        }
    }
    
    /**
     * Get all the VObjectActions (MenuModel) from a given vObject type. Base
     * parameters are appended to all URL's in the MenuModel but the
     * <i>moref</i> is not.
     * 
     * @param vObjectType
     *            the type of vObject
     * @param sessionInfo
     *            the session information
     * @param includeLinksWithoutURL
     *            if set to true the returned MenuModel will include links that
     *            do not have a URL
     * @return MenuModel with all possible actions
     */
    public MenuModel getVObjectActions (final SessionInfo.VObjectType vObjectType,
                                        final SessionInfo sessionInfo,
                                        final boolean includeLinksWithoutURL){
        if (sessionInfo != null && vObjectType != null) {
            prePopulateCache(sessionInfo);
            if (vObjectActions.get(vObjectType) != null) {
                
                MenuModel menu = new MenuModel(vObjectActions.get(vObjectType));
                
                List<LinkModel> linksToRemove = new ArrayList<LinkModel>();

                for (LinkModel link : menu.getMenuItems()) {
                    if (link.url != null && !link.url.equals("")) {
                        link.url += sessionInfo.getBaseParameters();
                    } else {
                        if (!includeLinksWithoutURL) {
                            //adding links without a URL to removal list
                            linksToRemove.add(link);
                        }
                    }
                }
                
                //removing unneeded links from menu
                for (LinkModel link : linksToRemove){
                    menu.getMenuItems().remove(link);
                }
                
                log.info("getVobjectActions is returning " + menu.toString());
                return menu;
            } else {
                log.info("Could not find menu for provided vObjectType");
                return new MenuModel();
            }
        } else {
            log.info("SessionInfo and VObjectType need to be passed to getVObjectActions");
            return new MenuModel();
        }
    }
    
    /**
     * 
     * @return true if VObjectActions has already been populated
     */
    public boolean hasVObjectActions () {
        return (vObjectActions.size() > 0);
    }
    
    /**
     * 
     * @return the URL for storage_root
     */
    public String getStorageRootURL (final SessionInfo sessionInfo) {
        prePopulateCache(sessionInfo);
        return storageRoot;
    }
    
    /**
     * Returns a MenuModel containing all Volume actions.
     * 
     * @param sessionInfo
     *            the session information
     * @return MenuModel with all Volume actions
     */
    public MenuModel getVolumeActions (final SessionInfo sessionInfo){
        if (sessionInfo != null) {
            prePopulateCache(sessionInfo);
            if (!storageRoot.equals("")) {
                
                LinkModel deleteVolumeLink = new LinkModel();
                deleteVolumeLink.displayName = DELETE_VOLUME_DISPLAY_NAME;
                deleteVolumeLink.url = storageRoot + DELETE_VOLUME_PATH
                        + sessionInfo.getBaseParameters();
                MenuModel volumeMenu = new MenuModel();
                volumeMenu.addMenu(deleteVolumeLink);

                return volumeMenu;
            } else {

                log.info("Storage_root not found");
                return new MenuModel();
            }
        }
        log.info("SessionInfo null");
        return new MenuModel();
    }

    @Override
    public String toString () {
        return "ConfigurationData [vObjectActions=" + vObjectActions.toString()
                + ", storageRoot=" + storageRoot + "]";
    }

}
