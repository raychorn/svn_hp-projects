package com.hp.asi.hpic4vc.provider.model;

import java.util.ArrayList;
import java.util.List;

public class TaskSummary extends BaseModel{
    public List<TaskModel> taskItems;
    
    public TaskSummary() {
        super();
        this.taskItems = new ArrayList<TaskModel>();
    }
    
    public TaskSummary(List<TaskModel> tasks) {
        super();
        this.taskItems = tasks;
    }
    
    public void addTask(final TaskModel task) {
        this.taskItems.add(task);
    }

	@Override
	public String toString() {
		return "TaskSummary [taskItems=" + taskItems + ", errorMessage="
				+ errorMessage + ", informationMessage=" + informationMessage
				+ "]";
	}

}
