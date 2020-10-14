package com.hp.asi.hpic4vc.server.provider.model;


import java.util.ArrayList;
import java.util.List;

import com.hp.asi.hpic4vc.provider.model.BaseModel;
import com.hp.asi.hpic4vc.provider.model.TableModel;
import com.hp.asi.hpic4vc.server.provider.data.network.DataStore;
import com.hp.asi.hpic4vc.server.provider.data.network.VCM;


public class NetworkDetailModel extends BaseModel
{
		
	public TableModel nics;
	public TableModel externalSwitches;
	public TableModel externalStorage;
	
	public List<VirtualSwitchModel> virtualSwitches;
	public List<DistributedVirtualSwitchModel> distributedVirtualSwitches;
	public List<VirtualConnectModuleModel> vcms;
	public List<DataStore> ds;
	
	public VCM vcm;
	
	public NetworkDetailModel() 
	{
        super();      
        nics = new TableModel();
        externalSwitches = new TableModel();
        externalStorage = new TableModel();
        
        virtualSwitches = new ArrayList<VirtualSwitchModel>();
        distributedVirtualSwitches = new ArrayList<DistributedVirtualSwitchModel>();
        vcms = new ArrayList<VirtualConnectModuleModel>();
        ds = new ArrayList<DataStore>();
        
    }
		
}
