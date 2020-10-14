package com.hp.asi.hpic4vc.server.provider.data;

public class HostConfigurationData {
	private String networkName;
	private String purpose;
	private String type;
	private String networkId;
	private String vlanID;
	private String otherInfo;
	private String uuid;
	private String version;
	private String uri;
	private String eTag;
	

	public HostConfigurationData() {

	}
	
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String geteTag() {
		return eTag;
	}

	public void seteTag(String eTag) {
		this.eTag = eTag;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getNetworkId() {
		return networkId;
	}

	public void setNetworkId(String networkId) {
		this.networkId = networkId;
	}

	public String getVlanID() {
		return vlanID;
	}

	public void setVlanID(String vlanID) {
		this.vlanID = vlanID;
	}

	public String getNetworkName() {
		return networkName;
	}

	public void setNetworkName(String networkName) {
		this.networkName = networkName;
	}

	public String getOtherInfo() {
		return otherInfo;
	}

	public void setOtherInfo(String otherInfo) {
		this.otherInfo = otherInfo;
	}

	@Override
	public String toString() {
		return "HostConfigurationData [networkname=" + networkName
				+ ", purpose=" + purpose + ", type=" + type + ", networkId="
				+ networkId + ", vlanID=" + vlanID + ", otherInfo=" + otherInfo + "]";
	}

}
