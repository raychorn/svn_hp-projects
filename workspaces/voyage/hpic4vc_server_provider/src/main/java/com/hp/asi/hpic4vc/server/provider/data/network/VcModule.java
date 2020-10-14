package com.hp.asi.hpic4vc.server.provider.data.network;

import java.util.List;

public class VcModule
{
	private String macAddress;
	private CommonIoModulesAttrs commonIoModuleAttrs;
	private int dipSwitchSettings;
	private String powerState;
	private String uid;
	private String commStatus;
	private CommonAttrs commonAttrs; 
	private int bay;
	private String enclosureId;
	private FcAttrs fcAtters;	
	private List<VCNetwork> networks;
	private String id;
	private String moduleType;
	private String oaReportedStatus;
	private List<Fabric> fabrics;
	private List<VCUplink> uplinks;
	
	public String getMacAddress()
	{
		return macAddress;
	}
	
	public void setMacAddress(String macAddress)
	{
		this.macAddress = macAddress;
	}

	public CommonIoModulesAttrs getCommonIoModuleAttrs()
	{
		return commonIoModuleAttrs;
	}

	public void setCommonIoModuleAttrs(CommonIoModulesAttrs commonIoModuleAttrs)
	{
		this.commonIoModuleAttrs = commonIoModuleAttrs;
	}

	public int getDipSwitchSettings()
	{
		return dipSwitchSettings;
	}

	public void setDipSwitchSettings(int dipSwitchSettings)
	{
		this.dipSwitchSettings = dipSwitchSettings;
	}

	public String getPowerState()
	{
		return powerState;
	}

	public void setPowerState(String powerState)
	{
		this.powerState = powerState;
	}

	public String getUid()
	{
		return uid;
	}

	public void setUid(String uid)
	{
		this.uid = uid;
	}

	public String getCommStatus()
	{
		return commStatus;
	}

	public void setCommStatus(String commStatus)
	{
		this.commStatus = commStatus;
	}

	public CommonAttrs getCommonAttrs()
	{
		return commonAttrs;
	}

	public void setCommonAttrs(CommonAttrs commonAttrs)
	{
		this.commonAttrs = commonAttrs;
	}

	public int getBay()
	{
		return bay;
	}

	public void setBay(int bay)
	{
		this.bay = bay;
	}

	public String getEnclosureId()
	{
		return enclosureId;
	}

	public void setEnclosureId(String enclosureId)
	{
		this.enclosureId = enclosureId;
	}

	public FcAttrs getFcAtters()
	{
		return fcAtters;
	}

	public void setFcAtters(FcAttrs fcAtters)
	{
		this.fcAtters = fcAtters;
	}

	public List<VCNetwork> getNetworks()
	{
		return networks;
	}

	public void setNetworks(List<VCNetwork> networks)
	{
		this.networks = networks;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getModuleType()
	{
		return moduleType;
	}

	public void setModuleType(String moduleType)
	{
		this.moduleType = moduleType;
	}

	public String getOaReportedStatus()
	{
		return oaReportedStatus;
	}

	public void setOaReportedStatus(String oaReportedStatus)
	{
		this.oaReportedStatus = oaReportedStatus;
	}

	public List<Fabric> getFabrics()
	{
		return fabrics;
	}

	public void setFabrics(List<Fabric> fabrics)
	{
		this.fabrics = fabrics;
	}

	public List<VCUplink> getUplinks()
	{
		return uplinks;
	}

	public void setUplinks(List<VCUplink> uplinks)
	{
		this.uplinks = uplinks;
	}
	
}
