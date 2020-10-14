package com.hp.asi.hpic4vc.server.provider.data;

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

public class FirmwareJobs{
	@JsonProperty("jobs")
   	private List<Jobs> jobs;
   	@JsonProperty("queue")
   	private List<Queue> queue;
   	@JsonProperty("Error")
    private List <String> Error;
 	public List<Jobs> getJobs(){
		return this.jobs;
	}
	public void setJobs(List<Jobs> jobs){
		this.jobs = jobs;
	}
 	public List<Queue> getQueue(){
		return this.queue;
	}
	public void setQueue(List<Queue> queue){
		this.queue = queue;
	}
	public List <String> getError() {
		return Error;
	}
	public void setError(List <String> error) {
		Error = error;
	}
}