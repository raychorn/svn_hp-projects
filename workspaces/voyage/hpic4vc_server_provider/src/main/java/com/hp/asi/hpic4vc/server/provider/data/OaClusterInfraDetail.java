package com.hp.asi.hpic4vc.server.provider.data;

import java.util.ArrayList;
import java.util.List;

public class OaClusterInfraDetail {
	
	private OaClusterInfraEncloserSummary enclosure_info;
	public OaClusterInfraDetail() {
		super();
		enclosure_info = new OaClusterInfraEncloserSummary();
		power = new OaPowerDetail();
		thermal = new OaThermal();
		fan_info = new OaFanInfoContainer();
		ps_info = new OaPowerSupplyInfoContainer();
		tray_info = new OaInterconnectContainer();
		oa_info_status = new ArrayList<OaInfoStatus>();
		syslog = new ArrayList<String>();
	}
	private OaPowerDetail power;
	private OaThermal thermal;
	private OaFanInfoContainer fan_info;
	private OaPowerSupplyInfoContainer ps_info;
	private OaInterconnectContainer tray_info;
	private List<OaInfoStatus> oa_info_status;
	private List<String> syslog;
	public OaClusterInfraEncloserSummary getEnclosure_info() {
		return enclosure_info;
	}
	public void setEnclosure_info(OaClusterInfraEncloserSummary enclosure_info) {
		this.enclosure_info = enclosure_info;
	}
	public OaPowerDetail getPower() {
		return power;
	}
	public void setPower(OaPowerDetail power) {
		this.power = power;
	}
	public OaThermal getThermal() {
		return thermal;
	}
	public void setThermal(OaThermal thermal) {
		this.thermal = thermal;
	}
	public OaFanInfoContainer getFan_info() {
		return fan_info;
	}
	public void setFan_info(OaFanInfoContainer fan_info) {
		this.fan_info = fan_info;
	}
	public OaPowerSupplyInfoContainer getPs_info() {
		return ps_info;
	}
	public void setPs_info(OaPowerSupplyInfoContainer ps_info) {
		this.ps_info = ps_info;
	}
	public OaInterconnectContainer getTray_info() {
		return tray_info;
	}
	public void setTray_info(OaInterconnectContainer tray_info) {
		this.tray_info = tray_info;
	}
	public List<OaInfoStatus> getOa_info_status() {
		return oa_info_status;
	}
	public void setOa_info_status(List<OaInfoStatus> oa_info_status) {
		this.oa_info_status = oa_info_status;
	}
	public List<String> getSyslog() {
		return syslog;
	}
	public void setSyslog(List<String> syslog) {
		this.syslog = syslog;
	}

}
