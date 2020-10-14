package com.hp.asi.hpic4vc.server.provider.data;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonSetter;

public class ImlLogEntry
{
	private String count;
	private String severity;
	private String last_update;
	private String class_type;
	private String initial_update;
	private String description;
	
	public String getCount()
	{
		return count;
	}
	
	public void setCount(String count)
	{
		this.count = count;
	}

	public String getSeverity()
	{
		return severity;
	}

	public void setSeverity(String severity)
	{
		this.severity = severity;
	}

	public String getLast_update()
	{
		return last_update;
	}

	public void setLast_update(String last_update)
	{
		this.last_update = last_update;
	}

	@JsonProperty("class")
	public String getClass_type()
	{
		return class_type;
	}

	@JsonSetter("class")
	public void setClass_type(String class_type)
	{
		this.class_type = class_type;
	}

	public String getInitial_update()
	{
		return initial_update;
	}

	public void setInitial_update(String initial_update)
	{
		this.initial_update = initial_update;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}
	
	
}
