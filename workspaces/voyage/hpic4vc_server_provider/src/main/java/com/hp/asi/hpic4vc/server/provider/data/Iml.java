package com.hp.asi.hpic4vc.server.provider.data;

import java.util.List;

public class Iml {

	private String _class_name;
	private List<Event> event;
	private String description;
	public String get_class_name() {
		return _class_name;
	}
	public void set_class_name(String _class_name) {
		this._class_name = _class_name;
	}
	public List<Event> getEvent() {
		return event;
	}
	public void setEvent(List<Event> event) {
		this.event = event;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
