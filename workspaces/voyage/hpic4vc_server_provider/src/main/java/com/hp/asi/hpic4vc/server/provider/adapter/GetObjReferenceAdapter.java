package com.hp.asi.hpic4vc.server.provider.adapter;

import com.hp.asi.hpic4vc.provider.adapter.DataAdapter;
import com.hp.asi.hpic4vc.provider.error.DataMapException;
import com.hp.asi.hpic4vc.provider.error.InitializationException;
import com.hp.asi.hpic4vc.provider.impl.SessionManagerImpl;
import com.hp.asi.hpic4vc.provider.model.StringModel;
import com.hp.asi.hpic4vc.server.provider.data.ObjRefNameResult;

public class GetObjReferenceAdapter extends DataAdapter<ObjRefNameResult, StringModel> {

	private static final String SERVICE_NAME = "services/host/icsp";
	
	private String uuidData;

	public GetObjReferenceAdapter(String uuidData) {
		super(ObjRefNameResult.class);
		this.uuidData = uuidData;

	}

	@Override
	public String getServiceName() {
		return SERVICE_NAME;
	}

	@Override
	public StringModel formatData(ObjRefNameResult rawData) {
		StringModel model = new StringModel();
		model.data = rawData.getObjRefName();
		return model;
	}

	@Override
	public StringModel getEmptyModel() {
		return new StringModel();
	}
	
	 protected String getWsUrl () throws InitializationException {
	        
	        
	        return super.getWsUrl() + "&uuid=" + this.uuidData;  
	    }

}
