package com.hp.asi.hpic4vc.provider.adapter;

import java.util.Map.Entry;

import com.hp.asi.hpic4vc.provider.data.TaskData;
import com.hp.asi.hpic4vc.provider.data.TaskResult;
import com.hp.asi.hpic4vc.provider.locale.I18NProvider;
import com.hp.asi.hpic4vc.provider.model.TableModel;
import com.hp.asi.hpic4vc.provider.model.TableModel.TableRow;

public class TaskDetailsDataAdapter extends NewsTasksDetailsBaseAdapter<TaskResult> {
    private static final String SERVICE_NAME = "tasks/";
    
    public TaskDetailsDataAdapter(){
        super(TaskResult.class);
    }

    @Override
    public String getServiceName () {
        return SERVICE_NAME;
    }
    
    @Override
    public String getErrorMsg (TaskResult rawData) {
        return rawData.getErrorMessage();
    }

    @Override
    public boolean isResultEmpty (TaskResult rawData) {
        if (null == rawData.getResult()) {
            return true;
        }
        return false;
    }
    
    @Override
    public void setRows (TableModel table, final TaskResult rawData) {        
        for (Entry<String, TaskData[]> taskMap : rawData.getResult().entrySet() ) {
            for (TaskData task : taskMap.getValue()) {
                TableRow row = new TableRow();
                if (isCluster) {
                    populateClusterData(row, task);
                }
                populateNormalData(row, task);
                table.addRow(row);
            }
        }
      
    }
    
    @Override
    public void addNormalColumns (TableModel table) {
        String status         = this.i18nProvider.getInternationalString
                                           (locale, I18NProvider.News_Tasks_Status);
        String taskName       = this.i18nProvider.getInternationalString
                                           (locale, I18NProvider.Tasks_Name);
        String taskDetails    = this.i18nProvider.getInternationalString
                                           (locale, I18NProvider.Tasks_Details);
        String userName       = this.i18nProvider.getInternationalString
                                           (locale, I18NProvider.Tasks_UserName);
        String startTime      = this.i18nProvider.getInternationalString
                                           (locale, I18NProvider.Tasks_StartTime);
        String completedTime  = this.i18nProvider.getInternationalString
                                           (locale, I18NProvider.Tasks_CompletedTime);
        
        table.addColumn(status,        null, "150", null, true, false, true);
        table.addColumn(taskName,      null, "150", null, true, false, false);       
        table.addColumn(taskDetails,   null, "500", null, true, false, false); 
        table.addColumn(userName,      null, "150", null, true, false, false);
        table.addColumn(startTime,     null, "200", null, true, false, false);
        table.addColumn(completedTime, null, "200", null, true, false, false);
        
    }
    
    @Override
    public void addClusterColumns (TableModel table) {
        String host = this.i18nProvider
                .getInternationalString(locale, I18NProvider.Tasks_Target);

        table.addColumn(host, null, "150", null, true, false, false);
    }


    
    private void populateNormalData(TableRow row, final TaskData task ) {
        String id                 = null;
        String rawStartTime       = Double.toString(task.getStartTime());
        String rawCompletedTime   = Double.toString(task.getCompletedTime());
        
        String formattedStartTime = I18NProvider.getInstance().getLocaleDateFromEpochTime
                (task.getStartTime(), locale);
        
        String formattedCompletedTime = I18NProvider.getInstance().getLocaleDateFromEpochTime
                (task.getCompletedTime(), locale);
        

         String iconClass = super.getIconClass(task.getStatus());
         String message   = getFormattedMessage(task.getTaskDetails(), task.getTaskDetailArguments());
         row.addCell(id, task.getStatus(),        task.getStatus(), 	  iconClass);
         row.addCell(id, task.getTaskName(),      task.getTaskName(), 	  null);
         row.addCell(id, message,                 message,   null);
         row.addCell(id, task.getUserName(),      task.getUserName(),	  null);
         row.addCell(id, rawStartTime,            formattedStartTime, 	  null);     
         row.addCell(id, rawCompletedTime,        formattedCompletedTime, null);
    }
    
    private void populateClusterData(TableRow row, final TaskData task) {
        String id = null;
        row.addCell(id,  task.getName(), task.getName(), null);
        
    }
}
