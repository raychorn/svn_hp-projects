package com.hp.asi.hpic4vc.server.provider.model;

import com.hp.asi.hpic4vc.provider.model.LabelValueListModel;
import com.hp.asi.hpic4vc.provider.model.LabelValueModel;
import com.hp.asi.hpic4vc.provider.model.TableModel;

public class ClusterHostDetail  extends com.hp.asi.hpic4vc.provider.model.BaseModel{
	
	public LabelValueListModel hostInfo;
	public LabelValueListModel serverStatus;
	public LabelValueListModel serverPower;
	
	public LabelValueModel powerCostAdvantage;
	
	public TableModel memoryInfo;
	public TableModel cpuInfo;
	public TableModel firmwareInfo;
	public TableModel softwareInfo;
	public TableModel iloLog;
	public TableModel imlLog;
	
	public ClusterHostDetail() 
	{
        super();      
        this.hostInfo = new LabelValueListModel();
        this.serverStatus = new LabelValueListModel();
        this.serverPower = new LabelValueListModel();
        
        this.powerCostAdvantage = new LabelValueModel();
        
        this.memoryInfo = new TableModel();
        this.cpuInfo = new TableModel();
        this.firmwareInfo = new TableModel();
        this.softwareInfo = new TableModel();
        this.iloLog = new TableModel();
        this.imlLog = new TableModel();
        
    }

	

}
