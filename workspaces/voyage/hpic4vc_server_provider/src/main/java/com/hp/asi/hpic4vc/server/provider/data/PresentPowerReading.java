package com.hp.asi.hpic4vc.server.provider.data;

public class PresentPowerReading {
  
	private String _class_name_;
	private String unit;
	private String value;
	public String get_class_name_() {
		return _class_name_;
	}
	public void set_class_name_(String _class_name_) {
		this._class_name_ = _class_name_;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
}
