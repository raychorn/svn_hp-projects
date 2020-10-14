package com.hp.asi.hpic4vc.provider.data;

public class UserData {
	private String userName;
	private String locale;
	private String fullName;
	private int    role;
	private String key;
	
	public UserData() {
		this.userName = null;
		this.locale   = null;
		this.fullName = null;
		this.role     = -1;
		this.key      = null;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public int getRole() {
		return role;
	}

	public void setRole(int role) {
		this.role = role;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@Override
	public String toString() {
		return "UserData [userName=" + userName + ", locale=" + locale
				+ ", fullName=" + fullName + ", role=" + role + ", key=" + key
				+ "]";
	}



}
