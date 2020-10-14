package com.hp.asi.hpic4vc.server.provider.data.Deployment;

import java.util.ArrayList;
import java.util.List;



public class DeploymentVO {
	
	
	private String buildPlanUri;
	
	public String getBuildPlanUri() {
		return buildPlanUri;
	}

	public void setBuildPlanUri(String buildPlanUri) {
		this.buildPlanUri = buildPlanUri;
	}

	public String getvCenterFolderName() {
		return vCenterFolderName;
	}

	public void setvCenterFolderName(String vCenterFolderName) {
		this.vCenterFolderName = vCenterFolderName;
	}

	public String getServerUri() {
		return serverUri;
	}

	public DeploymentVO() {
		super();
		this.personalityData = new ArrayList<PersonalityDataVO>();
	}

	public void setServerUri(String serverUri) {
		this.serverUri = serverUri;
	}

	public List<PersonalityDataVO> getPersonalityData() {
		return personalityData;
	}

	public void setPersonalityData(List<PersonalityDataVO> personalityData) {
		this.personalityData = personalityData;
	}

	private String vCenterFolderName;
	
	private String serverUri;
	
	private List<PersonalityDataVO> personalityData;

}
