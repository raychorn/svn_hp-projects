package com.hp.asi.hpic4vc.server.provider.data;

import java.util.ArrayList;
import java.util.List;

public class OaFanInfoContainer
{
	private List<OaFanInfo> fanInfo;

	public OaFanInfoContainer()
	{
		fanInfo = new ArrayList<OaFanInfo>();
	}
	
	public List<OaFanInfo> getFanInfo()
	{
		return fanInfo;
	}

	public void setFanInfo(List<OaFanInfo> fanInfo)
	{
		this.fanInfo = fanInfo;
	}

	
	

	
}
