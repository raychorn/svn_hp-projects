package com.hp.asi.hpic4vc.provider.adapter;

import com.hp.asi.hpic4vc.provider.data.UserData;
import com.hp.asi.hpic4vc.provider.model.UserInfoModel;

public class UserInfoDataAdapter extends DataAdapter<UserData, UserInfoModel> {
	private static final String SERVICE_NAME = "userinfo/";

	public UserInfoDataAdapter() {
		super(UserData.class);
	}
	
	@Override
	public UserInfoModel formatData(UserData rawData) {
		UserInfoModel model = new UserInfoModel();
		model.username = rawData.getUserName();
		model.roleId   = Integer.toString(rawData.getRole());
		return model;
	}

	@Override
	public UserInfoModel getEmptyModel() {
		return new UserInfoModel();
	}

	@Override
	public String getServiceName() {
		return SERVICE_NAME;
	}



}
