package com.hp.asi.hpic4vc.server.provider.data;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonSetter;

public class Event {
	private String count;
	private String severity;
	private String _class_name;
	private String last_update;
	//make sure that this is addressed as the json string contains class as property ,inform to Mohammed on this 
	private String classz;
	private String initial_update;
	private String description;
	public String getCount() {
		return count;
	}
	public void setCount(String count) {
		this.count = count;
	}
	public String getSeverity() {
		return severity;
	}
	public void setSeverity(String severity) {
		this.severity = severity;
	}
	public String get_class_name() {
		return _class_name;
	}
	public void set_class_name(String _class_name) {
		this._class_name = _class_name;
	}
	public String getLast_update() {
		return last_update;
	}
	public void setLast_update(String last_update) {
		this.last_update = last_update;
	}
	@JsonProperty("class")
	public String getClassz() {
		return classz;
	}
	@JsonSetter("class")
	public void setClassz(String classz) {
		this.classz = classz;
	}
	public String getInitial_update() {
		return initial_update;
	}
	public void setInitial_update(String initial_update) {
		this.initial_update = initial_update;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
}
