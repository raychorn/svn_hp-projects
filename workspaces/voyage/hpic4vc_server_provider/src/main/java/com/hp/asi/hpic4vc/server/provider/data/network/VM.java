package com.hp.asi.hpic4vc.server.provider.data.network;

import java.util.List;

public class VM
{
	private VMHardware hardware;
	private String name;
	private List<VMNics> nics;
	
	public VMHardware getHardware()
	{
		return hardware;
	}
	
	public void setHardware(VMHardware hardware)
	{
		this.hardware = hardware;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public List<VMNics> getNics() {
		return nics;
	}

	public void setNics(List<VMNics> nics) {
		this.nics = nics;
	}
}
