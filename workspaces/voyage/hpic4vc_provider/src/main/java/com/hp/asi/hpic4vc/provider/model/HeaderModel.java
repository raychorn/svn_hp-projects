/**
 * Copyright 2012 Hewlett-Packard Development Company, L.P.
 */

package com.hp.asi.hpic4vc.provider.model;

import java.util.ArrayList;
import java.util.List;

public class HeaderModel extends BaseModel {
    public HealthModel     health;
    public String          objReferenceName;
    public String          objReferenceType;
    public String          productInfo;
    public String          enclosureInfo;
    public boolean         showRefreshHover;
    public List<LinkModel> refresh;
    public List<TaskModel> tasks;
    public List<LinkModel> actions;
    public List<LinkModel> configurations;
    public String          helpUrl;
    
    /**
     * An empty constructor is required for the AMF serialization to work!
     */
    public HeaderModel() {
        super();
        this.health             = new HealthModel();
        this.objReferenceName   = "";
        this.objReferenceType   = "";
        this.productInfo        = "";
        this.enclosureInfo      = "";
        this.showRefreshHover   = false;
        this.refresh            = new ArrayList<LinkModel>();
        this.tasks              = new ArrayList<TaskModel>();
        this.actions            = new ArrayList<LinkModel>();
        this.configurations     = new ArrayList<LinkModel>();
        this.helpUrl            = null;
    }    
        

	@Override
	public String toString() {
		return "HeaderModel [health=" + health + ", objReferenceName="
				+ objReferenceName + ", objReferenceType" + objReferenceType
				+ ", productInfo=" + productInfo
				+ ", enclosureInfo=" + enclosureInfo + ", showRefreshHover="
				+ showRefreshHover + ", refresh=" + refresh + ", tasks="
				+ tasks + ", actions=" + actions + ", configurations="
				+ configurations + ", helpUrl=" + helpUrl + ", errorMessage="
				+ errorMessage + ", informationMessage=" + informationMessage
				+ "]";
	}

}
