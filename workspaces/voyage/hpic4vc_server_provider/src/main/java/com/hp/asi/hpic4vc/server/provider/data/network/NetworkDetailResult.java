package com.hp.asi.hpic4vc.server.provider.data.network;

import java.util.ArrayList;
import java.util.List;

import com.hp.asi.hpic4vc.server.provider.data.ResultBase;
import com.hp.asi.hpic4vc.server.provider.data.Nic;

public class NetworkDetailResult extends ResultBase
{
	private List<DVS> dvss;
	private List<Nic> nics;
	private List<VS> vss;
	private VCM vcm;
	private List<DataStore> datastores;
	
	public NetworkDetailResult()
	{
		super();
		dvss = new ArrayList<DVS>();
		nics = new ArrayList<Nic>();
		vss = new ArrayList<VS>();
		vcm = new VCM();
		datastores = new ArrayList<DataStore>();
	}
	
	public List<DVS> getDvss()
	{
		return dvss;
	}
	
	public void setDvss(List<DVS> dvss)
	{
		this.dvss = dvss;
	}

	public List<Nic> getNics()
	{
		return nics;
	}

	public void setNics(List<Nic> nics)
	{
		this.nics = nics;
	}

	public List<VS> getVss()
	{
		return vss;
	}

	public void setVss(List<VS> vss)
	{
		this.vss = vss;
	}

	public VCM getVcm()
	{
		return vcm;
	}

	public void setVcm(VCM vcm)
	{
		this.vcm = vcm;
	}

	public List<DataStore> getDatastores()
	{
		return datastores;
	}

	public void setDatastores(List<DataStore> datastores)
	{
		this.datastores = datastores;
	}
}
