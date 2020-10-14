package com.hp.asi.hpic4vc.server.provider.adapter;

import java.util.ArrayList;
import java.util.List;

import com.hp.asi.hpic4vc.provider.adapter.DataAdapter;
import com.hp.asi.hpic4vc.server.provider.data.network.DVS;
import com.hp.asi.hpic4vc.server.provider.data.network.DataStore;
import com.hp.asi.hpic4vc.server.provider.data.network.Enclosure;
import com.hp.asi.hpic4vc.server.provider.data.network.ExternalStorage;
import com.hp.asi.hpic4vc.server.provider.data.network.ExternalSwitch;
import com.hp.asi.hpic4vc.server.provider.data.network.Fabric;
import com.hp.asi.hpic4vc.server.provider.data.network.NetworkDetailResult;
import com.hp.asi.hpic4vc.server.provider.data.network.PortGroup;
import com.hp.asi.hpic4vc.server.provider.data.network.UplinkPortGroup;
import com.hp.asi.hpic4vc.server.provider.data.network.VCNetwork;
import com.hp.asi.hpic4vc.server.provider.data.network.VCUplink;
import com.hp.asi.hpic4vc.server.provider.data.network.VS;
import com.hp.asi.hpic4vc.server.provider.data.network.VcModule;
import com.hp.asi.hpic4vc.server.provider.data.Nic;
import com.hp.asi.hpic4vc.server.provider.model.DistributedVirtualSwitchModel;
import com.hp.asi.hpic4vc.server.provider.model.DownlinkPortGroupModel;
import com.hp.asi.hpic4vc.server.provider.model.NetworkDetailModel;
import com.hp.asi.hpic4vc.server.provider.model.NetworkDiagramDetailModel;
import com.hp.asi.hpic4vc.server.provider.model.VCFabricModel;
import com.hp.asi.hpic4vc.server.provider.model.VCNetworkModel;
import com.hp.asi.hpic4vc.server.provider.model.VCUplinkModel;
import com.hp.asi.hpic4vc.server.provider.model.VirtualConnectModuleModel;
import com.hp.asi.hpic4vc.server.provider.model.VirtualSwitchModel;


public class NetworkDiagramAdapter extends DataAdapter<NetworkDetailResult, NetworkDiagramDetailModel>
{
	private static final String SERVICE_NAME = "services/host/networkdetail";
	
	public NetworkDiagramAdapter()
	{
		super(NetworkDetailResult.class);
    }

	@Override
	public String getServiceName()
	{
		return SERVICE_NAME;
	}

	private NetworkDiagramDetailModel formatNics(NetworkDetailResult rawData, NetworkDiagramDetailModel model)
	{
		List<Nic>nics = rawData.getNics(); 
		if(nics.isEmpty())
		{
			return model;
		}
		
		model.nics = rawData.getNics();
		
		return model;
	}
	
	private NetworkDiagramDetailModel formatVirtualSwitches(NetworkDetailResult rawData, NetworkDiagramDetailModel model)
	{
		List<VS>vss = rawData.getVss();
		if(vss.isEmpty())
		{
			return model;
		}
							
		model.vss = rawData.getVss();
		return model;
	}	
	
	private NetworkDiagramDetailModel formatDistributedVirtualSwitches(NetworkDetailResult rawData, NetworkDiagramDetailModel model)
	{
		List<DVS>dvss = rawData.getDvss();
		if(dvss.isEmpty())
		{
			return model;
		}
		
		model.dvss = rawData.getDvss();
							
		return model;
	}	
	
	private NetworkDiagramDetailModel formatVirtualConnectModules(NetworkDetailResult rawData, NetworkDiagramDetailModel model)
	{
		model.vcm = rawData.getVcm();
		return model;
	}
	
	private NetworkDiagramDetailModel formatDataStoreModules(NetworkDetailResult rawData, NetworkDiagramDetailModel model){
		
		List<DataStore> dsList = rawData.getDatastores();
		
		if(dsList.isEmpty())
		{
			return model;
		}
		
		for (DataStore item : dsList)
		{
			model.ds.add(item);
		}
		
		return model;
		
	}
	
	
		
	@Override
	public NetworkDiagramDetailModel formatData(NetworkDetailResult rawData)
	{
        log.debug("NetworkDetailAdapter beginning" );
        
		NetworkDiagramDetailModel model = new NetworkDiagramDetailModel();
		
		if (rawData.hasError()) 
		{
            log.debug("NetworkDetailResults had an error message.  " +
                    "Returning a NetworkDiagramDetailModel with the error message");
            model.errorMessage = rawData.getErrorMessage();
            return model;
        }
		
		if(rawData.getNics().isEmpty())
		{
			model.errorMessage = "No Data in Network Detail Result.";
			return model;
		}
				
		model = this.formatNics(rawData, model);
		model = this.formatVirtualSwitches(rawData, model);
		model = this.formatDistributedVirtualSwitches(rawData, model);
		model = this.formatVirtualConnectModules(rawData, model);
		model = this.formatDataStoreModules(rawData, model);
		
        log.debug("NetworkDetailAdapter the end" );
		log.debug(model.toString());
		return model;
	}

	@Override
	public NetworkDiagramDetailModel getEmptyModel()
	{
		return new NetworkDiagramDetailModel();
	}

}
