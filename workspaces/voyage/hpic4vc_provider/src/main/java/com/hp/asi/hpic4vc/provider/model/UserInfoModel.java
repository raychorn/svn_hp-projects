package com.hp.asi.hpic4vc.provider.model;

public class UserInfoModel extends BaseModel {
	public String username = null;
	public String roleId   = null;
	
	public UserInfoModel() {
		super();
	}
	
	public UserInfoModel(final String userName, final String roleId) {
		super();
		this.username = userName;
		this.roleId   = roleId;
	}

	@Override
	public String toString() {
		return "UserInfoModel [username=" + username + ", roleId=" + roleId
				+ ", errorMessage=" + errorMessage + ", informationMessage="
				+ informationMessage + "]";
	}
	
}
