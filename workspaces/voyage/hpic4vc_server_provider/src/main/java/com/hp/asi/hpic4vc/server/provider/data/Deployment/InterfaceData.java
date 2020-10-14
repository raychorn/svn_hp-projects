package com.hp.asi.hpic4vc.server.provider.data.Deployment;

public class InterfaceData {

	private String macAddr;

	private String ipv4Addr;

	private String dhcpEnabled;

	public String getDhcpEnabled() {
		return dhcpEnabled;
	}

	public void setDhcpEnabled(String dhcpEnabled) {
		this.dhcpEnabled = dhcpEnabled;
	}

	public String getMacAddr() {
		return macAddr;
	}

	public void setMacAddr(String macAddr) {
		this.macAddr = macAddr;
	}

	public String getIpv4Addr() {
		return ipv4Addr;
	}

	public void setIpv4Addr(String ipv4Addr) {
		this.ipv4Addr = ipv4Addr;
	}

}
