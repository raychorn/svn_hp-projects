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
import com.hp.asi.hpic4vc.server.provider.model.VCFabricModel;
import com.hp.asi.hpic4vc.server.provider.model.VCNetworkModel;
import com.hp.asi.hpic4vc.server.provider.model.VCUplinkModel;
import com.hp.asi.hpic4vc.server.provider.model.VirtualConnectModuleModel;
import com.hp.asi.hpic4vc.server.provider.model.VirtualSwitchModel;


public class NetworkDetailAdapter extends DataAdapter<NetworkDetailResult, NetworkDetailModel>
{
	private static final String SERVICE_NAME = "services/host/networkdetail";
	private static final String ZERO = "0.0";
	private static final String LINK_SPEED = "Not Linked";
	
	public NetworkDetailAdapter()
	{
		super(NetworkDetailResult.class);
    }

	@Override
	public String getServiceName()
	{
		return SERVICE_NAME;
	}

	private NetworkDetailModel formatNics(NetworkDetailResult rawData, NetworkDetailModel model)
	{
		List<Nic>nics = rawData.getNics(); 
		if(nics.isEmpty())
		{
			return model;
		}
		
		model.nics.columnNames.add("VM NIC");
		model.nics.columnNames.add("Physical NIC");
		model.nics.columnNames.add("MAC");
		model.nics.columnNames.add("Slot");
		model.nics.columnNames.add("PCI");
		model.nics.columnNames.add("Link Speed");
		model.nics.columnNames.add("vSwitch");
		
		for (Nic item : nics)
		{
			List<String> row = new ArrayList<String>();
			row.add(item.getVmnic());
			row.add(item.getPhysical_nic());
			row.add(item.getMac());
			row.add(String.valueOf(item.getSlot()));
			row.add(item.getPci());
			if(Float.toString(item.getSpeedGb()).equals(ZERO)){
				row.add(LINK_SPEED);
			}else {
				row.add(Float.toString(item.getSpeedGb()) + "Gb");	
			}
			
			row.add(item.getVswitch());
			
			model.nics.rowFormattedData.add(row);
		}
		
		return model;
	}
	
	private NetworkDetailModel formatExternalSwitches(NetworkDetailResult rawData, NetworkDetailModel model)
	{
		List<ExternalSwitch>ess = rawData.getVcm().getExternalSwitches();
		if(ess.isEmpty())
		{
			return model;
		}
		
		model.externalSwitches.columnNames.add("Switch Name");
		model.externalSwitches.columnNames.add("Description");
		model.externalSwitches.columnNames.add("Chassis ID");		
		
		for (ExternalSwitch item : ess)
		{
			List<String> row = new ArrayList<String>();
			row.add(item.getRemote_system_name());
			row.add(item.getRemote_system_desc());
			row.add(item.getRemote_chassis_id());
			
			model.externalSwitches.rowFormattedData.add(row);
		}
		
		return model;
	}
	
	private NetworkDetailModel formatExternalStorage(NetworkDetailResult rawData, NetworkDetailModel model)
	{
		List<ExternalStorage>ess = rawData.getVcm().getExternalStorage();
		if(ess.isEmpty())
		{
			return model;
		}
		
		model.externalStorage.columnNames.add("WWN");			
		
		for (ExternalStorage item : ess)
		{
			List<String> row = new ArrayList<String>();
			row.add(item.getWWN());			
			
			model.externalStorage.rowFormattedData.add(row);
		}
		
		return model;
	}	
	
	private NetworkDetailModel formatVirtualSwitches(NetworkDetailResult rawData, NetworkDetailModel model)
	{
		List<VS>vss = rawData.getVss();
		if(vss.isEmpty())
		{
			return model;
		}
							
		for (VS item : vss)
		{
			VirtualSwitchModel vs = new VirtualSwitchModel();
			vs.name = item.getName();
			vs.numPorts = item.getNumPorts();
			vs.numPortsAvailable = item.getNumPortsAvailable();
			for(PortGroup pg : item.getPort_groups() )
			{
				if(pg.getVlanId().isEmpty() || pg.getVlanId().equals("0"))
					vs.portGroups.add(pg);
				else
					vs.portGroups.add(pg);
//					vs.portGroups.add(pg.getName() + "   VLAN ID : " + pg.getVlanId());
				
			}
			model.virtualSwitches.add(vs);
		}
		
		return model;
	}	
	
	private NetworkDetailModel formatDistributedVirtualSwitches(NetworkDetailResult rawData, NetworkDetailModel model)
	{
		List<DVS>dvss = rawData.getDvss();
		if(dvss.isEmpty())
		{
			return model;
		}
							
		for (DVS item : dvss)
		{
			DistributedVirtualSwitchModel dvs = new DistributedVirtualSwitchModel();
			dvs.name = item.getName();
			
			for(PortGroup pg : item.getDownlink_port_groups() )
			{
				DownlinkPortGroupModel dpgm = new DownlinkPortGroupModel();
				dpgm.name = pg.getName();
				dpgm.vlanid = pg.getVlanId();
				dpgm.vms = pg.getVms();
				dvs.downlinkPortGroups.add(dpgm);
			}
			
			for(UplinkPortGroup pg : item.getUplink_port_groups() )
			{
				dvs.uplinkPortGroups.add(pg.getName());
			}
			
			model.distributedVirtualSwitches.add(dvs);
		}
		
		return model;
	}	
	
	private NetworkDetailModel formatVirtualConnectModules(NetworkDetailResult rawData, NetworkDetailModel model)
	{
		for(Enclosure enc : rawData.getVcm().getEnclosures() )
		{
			for(VcModule vcm : enc.getAllVcModuleG1s())
			{
				VirtualConnectModuleModel vcmm = new VirtualConnectModuleModel();
				vcmm.enclosure = enc.getEnclosureName();
				vcmm.bay = vcm.getBay();
				vcmm.name = vcm.getCommonIoModuleAttrs().getProductName();
				vcmm.status = vcm.getCommonAttrs().getOverallStatus();
				vcmm.ip = vcm.getCommonIoModuleAttrs().getIpaddress();
				vcmm.firmwareVersion = vcm.getCommonIoModuleAttrs().getFwRev();
				
				
				for(VCNetwork n : vcm.getNetworks())
				{
					VCNetworkModel vcnm = new VCNetworkModel(n.getDisplayName(), n.getUplinkVLANId());					
					vcmm.networks.add(vcnm);
				}
				
				for(Fabric f : vcm.getFabrics())
				{
					VCFabricModel vcfm = new VCFabricModel(f.getDisplayName());					
					vcmm.fabrics.add(vcfm);
					
				}
				
				for(VCUplink vcu : vcm.getUplinks())
				{					
					VCUplinkModel vcum = new VCUplinkModel();
					vcum.speedGb = String.valueOf(vcu.getSpeedGb());
					vcum.portLabel = vcu.getPortLabel();
					vcum.uplinkType = vcu.getUplinkType();
					if(vcum.uplinkType.equals("network")){
					    vcum.status = vcu.getLinkStatus();
					}else if(vcum.uplinkType.equals("fc"))  {
						vcum.status = vcu.getPortConnectStatus();	
					}else {
						 vcum.status="";
					}
					vcum.connector = vcu.getConnectorType();
					
					vcmm.uplinks.add(vcum);
				}
				model.vcms.add(vcmm);
			}
		}

	
		return model;
	}
	private NetworkDetailModel formatDataStoreModules(NetworkDetailResult rawData, NetworkDetailModel model){
		
		
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
	public NetworkDetailModel formatData(NetworkDetailResult rawData)
	{
        log.debug("NetworkDetailAdapter beginning" );
        
		NetworkDetailModel model = new NetworkDetailModel();
		
		if (rawData.hasError()) 
		{
            log.debug("NetworkDetailResults had an error message.  " +
                    "Returning a NetworkDetailModel with the error message");
            model.errorMessage = rawData.getErrorMessage();
            return model;
        }
		
		if(rawData.getNics().isEmpty())
		{
			model.errorMessage = "No Data in Network Detail Result.";
			return model;
		}
				
		model = this.formatNics(rawData, model);
		model = this.formatExternalSwitches(rawData, model);
		model = this.formatExternalStorage(rawData, model);		
		model = this.formatVirtualSwitches(rawData, model);
		model = this.formatDistributedVirtualSwitches(rawData, model);
		model = this.formatVirtualConnectModules(rawData, model);
		model = this.formatDataStoreModules(rawData, model);
		
        log.debug("NetworkDetailAdapter the end" );
		log.debug(model.toString());
		return model;
	}

	@Override
	public NetworkDetailModel getEmptyModel()
	{
		return new NetworkDetailModel();
	}

}
