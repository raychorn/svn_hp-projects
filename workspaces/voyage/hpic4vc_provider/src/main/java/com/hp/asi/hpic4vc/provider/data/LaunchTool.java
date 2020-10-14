package com.hp.asi.hpic4vc.provider.data;

import java.util.Arrays;


public class LaunchTool {
    
    private String id;
    private String errorMessage;
    private String icon_url;
    private LaunchLink[] launch_links;
    private String force_menu;
    
    public LaunchTool () {
        this.launch_links = null;       
    }
    
    public String getId () {
        return id;
    }

    public String getErrorMessage () {
        return errorMessage;
    }

    public String getIcon_url () {
        return icon_url;
    }
    
    public void setId (String id) {
        this.id = id;
    }

    public void setErrorMessage (String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void setIcon_url (String icon_url) {
        this.icon_url = icon_url;
    }

    public void setLaunch_links (LaunchLink[] launch_links) {
        this.launch_links = launch_links;
    }

    public LaunchLink[] getLaunch_links () {
        return launch_links;
    }
    
    public String getForce_menu () {
        return force_menu;
    }

    public void setForce_menu (String force_menu) {
        this.force_menu = force_menu;
    }

    @Override
    public String toString () {
        return "LaunchTool [id=" + id + ", errorMessage=" + errorMessage
                + ", icon_url=" + icon_url + ", launch_links="
                + Arrays.toString(launch_links) + ", force_menu=" + force_menu
                + "]";
    }


}
