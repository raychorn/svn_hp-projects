package com.hp.asi.hpic4vc.server.provider.data;

import java.util.ArrayList;
import java.util.List;

public class OaInterconnectContainer
{
	private List<OaInterconnects> interconnectTrayInfo;

	public OaInterconnectContainer()
	{
		interconnectTrayInfo = new ArrayList<OaInterconnects>();
	}
	
	public List<OaInterconnects> getInterconnectTrayInfo()
	{
		return interconnectTrayInfo;
	}

	public void setInterconnectTrayInfo(List<OaInterconnects> interconnectTrayInfo)
	{
		this.interconnectTrayInfo = interconnectTrayInfo;
	}
}
