package com.hp.asi.hpic4vc.provider.data;

import javax.xml.bind.annotation.XmlElement;

import com.hp.asi.hpic4vc.provider.model.BaseModel;

public class PageLinksData extends BaseModel {
    private ActionsData[]   action_menu_items;
    private SettingsData[]  settings;
    private MoreMenuData[]  more_menu_items;

    public PageLinksData () {
        action_menu_items   = null;
        settings            = null;
        more_menu_items     = null;
    }

    @XmlElement(name = "action_menu_items")
    public ActionsData[] getAction_menu_items () {
        if (this.action_menu_items == null) {
            return new ActionsData[0];
        }
        return action_menu_items;
    }

    public void setAction_menu_items (ActionsData[] action_menu_items) {
        this.action_menu_items = action_menu_items;
    }

    @XmlElement(name = "settings")
    public SettingsData[] getSettings () {
        if (this.settings == null) {
            return new SettingsData[0];
        }
        return settings;
    }

    public void setSettings (SettingsData[] settings) {
        this.settings = settings;
    }

    @XmlElement(name = "more_menu_items")
    public MoreMenuData[] getMore_menu_items () {
        if (this.more_menu_items == null) {
            return new MoreMenuData[0];
        }
        return more_menu_items;
    }

    public void setMore_menu_items (MoreMenuData[] more_menu_items) {
        this.more_menu_items = more_menu_items;
    }

}
