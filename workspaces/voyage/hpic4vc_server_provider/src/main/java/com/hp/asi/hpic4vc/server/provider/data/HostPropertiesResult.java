package com.hp.asi.hpic4vc.server.provider.data;

import java.util.List;

public class HostPropertiesResult extends ResultBase {
	
	private List<HostPropertiesDetails> hostprop;	
	
	public List<HostPropertiesDetails> getHostprop()
	{
		return hostprop;
	}

	public void setHostprop(List<HostPropertiesDetails> hostPropertiesDetails)
	{
		this.hostprop = hostPropertiesDetails;
	}

}
