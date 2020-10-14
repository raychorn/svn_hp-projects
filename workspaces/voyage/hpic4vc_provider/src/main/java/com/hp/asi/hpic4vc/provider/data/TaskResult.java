/**
 * Copyright 2011 Hewlett-Packard Development Company, L.P.
 */
package com.hp.asi.hpic4vc.provider.data;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="task")
public class TaskResult {
    private Map<String, TaskData[]> taskMap;
    private String errorMessage;
    
    public TaskResult () {
        this.taskMap      = new HashMap<String, TaskData[]>();
        this.errorMessage = null;
    }

    
    public void setResult (Map<String, TaskData[]> dataMap) {
        this.taskMap = dataMap;
    }

    @XmlElement(name="result")
    public Map<String, TaskData[]> getResult () {
        return taskMap;
    }

    public void setErrorMessage (String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @XmlElement(name="errorMessage")
    public String getErrorMessage () {
        return errorMessage;
    }

    @Override
    public String toString () {
    	StringBuilder sb = new StringBuilder();
    	sb.append("TaskResult [errorMessage=");
    	sb.append(errorMessage);
    	sb.append(", taskMap=");
    	sb.append(taskMap.toString());
    	sb.append("]");
        return sb.toString();
    }
}
