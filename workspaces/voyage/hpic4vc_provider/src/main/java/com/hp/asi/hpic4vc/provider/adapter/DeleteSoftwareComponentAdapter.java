package com.hp.asi.hpic4vc.provider.adapter;

import com.hp.asi.hpic4vc.provider.model.StringModel;

public class DeleteSoftwareComponentAdapter extends DeleteAdapter<String, StringModel> {

	private static final String SERVICE_NAME = "services/host/smart_components";
	private String   data;
	
	public DeleteSoftwareComponentAdapter () {
	  	super();
	}
	
	public DeleteSoftwareComponentAdapter (String data) {
	   super();
	   this.data = data;
	}

	@Override
	public StringModel getEmptyModel() {
		return new StringModel();
	}
	
	@Override
	public String getServiceName() {
		 return SERVICE_NAME;
	}
	
	@Override
	public String getDeleteParams() {
		StringBuilder sb = new StringBuilder();
		sb.append("{\"sc_filename\":\"");
		sb.append(data);
		sb.append("\"}");
		return sb.toString();
	}

	@Override
	public StringModel formatData(String response) {
		 StringModel model = new StringModel();
		 model.data = response; 
		 return model;
	}
	

}
