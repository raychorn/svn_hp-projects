package com.hp.asi.hpic4vc.server.provider.data;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonSetter;

public class OaExtraData
{
	private String value;
	private String _name;
	
	

	public String getValue()
	{
		return value;
	}
	
	public void setValue(String value)
	{
		this.value = value;
	}

	public String get_name() {
		return _name;
	}

	public void set_name(String _name) {
		this._name = _name;
	}
	
//	@JsonProperty("_name")
//	public String getName()
//	{
//		return _name;
//	}
//
//	@JsonSetter("_name")
//	public void setName(String name)
//	{
//		this._name = name;
//	}
}
