package com.hp.asi.hpic4vc.server.provider.model;

import java.util.ArrayList;
import java.util.List;

public class VirtualConnectModuleModel
{
	public String enclosure;
	public int bay;
	public String name;
	public String status;
	public String ip;
	public String firmwareVersion;
	public List<VCNetworkModel> networks;
	public List<VCFabricModel> fabrics;
	public List<VCUplinkModel> uplinks;
	
	public VirtualConnectModuleModel()
	{
		networks = new ArrayList<VCNetworkModel>();
		fabrics = new ArrayList<VCFabricModel>();
		uplinks = new ArrayList<VCUplinkModel>();
	}
}
