/**
 * Copyright 2011 Hewlett-Packard Development Company, L.P.
 */
package com.hp.asi.hpic4vc.provider.data;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name="task")
public class TaskData implements Comparable<TaskData> {   
	
    private String   status                 = null;
    private String   name                   = null;
    private String   vc_id                  = null;
    private String   vc_uuid                = null;
    private String   _id                    = null;
    
    private String   userName               = null;
    private String[] taskDetailArgs         = null;
    private double   startTime              = 0;
    private String   formattedStartTime     = null;
    private double   completedTime          = 0;
    private String[] taskNameArgs           = null;
    private String   taskName               = null;
    private String   taskDetails            = null;
    private String   formattedCompletedTime = null;
    
    public TaskData () {
    }
    
    @XmlElement(name="status") 
    public String getStatus() {
        return status;
    }
    
    public void setStatus(final String status) {
        this.status = status;
    }
    
    @XmlElement(name="name") 
    public String getName() {
        return name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    @XmlElement(name="userName") 
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(final String name) {
        this.userName = name;
    }
    
    @XmlElement(name="vc_uuid") 
    public String getVc_uuid() {
        return vc_uuid;
    }
    
    public void setVc_uuid(final String vc_uuid) {
        this.vc_uuid = vc_uuid;
    }
    
    @XmlElement(name="taskDetailArguments") 
    public String[] getTaskDetailArguments() {
        return taskDetailArgs;
    }
    
    public void setTaskDetailArguments(final String[] arguments) {
        this.taskDetailArgs = arguments;
    }
    
    @XmlElement(name="startTime") 
    public double getStartTime() {
        return startTime;
    }
    
    public void setStartTime(final double time) {
        this.startTime = time;
    }
    
    @XmlElement(name="formattedStartTime") 
    public String getFormattedStartTime() {
        return formattedStartTime;
    }
    
    public void setFormattedStartTime(final String time) {
        this.formattedStartTime = time;
    }
    
    @XmlElement(name="completedTime") 
    public double getCompletedTime() {
        return completedTime;
    }
    
    public void setCompletedTime(double time) {
        this.completedTime = time;
    }
    
    @XmlElement(name="taskNameArguments") 
    public String[] getTaskNameArguments() {
        return taskNameArgs;
    }
    
    public void setTaskNameArguments(final String[] arguments) {
        this.taskNameArgs = arguments;
    }

    @XmlElement(name="vc_id") 
    public String getVc_id() {
        return vc_id;
    }
    
    public void setVc_id(final String vc_uid) {
        this.vc_uuid = vc_id;
    }

    @XmlElement(name="taskName") 
    public String getTaskName() {
        return taskName;
    }
    
    public void setTaskName(final String name) {
        this.taskName = name;
    }
    
    @XmlElement(name="_id") 
    public String get_id() {
        return this._id;
    }
    
    public void set_id(final String id) {
        this._id = id;
    }
    
    @XmlElement(name="taskDetails") 
    public String getTaskDetails() {
        return taskDetails;
    }
    
    public void setTaskDetails(final String details) {
        this.taskDetails = details;
    }
    
    @XmlElement(name="formattedCompletedTime") 
    public String getFormattedCompletedTime() {
        return formattedCompletedTime;
    }
    
    public void setFormattedCompletedTime(final String time) {
        this.formattedCompletedTime = time;
    }

    @Override
    public String toString () {
        return "TaskData [status=" + status + ", userName=" + userName
                + ", vc_uuid=" + vc_uuid + ", taskDetailArgs="
                + Arrays.toString(taskDetailArgs) + ", startTime=" + startTime
                + ", formattedStartTime=" + formattedStartTime
                + ", completedTime=" + completedTime + ", taskNameArgs="
                + Arrays.toString(taskNameArgs) + ", vc_id=" + vc_id
                + ", taskName=" + taskName + ", _id=" + _id + ", taskDetails="
                + taskDetails + ", formattedCompletedTime="
                + formattedCompletedTime + "]";
    }

	@Override
	public int compareTo(TaskData other) {
		if (this.startTime < other.startTime) {
			return 1;
		} else {
			return -1;
		}
	}
}
