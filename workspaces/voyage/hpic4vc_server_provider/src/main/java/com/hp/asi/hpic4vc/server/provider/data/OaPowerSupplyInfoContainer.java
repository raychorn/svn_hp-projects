package com.hp.asi.hpic4vc.server.provider.data;

import java.util.ArrayList;
import java.util.List;

public class OaPowerSupplyInfoContainer
{
	private List<OaPowerSupplyInfo> powerSupplyInfo;

	public OaPowerSupplyInfoContainer()
	{
		powerSupplyInfo = new ArrayList<OaPowerSupplyInfo>();
	}
	
	public List<OaPowerSupplyInfo> getPowerSupplyInfo()
	{
		return powerSupplyInfo;
	}

	public void setPowerSupplyInfo(List<OaPowerSupplyInfo> powerSupplyInfo)
	{
		this.powerSupplyInfo = powerSupplyInfo;
	}

	
}
