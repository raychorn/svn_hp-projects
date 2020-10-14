/* Copyright 2012 Hewlett-Packard Development Company, L.P.  */
package com.hp.asi.hpic4vc.provider.model;

import java.util.ArrayList;
import java.util.List;

public class MenuModel  extends BaseModel{
    public List<LinkModel> menuItems;
    
    public MenuModel() {
        super();
        this.menuItems = new ArrayList<LinkModel>();
    }
    
    /**
     * Copy constructor
     * @param menuModel
     */
    public MenuModel(MenuModel menuModel){
        this.menuItems = new ArrayList<LinkModel>();
        
        if (menuModel != null) {
            this.errorMessage = menuModel.errorMessage;
            this.informationMessage = menuModel.informationMessage;

            for (LinkModel link : menuModel.getMenuItems()) {
                addMenu(new LinkModel(link));
            }
        }
    }
    
    public void addMenu(final LinkModel menuItem) {
        this.menuItems.add(menuItem);
    }

    public List<LinkModel> getMenuItems () {
        return menuItems;
    }

	@Override
	public String toString() {
		return "MenuModel [menuItems=" + menuItems + ", errorMessage="
				+ errorMessage + ", informationMessage=" + informationMessage
				+ "]";
	}

}
