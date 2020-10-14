package com.hp.asi.hpic4vc.server.provider.data;

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

public class Queue {
	@JsonProperty("retry_count")
	private String retry_count;
	@JsonProperty("package_url")
	private String package_url;
	@JsonProperty("run")
	private boolean run;
	@JsonProperty("options")
	private List<Integer> options;
	@JsonProperty("skip")
	private boolean skip;
	public String getRetry_count() {
		return retry_count;
	}
	public void setRetry_count(String retry_count) {
		this.retry_count = retry_count;
	}
	public String getPackage_url() {
		return package_url;
	}
	public void setPackage_url(String package_url) {
		this.package_url = package_url;
	}
	public boolean isRun() {
		return run;
	}
	public void setRun(boolean run) {
		this.run = run;
	}
	public List<Integer> getOptions() {
		return options;
	}
	public void setOptions(List<Integer> options) {
		this.options = options;
	}
	public boolean isSkip() {
		return skip;
	}
	public void setSkip(boolean skip) {
		this.skip = skip;
	}
	
	
}
