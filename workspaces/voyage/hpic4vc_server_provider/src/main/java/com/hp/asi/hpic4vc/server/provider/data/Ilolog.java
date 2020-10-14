package com.hp.asi.hpic4vc.server.provider.data;

import java.util.List;

public class Ilolog {
	
	private List<Event> event;
	private String description;

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
