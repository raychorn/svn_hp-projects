package com.hp.asi.hpic4vc.server.provider.adapter;

import com.hp.asi.hpic4vc.provider.adapter.DataAdapter;
import com.hp.asi.hpic4vc.provider.model.StringModel;
import com.hp.asi.hpic4vc.server.provider.data.RediscoverNodeResult;
import com.hp.asi.hpic4vc.server.provider.model.AssociatedDevices;

public class GetRediscoverNodeAdapter extends
		DataAdapter<RediscoverNodeResult, AssociatedDevices> {

	private static final String SERVICE_NAME = "services/host/rediscovernodes";

	public GetRediscoverNodeAdapter() {
		super(RediscoverNodeResult.class);
	}

	@Override
	public String getServiceName() {
		return SERVICE_NAME;
	}

	@Override
	public AssociatedDevices formatData(RediscoverNodeResult rawData) {
		AssociatedDevices model = new AssociatedDevices();
		model.setServer(rawData.getAssociatedDevices().getServer());
		model.setIlo(rawData.getAssociatedDevices().getIlo());
		model.setOa(rawData.getAssociatedDevices().getOa());
		model.setVcm(rawData.getAssociatedDevices().getVcm());
		return model;
	}

	@Override
	public AssociatedDevices getEmptyModel() {
		return new AssociatedDevices();
	}

}
