package com.hp.asi.hpic4vc.provider.adapter;

import java.util.Map.Entry;
import java.util.concurrent.CountDownLatch;

import com.hp.asi.hpic4vc.provider.data.TaskData;
import com.hp.asi.hpic4vc.provider.data.TaskResult;
import com.hp.asi.hpic4vc.provider.locale.I18NProvider;
import com.hp.asi.hpic4vc.provider.model.HeaderModel;
import com.hp.asi.hpic4vc.provider.model.TaskModel;
import com.hp.asi.hpic4vc.provider.model.TaskSummary;

public class TaskSummaryDataAdapter extends AbstractHeaderDataAdapter<TaskResult, TaskSummary> {
    private static final String SERVICE_NAME = "tasks/?top=10&";
    
    public TaskSummaryDataAdapter (final HeaderModel headerModel,
                                   final CountDownLatch countdown) {
        super(TaskResult.class, headerModel, countdown);
    }

    @Override
    public TaskSummary formatData (TaskResult rawData) {
        TaskSummary summary = new TaskSummary();
        
        if (null == rawData.getResult()) {
            String errorMessage = this.i18nProvider.getInternationalString
                    (locale, I18NProvider.Tasks_NoData);
            summary.errorMessage = errorMessage;
            log.debug("TaskResult.getResult() is null.  No task items to report.");
            return summary;
        }

        for (Entry<String, TaskData[]> taskMap : rawData.getResult().entrySet() ) {
            for (TaskData task : taskMap.getValue()) {
                summary.addTask(createTaskModel(task));
            }
        }
        
        if (null != headerModelToUpdate) {
            this.headerModelToUpdate.tasks = summary.taskItems;
        }
        return summary;
    }
    
    
    @Override
    public String getServiceName () {
        return SERVICE_NAME;
    }

    @Override
    public TaskSummary getEmptyModel () {
        return new TaskSummary();
    }
    
    private TaskModel createTaskModel(final TaskData task) {
        String formattedStartTime = I18NProvider.getInstance().getLocaleDateFromEpochTime
                (task.getStartTime(), locale);
        
        String formattedCompletedTime = I18NProvider.getInstance().getLocaleDateFromEpochTime
                (task.getCompletedTime(), locale);

        TaskModel taskModel     = new TaskModel();
        taskModel.completedTime = formattedCompletedTime;
        taskModel.startTime     = formattedStartTime;
        taskModel.status        = task.getStatus();
        taskModel.taskDetails   = getFormattedMessage(task.getTaskDetails(), 
                                                      task.getTaskDetailArguments());
        taskModel.taskName      = task.getTaskName();
        taskModel.username      = task.getUserName();
        taskModel.name          = task.getName();
        
        return taskModel;
    }

    protected String getFormattedMessage(final String message, final String[] args) {
        if (null == args || args.length < 2) {
            return message;
        }

        String formatter = I18NProvider.News_Tasks_Message;
        if (message.equalsIgnoreCase("task.queued")) {
            formatter = I18NProvider.News_Tasks_Queued;
        } else if (message.equalsIgnoreCase("task.error")) {
            formatter = I18NProvider.News_Tasks_Error;
        } else if (message.equalsIgnoreCase("task.success")) {
            formatter = I18NProvider.News_Tasks_Success;
        } else if (message.equalsIgnoreCase("task.running")) {
            formatter = I18NProvider.News_Tasks_Running;
        } else if (message.equalsIgnoreCase("task.jobstepmessage")) {
            formatter = I18NProvider.News_Tasks_JobStepMessage;
        } 

        return this.i18nProvider.getInternationalString
                (locale, 
                 formatter, 
                 (Object[])args);

    }
}
