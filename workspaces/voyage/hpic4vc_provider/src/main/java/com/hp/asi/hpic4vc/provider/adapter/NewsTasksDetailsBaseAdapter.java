package com.hp.asi.hpic4vc.provider.adapter;

import com.hp.asi.hpic4vc.provider.adapter.TableDataAdapter;
import com.hp.asi.hpic4vc.provider.impl.SessionInfo.VObjectType;
import com.hp.asi.hpic4vc.provider.locale.I18NProvider;
import com.hp.asi.hpic4vc.provider.model.TableModel;


public abstract class NewsTasksDetailsBaseAdapter<T>  extends TableDataAdapter<T> {

    protected boolean isCluster = false;
    
    public NewsTasksDetailsBaseAdapter (Class<T> clazz) {
        super(clazz);
    }

    @Override
    public void setColumns(TableModel table) {
        if (VObjectType.CLUSTER == sessionInfo.getVobjectType()) {
            addClusterColumns(table);
            isCluster = true;
        }
        addNormalColumns(table);
    }
    
    public abstract void addClusterColumns(TableModel table);
    public abstract void addNormalColumns(TableModel table);
    
    protected String getIconClass(final String status) {
        String iconClass = status.toLowerCase();
        
        // The check is to make sure that the correct Error icon is displayed
        if(iconClass.equals("failed") || 
                iconClass.equals("cancelled") || 
                iconClass.equals("cancel_failed") ||
                iconClass.equals("canceled") || 
                iconClass.equals("error") ) {
            iconClass = "news_tasks_error";
        }
        
        return iconClass;
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
