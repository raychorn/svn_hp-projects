/**
 * Copyright 2012 Hewlett-Packard Development Company, L.P. 
 */
package com.hp.asi.hpic4vc.provider.model;

public class TaskModel extends BaseModel {
    public String status;
    public String taskName;
    public String taskDetails;
    public String username;
    public String startTime;
    public String completedTime;
    public String name;

    
    /**
     * An empty constructor is required for the AMF serialization to work!
     */
    public TaskModel() {
        super();
        this.status         = new String();
        this.taskName       = new String();
        this.taskDetails    = new String();
        this.username       = new String();
        this.startTime      = new String();
        this.completedTime  = new String();
        this.name 			= new String();
    }    
        
    public TaskModel (String status,
            String taskName,
            String taskDetails,
            String username,
            String startTime,
            String completedTime,
            String name) {

        super();
        this.status         = status;
        this.taskName       = taskName;
        this.taskDetails    = taskDetails;
        this.username       = username;
        this.startTime      = startTime;
        this.completedTime  = completedTime;
        this.name			= name;

    }

    @Override
	public String toString() {
		return "TaskModel [status=" + status + ", taskName=" + taskName
				+ ", taskDetails=" + taskDetails + ", username=" + username
				+ ", startTime=" + startTime + ", completedTime="
				+ completedTime +",name=" + name + ", errorMessage=" + errorMessage
				+ ", informationMessage=" + informationMessage + "]";
	}


}
