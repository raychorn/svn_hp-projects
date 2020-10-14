/**
 * 
 */
package com.hp.asi.hpic4vc.server.provider.data;

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author bhowmicp
 * 
 */
public class Jobs {
	@JsonProperty("HealthState")
	private String HealthState;
	@JsonProperty("StatusDescriptions")
	private List<String> statusDescriptions;
	@JsonProperty("InstanceID")
	private String instanceID;
	@JsonProperty("Priority")
	private String Priority;
	@JsonProperty("CommunicationStatus")
	private String CommunicationStatus;

	@JsonProperty("JobRunTimes")
	private int JobRunTimes;
	@JsonProperty("OtherRecoveryAction")
	private String OtherRecoveryAction;
	@JsonProperty("DetailedStatus")
	private String DetailedStatus;
	@JsonProperty("UntilTime")
	private String UntilTime;
	@JsonProperty("JobState")
	private int JobState;
	@JsonProperty("Description")
	private String Description;
	@JsonProperty("RunDay")
	private String RunDay;
	@JsonProperty("TimeOfLastStateChange")
	private String TimeOfLastStateChange;
	@JsonProperty("RunMonth")
	private String RunMonth;
	@JsonProperty("ErrorCode")
	private int ErrorCode;
	@JsonProperty("RecoveryAction")
	private String RecoveryAction;
	@JsonProperty("PercentComplete")
	private int PercentComplete;
	@JsonProperty("LocalOrUtcTime")
	private String LocalOrUtcTime;
	@JsonProperty("Status")
	private String Status;
	@JsonProperty("Name")
	private String Name;
	@JsonProperty("InstallDate")
	private String InstallDate;
	@JsonProperty("RunDayOfWeek")
	private String RunDayOfWeek;
	@JsonProperty("ElementName")
	private String ElementName;
	@JsonProperty("JobStatus")
	private String JobStatus;
	@JsonProperty("ElapsedTime")
	private String ElapsedTime;
	@JsonProperty("Caption")
	private String Caption;
	@JsonProperty("DeleteOnCompletion")
	private boolean DeleteOnCompletion;
	@JsonProperty("TimeSubmitted")
	private String TimeSubmitted;
	@JsonProperty("PrimaryStatus")
	private String PrimaryStatus;
	@JsonProperty("ErrorDescription")
	private String ErrorDescription;
	@JsonProperty("RunStartInterval")
	private String RunStartInterval;
	@JsonProperty("ScheduledStartTime")
	private String ScheduledStartTime;
	@JsonProperty("OperationalStatus")
	private List<Integer> OperationalStatus;
	@JsonProperty("OperatingStatus")
	private String OperatingStatus;
	@JsonProperty("Notify")
	private String Notify;
	@JsonProperty("Owner")
	private String Owner;

	public String getHealthState() {
		return HealthState;
	}

	public void setHealthState(String healthState) {
		HealthState = healthState;
	}

	// @JsonProperty("StatusDescriptions")
	public List<String> getStatusDescriptions() {
		return statusDescriptions;
	}

	public void setStatusDescriptions(List<String> statusDescriptions) {
		this.statusDescriptions = statusDescriptions;
	}

	public String getInstanceID() {
		return instanceID;
	}

	// @JsonProperty("InstanceID")
	public void setInstanceID(String instanceID) {
		this.instanceID = instanceID;
	}

	public String getPriority() {
		return Priority;
	}

	public void setPriority(String priority) {
		Priority = priority;
	}

	public String getCommunicationStatus() {
		return CommunicationStatus;
	}

	public void setCommunicationStatus(String communicationStatus) {
		CommunicationStatus = communicationStatus;
	}

	public int getJobRunTimes() {
		return JobRunTimes;
	}

	public void setJobRunTimes(int jobRunTimes) {
		JobRunTimes = jobRunTimes;
	}

	public String getOtherRecoveryAction() {
		return OtherRecoveryAction;
	}

	public void setOtherRecoveryAction(String otherRecoveryAction) {
		OtherRecoveryAction = otherRecoveryAction;
	}

	public String getDetailedStatus() {
		return DetailedStatus;
	}

	public void setDetailedStatus(String detailedStatus) {
		DetailedStatus = detailedStatus;
	}

	public String getUntilTime() {
		return UntilTime;
	}

	public void setUntilTime(String untilTime) {
		UntilTime = untilTime;
	}

	public int getJobState() {
		return JobState;
	}

	public void setJobState(int jobState) {
		JobState = jobState;
	}

	public String getDescription() {
		return Description;
	}

	public void setDescription(String description) {
		Description = description;
	}

	public String getRunDay() {
		return RunDay;
	}

	public void setRunDay(String runDay) {
		RunDay = runDay;
	}

	public String getTimeOfLastStateChange() {
		return TimeOfLastStateChange;
	}

	public void setTimeOfLastStateChange(String timeOfLastStateChange) {
		TimeOfLastStateChange = timeOfLastStateChange;
	}

	public String getRunMonth() {
		return RunMonth;
	}

	public void setRunMonth(String runMonth) {
		RunMonth = runMonth;
	}

	public int getErrorCode() {
		return ErrorCode;
	}

	public void setErrorCode(int errorCode) {
		ErrorCode = errorCode;
	}

	public String getRecoveryAction() {
		return RecoveryAction;
	}

	public void setRecoveryAction(String recoveryAction) {
		RecoveryAction = recoveryAction;
	}

	public int getPercentComplete() {
		return PercentComplete;
	}

	public void setPercentComplete(int percentComplete) {
		PercentComplete = percentComplete;
	}

	public String getLocalOrUtcTime() {
		return LocalOrUtcTime;
	}

	public void setLocalOrUtcTime(String localOrUtcTime) {
		LocalOrUtcTime = localOrUtcTime;
	}

	public String getStatus() {
		return Status;
	}

	public void setStatus(String status) {
		Status = status;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getInstallDate() {
		return InstallDate;
	}

	public void setInstallDate(String installDate) {
		InstallDate = installDate;
	}

	public String getRunDayOfWeek() {
		return RunDayOfWeek;
	}

	public void setRunDayOfWeek(String runDayOfWeek) {
		RunDayOfWeek = runDayOfWeek;
	}

	public String getElementName() {
		return ElementName;
	}

	public void setElementName(String elementName) {
		ElementName = elementName;
	}

	public String getJobStatus() {
		return JobStatus;
	}

	public void setJobStatus(String jobStatus) {
		JobStatus = jobStatus;
	}

	public String getElapsedTime() {
		return ElapsedTime;
	}

	public void setElapsedTime(String elapsedTime) {
		ElapsedTime = elapsedTime;
	}

	public String getCaption() {
		return Caption;
	}

	public void setCaption(String caption) {
		Caption = caption;
	}

	public boolean getDeleteOnCompletion() {
		return DeleteOnCompletion;
	}

	public void setDeleteOnCompletion(boolean deleteOnCompletion) {
		DeleteOnCompletion = deleteOnCompletion;
	}

	public String getTimeSubmitted() {
		return TimeSubmitted;
	}

	public void setTimeSubmitted(String timeSubmitted) {
		TimeSubmitted = timeSubmitted;
	}

	public String getPrimaryStatus() {
		return PrimaryStatus;
	}

	public void setPrimaryStatus(String primaryStatus) {
		PrimaryStatus = primaryStatus;
	}

	public String getErrorDescription() {
		return ErrorDescription;
	}

	public void setErrorDescription(String errorDescription) {
		ErrorDescription = errorDescription;
	}

	public String getRunStartInterval() {
		return RunStartInterval;
	}

	public void setRunStartInterval(String runStartInterval) {
		RunStartInterval = runStartInterval;
	}

	public String getScheduledStartTime() {
		return ScheduledStartTime;
	}

	public void setScheduledStartTime(String scheduledStartTime) {
		ScheduledStartTime = scheduledStartTime;
	}

	public List<Integer> getOperationalStatus() {
		return OperationalStatus;
	}

	public void setOperationalStatus(List<Integer> operationalStatus) {
		OperationalStatus = operationalStatus;
	}

	public String getOperatingStatus() {
		return OperatingStatus;
	}

	public void setOperatingStatus(String operatingStatus) {
		OperatingStatus = operatingStatus;
	}

	public String getNotify() {
		return Notify;
	}

	public void setNotify(String notify) {
		Notify = notify;
	}

	public String getOwner() {
		return Owner;
	}

	public void setOwner(String owner) {
		Owner = owner;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Jobs [");
		if (HealthState != null) {
			builder.append("HealthState=");
			builder.append(HealthState);
			builder.append(", ");
		}
		if (statusDescriptions != null) {
			builder.append("statusDescriptions=");
			for (String x : statusDescriptions) {
				builder.append(x);
				builder.append(", ");
			}
		}
		if (instanceID != null) {
			builder.append("instanceID=");
			builder.append(instanceID);
			builder.append(", ");
		}
		if (Priority != null) {
			builder.append("Priority=");
			builder.append(Priority);
			builder.append(", ");
		}
		if (CommunicationStatus != null) {
			builder.append("CommunicationStatus=");
			builder.append(CommunicationStatus);
			builder.append(", ");
		}
		builder.append("JobRunTimes=");
		builder.append(JobRunTimes);
		builder.append(", ");
		if (OtherRecoveryAction != null) {
			builder.append("OtherRecoveryAction=");
			builder.append(OtherRecoveryAction);
			builder.append(", ");
		}
		if (DetailedStatus != null) {
			builder.append("DetailedStatus=");
			builder.append(DetailedStatus);
			builder.append(", ");
		}
		if (UntilTime != null) {
			builder.append("UntilTime=");
			builder.append(UntilTime);
			builder.append(", ");
		}
		builder.append("JobState=");
		builder.append(JobState);
		builder.append(", ");
		if (Description != null) {
			builder.append("Description=");
			builder.append(Description);
			builder.append(", ");
		}
		if (RunDay != null) {
			builder.append("RunDay=");
			builder.append(RunDay);
			builder.append(", ");
		}
		if (TimeOfLastStateChange != null) {
			builder.append("TimeOfLastStateChange=");
			builder.append(TimeOfLastStateChange);
			builder.append(", ");
		}
		if (RunMonth != null) {
			builder.append("RunMonth=");
			builder.append(RunMonth);
			builder.append(", ");
		}
		builder.append("ErrorCode=");
		builder.append(ErrorCode);
		builder.append(", ");
		if (RecoveryAction != null) {
			builder.append("RecoveryAction=");
			builder.append(RecoveryAction);
			builder.append(", ");
		}
		builder.append("PercentComplete=");
		builder.append(PercentComplete);
		builder.append(", ");
		if (LocalOrUtcTime != null) {
			builder.append("LocalOrUtcTime=");
			builder.append(LocalOrUtcTime);
			builder.append(", ");
		}
		if (Status != null) {
			builder.append("Status=");
			builder.append(Status);
			builder.append(", ");
		}
		if (Name != null) {
			builder.append("Name=");
			builder.append(Name);
			builder.append(", ");
		}
		if (InstallDate != null) {
			builder.append("InstallDate=");
			builder.append(InstallDate);
			builder.append(", ");
		}
		if (RunDayOfWeek != null) {
			builder.append("RunDayOfWeek=");
			builder.append(RunDayOfWeek);
			builder.append(", ");
		}
		if (ElementName != null) {
			builder.append("ElementName=");
			builder.append(ElementName);
			builder.append(", ");
		}
		if (JobStatus != null) {
			builder.append("JobStatus=");
			builder.append(JobStatus);
			builder.append(", ");
		}
		if (ElapsedTime != null) {
			builder.append("ElapsedTime=");
			builder.append(ElapsedTime);
			builder.append(", ");
		}
		if (Caption != null) {
			builder.append("Caption=");
			builder.append(Caption);
			builder.append(", ");
		}
		builder.append("DeleteOnCompletion=");
		builder.append(DeleteOnCompletion);
		builder.append(", ");
		if (TimeSubmitted != null) {
			builder.append("TimeSubmitted=");
			builder.append(TimeSubmitted);
			builder.append(", ");
		}
		if (PrimaryStatus != null) {
			builder.append("PrimaryStatus=");
			builder.append(PrimaryStatus);
			builder.append(", ");
		}
		if (ErrorDescription != null) {
			builder.append("ErrorDescription=");
			builder.append(ErrorDescription);
			builder.append(", ");
		}
		if (RunStartInterval != null) {
			builder.append("RunStartInterval=");
			builder.append(RunStartInterval);
			builder.append(", ");
		}
		if (ScheduledStartTime != null) {
			builder.append("ScheduledStartTime=");
			builder.append(ScheduledStartTime);
			builder.append(", ");
		}
		if (OperationalStatus != null) {
			builder.append("OperationalStatus=");
			for (Integer x : OperationalStatus) {
				builder.append(x);
				builder.append(", ");
			}
		}
		if (OperatingStatus != null) {
			builder.append("OperatingStatus=");
			builder.append(OperatingStatus);
			builder.append(", ");
		}
		if (Notify != null) {
			builder.append("Notify=");
			builder.append(Notify);
			builder.append(", ");
		}
		if (Owner != null) {
			builder.append("Owner=");
			builder.append(Owner);
		}
		builder.append("]");
		return builder.toString();
	}

}
