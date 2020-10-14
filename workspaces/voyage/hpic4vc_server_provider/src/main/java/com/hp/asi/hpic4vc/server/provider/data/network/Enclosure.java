package com.hp.asi.hpic4vc.server.provider.data.network;

import java.util.ArrayList;
import java.util.List;

public class Enclosure
{
	private String enclosureName;
	private String enclosureType;
	private String id;
	private List<VcModule> allVcModuleG1s;
	
	public Enclosure()
	{
		allVcModuleG1s = new ArrayList<VcModule>();
	}
	
	public String getEnclosureName()
	{
		return enclosureName;
	}
	
	public void setEnclosureName(String enclosureName)
	{
		this.enclosureName = enclosureName;
	}

	public String getEnclosureType()
	{
		return enclosureType;
	}

	public void setEnclosureType(String enclosureType)
	{
		this.enclosureType = enclosureType;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public List<VcModule> getAllVcModuleG1s()
	{
		return allVcModuleG1s;
	}

	public void setAllVcModuleG1s(List<VcModule> allVcModuleG1s)
	{
		this.allVcModuleG1s = allVcModuleG1s;
	}
}
