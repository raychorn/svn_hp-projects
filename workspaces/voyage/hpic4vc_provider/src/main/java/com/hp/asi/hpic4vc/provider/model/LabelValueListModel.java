package com.hp.asi.hpic4vc.provider.model;

import java.util.ArrayList;
import java.util.List;

import com.hp.asi.hpic4vc.provider.model.BaseModel;

public class LabelValueListModel extends BaseModel {    
	public List<LabelValueModel> lvList;
        
    public LabelValueListModel() {
        super();
        lvList = new ArrayList<LabelValueModel>();        
    }

    
    public LabelValueListModel (final List<LabelValueModel> lvList) {
        super();
        this.lvList = lvList;
    }
    
    public void addLabelValuePair(final String label, final String value) {        
    	LabelValueModel lv = new LabelValueModel(label, value);    	
    	
        lvList.add(lv);        
    }

    public void addLabelValue(LabelValueModel lv)
    {
    	lvList.add(lv);    	
    }

	@Override
	public String toString() {
		return "LabelValueListModel [lvList=" + lvList + ", errorMessage="
				+ errorMessage + ", informationMessage=" + informationMessage
				+ "]";
	}


	public void addLabelValuePair(String label, int value)
	{
		this.addLabelValuePair(label, Integer.toString(value));		
	}
    

}
