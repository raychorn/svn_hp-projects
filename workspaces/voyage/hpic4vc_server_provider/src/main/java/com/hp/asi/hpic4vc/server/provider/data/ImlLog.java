package com.hp.asi.hpic4vc.server.provider.data;

import java.util.List;

public class ImlLog
{
	private String description;
	private List<ImlLogEntry> event;
	
	public String getDescription()
	{
		return description;
	}
	
	public void setDescription(String description)
	{
		this.description = description;
	}

	public List<ImlLogEntry> getEvent()
	{
		return event;
	}

	public void setEvent(List<ImlLogEntry> event)
	{
		this.event = event;
	}
	
	
}
