package com.hp.asi.hpic4vc.server.provider.data.network;

import java.util.List;

public class DVSUplink
{
	private String name;
	private List<PNIC> pnics;
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}

	public List<PNIC> getPnics()
	{
		return pnics;
	}

	public void setPnics(List<PNIC> pnics)
	{
		this.pnics = pnics;
	}
}
