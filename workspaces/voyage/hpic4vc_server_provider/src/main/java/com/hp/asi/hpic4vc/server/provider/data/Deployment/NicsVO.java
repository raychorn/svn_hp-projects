package com.hp.asi.hpic4vc.server.provider.data.Deployment;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.PROPERTY)
public class NicsVO {
	
	private Boolean dhcp;

public Boolean getDhcp() {
		return dhcp;
	}

	public void setDhcp(Boolean dhcp) {
		this.dhcp = dhcp;
	}

	public String getDns() {
		return dns;
	}

	public void setDns(String dns) {
		this.dns = dns;
	}

	public String getDnsDomain() {
		return dnsDomain;
	}

	public void setDnsDomain(String dnsDomain) {
		this.dnsDomain = dnsDomain;
	}

	public String getGateway() {
		return gateway;
	}

	public void setGateway(String gateway) {
		this.gateway = gateway;
	}

	public String getIp4Address() {
		return ip4Address;
	}

	public void setIp4Address(String ip4Address) {
		this.ip4Address = ip4Address;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	public String getMask() {
		return mask;
	}

	public void setMask(String mask) {
		this.mask = mask;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

private String dns;

private String dnsDomain;

private String gateway ;

private String ip4Address;

private String macAddress;

private String mask;

private String state;

}
