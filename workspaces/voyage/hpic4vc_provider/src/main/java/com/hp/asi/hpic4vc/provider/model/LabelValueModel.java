package com.hp.asi.hpic4vc.provider.model;

public class LabelValueModel extends BaseModel
{
	public String label;
	public String value;
	
	public LabelValueModel()
	{
		this.label = "";
		this.value = "";
	}
	
	public LabelValueModel(String label, String value)
	{
		this.label = label;
		this.value = value;
	}

	@Override
	public String toString() {
		return "LabelValueModel [label=" + label + ", value=" + value + "]";
	}
	
}
