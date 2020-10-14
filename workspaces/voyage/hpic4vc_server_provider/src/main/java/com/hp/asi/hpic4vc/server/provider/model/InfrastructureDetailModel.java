package com.hp.asi.hpic4vc.server.provider.model;


import java.util.ArrayList;
import java.util.List;

import com.hp.asi.hpic4vc.provider.model.BaseModel;
import com.hp.asi.hpic4vc.provider.model.TableModel;
import com.hp.asi.hpic4vc.provider.model.LabelValueListModel;

public class InfrastructureDetailModel extends BaseModel
{
	public LabelValueListModel enclosure;
	public LabelValueListModel power;
	public LabelValueListModel thermal;	
	
	public TableModel fans;
	public TableModel powerSupplies;
	public TableModel interconnects;
	public TableModel oaModules;
	public List<String> syslog;
	
	public InfrastructureDetailModel() 
	{
        super();      
        this.enclosure = new LabelValueListModel();
        this.power = new LabelValueListModel();
        this.thermal = new LabelValueListModel();
        
        this.fans = new TableModel();
        this.powerSupplies = new TableModel();
        this.interconnects = new TableModel();
        this.oaModules = new TableModel();
        this.syslog = new ArrayList<String>();        
    }
		
}
