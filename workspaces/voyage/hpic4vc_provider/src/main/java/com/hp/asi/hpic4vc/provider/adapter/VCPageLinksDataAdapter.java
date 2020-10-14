package com.hp.asi.hpic4vc.provider.adapter;

import java.util.HashMap;
import java.util.Map;

import com.hp.asi.hpic4vc.provider.data.ActionsData;
import com.hp.asi.hpic4vc.provider.data.ConfigurationData;
import com.hp.asi.hpic4vc.provider.data.PageLinksData;
import com.hp.asi.hpic4vc.provider.data.VCPageLinksData;
import com.hp.asi.hpic4vc.provider.impl.SessionInfo;
import com.hp.asi.hpic4vc.provider.impl.SessionInfo.VObjectType;
import com.hp.asi.hpic4vc.provider.locale.I18NProvider;
import com.hp.asi.hpic4vc.provider.model.LinkModel;
import com.hp.asi.hpic4vc.provider.model.MenuModel;

/**
 * Adapts VCPageLinksData to ConfigurationData
 * 
 * @author Andrew Khoury
 * 
 */
public class VCPageLinksDataAdapter extends
        DataAdapter<VCPageLinksData, ConfigurationData> {

    private String storageRoot = null;

    public VCPageLinksDataAdapter () {
        super(VCPageLinksData.class);
    }

    @Override
    public ConfigurationData formatData (VCPageLinksData rawData) {
        Map<SessionInfo.VObjectType, MenuModel> vObjectActions = new HashMap<SessionInfo.VObjectType, MenuModel>();
        
        vObjectActions
                .put(SessionInfo.VObjectType.CLUSTER,
                     makeActionsMenu(rawData.getCluster_overview(),
                                     SessionInfo.VObjectType.CLUSTER));

        vObjectActions
                .put(SessionInfo.VObjectType.DATASTORE,
                     makeActionsMenu(rawData.getDatastore_overview(),
                                     SessionInfo.VObjectType.DATASTORE));

        vObjectActions.put(SessionInfo.VObjectType.HOST,
                           makeActionsMenu(rawData.getHost_overview(),
                                           SessionInfo.VObjectType.HOST));

        vObjectActions.put(SessionInfo.VObjectType.VM,
                           makeActionsMenu(rawData.getVm_overview(),
                                           SessionInfo.VObjectType.VM));

        vObjectActions
                .put(SessionInfo.VObjectType.VM_TEMPLATE,
                     makeActionsMenu(rawData.getVmTemplate_overview(),
                                     SessionInfo.VObjectType.VM_TEMPLATE));

        ConfigurationData.getInstance().setVObjectActions(vObjectActions);
        ConfigurationData.getInstance().setStorageRoot(storageRoot);
        return ConfigurationData.getInstance();
    }

    private MenuModel makeActionsMenu (PageLinksData pageLinksData,
                                       VObjectType vObjectType) {
        MenuModel menuModel = new MenuModel();
        if (pageLinksData != null) {
            ActionsData[] actionsData = pageLinksData.getAction_menu_items();
            for (ActionsData action : actionsData) {
                LinkModel linkModel = new LinkModel();
                if ("Create VM from Template".equalsIgnoreCase(action
                        .getDisplay_name())) {
                    linkModel.displayName = this.i18nProvider
                            .getInternationalString(locale,
                                                    I18NProvider.Actions_Create_VM_From_Template);
                } else if ("Create Datastore".equalsIgnoreCase(action
                        .getDisplay_name())) {
                    linkModel.displayName = this.i18nProvider
                            .getInternationalString(locale,
                                                    I18NProvider.Actions_Create_Datastore);
                } else if ("Clone VM"
                        .equalsIgnoreCase(action.getDisplay_name())) {
                    linkModel.displayName = this.i18nProvider
                            .getInternationalString(locale,
                                                    I18NProvider.Actions_Clone_VM);
                } else if ("Expand Datastore".equalsIgnoreCase(action
                        .getDisplay_name())) {
                    linkModel.displayName = this.i18nProvider
                            .getInternationalString(locale,
                                                    I18NProvider.Actions_Expand_Datastore);
                } else if ("Delete Datastore".equalsIgnoreCase(action
                        .getDisplay_name())) {
                    linkModel.displayName = this.i18nProvider
                            .getInternationalString(locale,
                                                    I18NProvider.Actions_Delete_Datastore);
                } else if ("Delete Volume".equalsIgnoreCase(action
                        .getDisplay_name())) {
                    linkModel.displayName = this.i18nProvider
                            .getInternationalString(locale,
                                                    I18NProvider.Actions_Delete_Volume);
                } else if ("Firmware"
                        .equalsIgnoreCase(action.getDisplay_name())) {
                    switch (vObjectType) {
                        case HOST:
                            linkModel.firmwareForHost = "host";
                            break;
                        default:
                            linkModel.firmwareForHost = "cluster";
                            break;
                    }
                    linkModel.displayName = this.i18nProvider
                            .getInternationalString(locale,
                                                    I18NProvider.Actions_Firmware);

                } else if ("Toggle UID".equalsIgnoreCase(action
                        .getDisplay_name())) {
                    linkModel.displayName = this.i18nProvider
                            .getInternationalString(locale,
                                                    I18NProvider.Actions_ToggleUID);

                } else if ("Power Control".equalsIgnoreCase(action
                        .getDisplay_name())) {
                    linkModel.displayName = this.i18nProvider
                            .getInternationalString(locale,
                                                    I18NProvider.Actions_Power_On);
                } else if ("Rediscover Node".equalsIgnoreCase(action
                        .getDisplay_name())) {
                    linkModel.displayName = this.i18nProvider
                            .getInternationalString(locale,
                                                    I18NProvider.Actions_Rediscover_Node);

                } else {
                    linkModel.displayName = action.getDisplay_name();
                }

                linkModel.url = action.getURL();

                setStorageRoot(action);

                menuModel.addMenu(linkModel);
            }
        }
        return menuModel;
    }

    /**
     * Checks if storage root is not set, checks if web root is the storage root
     * and sets the variable if it is.
     * 
     * @param webRoot
     */
    protected void setStorageRoot (ActionsData action) {
        if ((storageRoot == null || storageRoot.compareTo("") == 0)
                && action.getWeb_root() != null
                && action.getWeb_root().compareTo("storage_root") == 0) {
            storageRoot = action.getURL();

            if (storageRoot != null) {
                try {
                    // Using 7 since it is the searching after "http://"
                    int k = storageRoot.indexOf("/", 7);
                    if (k > 0) {
                        storageRoot = storageRoot.substring(0, k);
                    } else {
                        storageRoot = null;
                    }
                } catch (IndexOutOfBoundsException e) {
                    storageRoot = null;
                }
            }
        }
    }

    @Override
    public ConfigurationData getEmptyModel () {
        return ConfigurationData.getInstance();
    }

    @Override
    public String getServiceName () {
        return "config/pages";
    }

}
