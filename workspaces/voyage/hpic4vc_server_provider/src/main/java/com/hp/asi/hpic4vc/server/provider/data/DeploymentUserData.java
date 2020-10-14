package com.hp.asi.hpic4vc.server.provider.data;


public class DeploymentUserData  {
	
	public DeploymentUserData(String _userName , String _password) {
		super();
		setUserName(_userName);
		setPassword(_password);
	}

	private String userName;
	
	private String password ;
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String username) {
		this.userName = username;
	}




	

}
