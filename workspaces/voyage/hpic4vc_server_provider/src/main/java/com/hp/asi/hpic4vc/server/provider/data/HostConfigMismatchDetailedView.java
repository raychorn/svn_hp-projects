package com.hp.asi.hpic4vc.server.provider.data;



import java.util.ArrayList;
import java.util.List;

public class HostConfigMismatchDetailedView {
	private List<HostConfigMismatchvSwitches> vSwitches;

	
	public List<HostConfigMismatchvSwitches> getvSwitches() {
		return vSwitches;
	}

	public void setvSwitches(List<HostConfigMismatchvSwitches> vSwitches) {
		this.vSwitches = vSwitches;
	}

	public HostConfigMismatchDetailedView() {
		super();

		this.vSwitches = new ArrayList<HostConfigMismatchvSwitches>();
	}
}
