package com.hp.asi.hpic4vc.server.provider.data.Deployment;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.PROPERTY)
public class PersonalityDataVO {
	
	private String displayName;
	
	private String dnsSuffix;
	
	private String domainName;
	
	private String domainType;
	
	private String hostName;
	
	private String  groupName;
	
	private List<NicsVO> nics;
	
	public List<NicsVO> getNics() {
		return nics;
	}

	public void setNics(List<NicsVO> nics) {
		this.nics = nics;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getDnsSuffix() {
		return dnsSuffix;
	}

	public void setDnsSuffix(String dnsSuffix) {
		this.dnsSuffix = dnsSuffix;
	}

	public String getDomainName() {
		return domainName;
	}

	public PersonalityDataVO() {
		super();
		this.nics = new ArrayList<NicsVO>();
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public String getDomainType() {
		return domainType;
	}

	public void setDomainType(String domainType) {
		this.domainType = domainType;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}


}
