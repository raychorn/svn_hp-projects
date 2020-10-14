package com.hp.asi.hpic4vc.server.provider.adapter;

import com.hp.asi.hpic4vc.provider.adapter.DataAdapter;
import com.hp.asi.hpic4vc.provider.model.StringModel;
import com.hp.asi.hpic4vc.server.provider.data.PowerControlStatusResult;

public class GetPowerControlStatusAdapter extends
		DataAdapter<PowerControlStatusResult, StringModel> {

	private static final String SERVICE_NAME = "services/host/powercontrol";

	public GetPowerControlStatusAdapter() {
		super(PowerControlStatusResult.class);
	}

	@Override
	public String getServiceName() {
		return SERVICE_NAME;
	}

	@Override
	public StringModel formatData(PowerControlStatusResult rawData) {
		StringModel model = new StringModel();
		model.data = rawData.getPower();
		return model;
	}

	@Override
	public StringModel getEmptyModel() {
		return new StringModel();
	}

}
