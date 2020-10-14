package com.hp.asi.hpic4vc.server.provider.data;

import java.util.ArrayList;
import java.util.List;

public class VDSversionResult {
	private String type;
	private String settingName;
	private List<String> supportedValues;
	private String selectedValue;

	public VDSversionResult() {
		super();
		this.supportedValues = new ArrayList<String>();
		// TODO Auto-generated constructor stub
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSettingName() {
		return settingName;
	}

	public void setSettingName(String settingName) {
		this.settingName = settingName;
	}

	public List<String> getSupportedValues() {
		return supportedValues;
	}

	public void setSupportedValues(List<String> supportedValues) {
		this.supportedValues = supportedValues;
	}

	public String getSelectedValue() {
		return selectedValue;
	}

	public void setSelectedValue(String selectedValue) {
		this.selectedValue = selectedValue;
	}

}
