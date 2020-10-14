package com.hp.asi.hpic4vc.server.provider.data;

import java.util.ArrayList;
import java.util.List;



public class SwitchTypeData {

	private String type;
	//private List<SwitchSupportedValueData> supportedValues;
	private List<String> supportedValues;
	private String selectedValue;
	private String settingName;
	private List<SwitchSupportedValueData> switchSupportedValueData;
	//private List<String, Boolean> sWitchList;
	
	public SwitchTypeData() {
		super();
		this.supportedValues = new ArrayList<String>();		
		this.switchSupportedValueData = new ArrayList<SwitchSupportedValueData>();	
		// TODO Auto-generated constructor stub
	}
	
	

	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getSelectedValue() {
		return selectedValue;
	}
	
	public void setSelectedValue(String selectedValue) {
		this.selectedValue = selectedValue;
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



	public List<SwitchSupportedValueData> getSwitchSupportedValueData() {
		return switchSupportedValueData;
	}



	public void setSwitchSupportedValueData(
			List<SwitchSupportedValueData> switchSupportedValueData) {
		this.switchSupportedValueData = switchSupportedValueData;
	}



	

	
	
	
}
