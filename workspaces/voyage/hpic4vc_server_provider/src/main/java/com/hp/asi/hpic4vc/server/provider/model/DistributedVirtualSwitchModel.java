package com.hp.asi.hpic4vc.server.provider.model;

import java.util.ArrayList;
import java.util.List;

public class DistributedVirtualSwitchModel
{
	public String name;
	public List<DownlinkPortGroupModel> downlinkPortGroups;
	public List<String> uplinkPortGroups;
	
	public DistributedVirtualSwitchModel()
	{
		downlinkPortGroups = new ArrayList<DownlinkPortGroupModel>();
		uplinkPortGroups = new ArrayList<String>();
		
	}
}
