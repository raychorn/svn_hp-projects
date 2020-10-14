package com.hp.asi.hpic4vc.server.provider.model;

import java.util.ArrayList;
import java.util.List;

import com.hp.asi.hpic4vc.provider.model.BaseModel;
import com.hp.asi.hpic4vc.provider.model.TableModel;
import com.hp.asi.hpic4vc.server.provider.data.Nic;
import com.hp.asi.hpic4vc.server.provider.data.network.DVS;
import com.hp.asi.hpic4vc.server.provider.data.network.DataStore;
import com.hp.asi.hpic4vc.server.provider.data.network.VCM;
import com.hp.asi.hpic4vc.server.provider.data.network.VS;

public class NetworkDiagramDetailModel extends BaseModel {

	public List<DVS> dvss;
	public List<Nic> nics;
	public List<VS> vss;
	public VCM vcm;
	public List<DataStore> ds;
	
	public NetworkDiagramDetailModel() 
	{
        super();      
        
        ds = new ArrayList<DataStore>();
        vss = new ArrayList<VS>();
		dvss = new ArrayList<DVS>();
		nics = new ArrayList<Nic>();
		vcm = new VCM();
        
    }
}
