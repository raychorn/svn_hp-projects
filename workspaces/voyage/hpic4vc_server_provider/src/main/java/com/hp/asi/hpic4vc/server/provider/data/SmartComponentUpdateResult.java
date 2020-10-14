package com.hp.asi.hpic4vc.server.provider.data;

import java.util.List;
import org.codehaus.jackson.annotate.JsonProperty;

public class SmartComponentUpdateResult extends ResultBase {

	@JsonProperty("queue")
	private List<Queue> queue;
	@JsonProperty("jobs")
	private List<Jobs> jobs;
	@JsonProperty("Error")
    private List <String> Error;
	
	public List<Queue> getQueue() {
		return queue;
	}

	public void setQueue(List<Queue> queue) {
		this.queue = queue;
	}

	public List<Jobs> getJobs() {
		return jobs;
	}

	public void setJobs(List<Jobs> jobs) {
		this.jobs = jobs;
	}

	public List <String> getError() {
		return Error;
	}

	public void setError(List <String> error) {
		Error = error;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SmartComponentUpdateResult [");
		if (queue != null) {
			builder.append("queue=");
			for (Queue q : queue) {
				builder.append(q.toString());
				builder.append(", ");
			}
			builder.append("}\n");
		}
		if (jobs != null) {
			builder.append("jobs={");
			for (Jobs j: jobs) {
				builder.append(j.toString());
				builder.append(", ");
			}
			builder.append("}\n");
		}
		builder.append("]");
		return builder.toString();
	}

	

}
