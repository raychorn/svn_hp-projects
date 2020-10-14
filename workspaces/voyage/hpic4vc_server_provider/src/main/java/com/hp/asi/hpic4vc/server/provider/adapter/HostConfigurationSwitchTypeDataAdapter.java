package com.hp.asi.hpic4vc.server.provider.adapter;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import com.hp.asi.hpic4vc.provider.adapter.DataAdapter;
import com.hp.asi.hpic4vc.provider.error.InitializationException;
import com.hp.asi.hpic4vc.provider.impl.SessionManagerImpl;
import com.hp.asi.hpic4vc.server.provider.data.SwitchTypeData;
import com.hp.asi.hpic4vc.server.provider.data.SwitchSupportedValueData;
import com.hp.asi.hpic4vc.server.provider.model.SwitchTypeModel;


public class HostConfigurationSwitchTypeDataAdapter extends DataAdapter<SwitchTypeData, SwitchTypeModel>{

    //private static final String SERVICE_NAME = "rest/Settings/DefaultSwitchType";
    private static final String SERVICE_NAME = "services/net/DefaultSwitchType";
    
    public HostConfigurationSwitchTypeDataAdapter() {
        super(SwitchTypeData.class);
    }
    
    @Override
    public String getServiceName () {
        return SERVICE_NAME;
    }

	@Override
	public SwitchTypeModel formatData(SwitchTypeData rawData) {
		
		SwitchTypeModel model = new SwitchTypeModel();		
		model.switchdata.setType(rawData.getType());
		model.switchdata.setSettingName(rawData.getSettingName());		
		model.switchdata.setSelectedValue(rawData.getSelectedValue());	
		model.switchdata.setSupportedValues(rawData.getSupportedValues());
	
		List<String> sWitchData = rawData.getSupportedValues(); 
		String supportedSwitchselected= rawData.getSelectedValue();
		List<SwitchSupportedValueData> switchSupportedValueData= new ArrayList<SwitchSupportedValueData>();
		for(int i=0; i<sWitchData.size(); i++){	
			SwitchSupportedValueData obj= new SwitchSupportedValueData();
			String sWitchName= sWitchData.get(i);
			if(sWitchName.equals(supportedSwitchselected))
			{
				obj.setSwitchName(sWitchName);
				obj.setSelectedSwitch(true);
			}
			else{
				obj.setSwitchName(sWitchName);
				obj.setSelectedSwitch(false);
			}
			switchSupportedValueData.add(obj);
		}
		model.switchdata.setSwitchSupportedValueData(switchSupportedValueData);
		return model;
		
	}	
	protected String getWsUrl () throws InitializationException {
		String url = null;		
		String hostname = SessionManagerImpl.getInstance().getWSURLHostname
                (this.sessionInfo.getSessionId(),
                 this.sessionInfo.getServerGuid());			
		try {
			url= hostname + SERVICE_NAME + "?filter="+URLEncoder.encode(getQueryParameters(),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			 log.error("Encode url " + this.getQueryParameters()
	                    + " caught an UnsupportedEncodingException", e);
		}				 
        return url;
       
    }	
	
	 public String getQueryParameters () {
	        return getBaseParameters() + "&moref=" + this.sessionInfo.getMoref();
	    }
	    
	    public String getBaseParameters () {	    	
	    		    	
	        return '"'+"sessionID="+this.sessionInfo.getSessionId()+'"'
	        	  + "&filter="+'"'+"vmmUuid=" + this.sessionInfo.getServerGuid()+'"'	
	              + "&locale="     + this.sessionInfo.getLocale();
	              //+ "&serviceUrl=" + this.sessionInfo.getServiceUrl();	       
	    }
	
@Override
public SwitchTypeModel getEmptyModel() {
	// TODO Auto-generated method stub
	return null;
}
}
