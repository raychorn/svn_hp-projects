package com.hp.asi.hpic4vc.provider.api;

import java.util.concurrent.Future;

import com.hp.asi.hpic4vc.provider.adapter.BaseAdapter;
import com.hp.asi.hpic4vc.provider.impl.SessionInfo;
import com.hp.asi.hpic4vc.provider.model.BaseModel;

public interface ProviderExecutor {
    
    /**
     * Method to perform the bulk of the work for calling the data handler
     * to get the data.
     * 
     * @param <S>       The type of object returned from the web service.
     * @param callable  The task to execute.
     * @return          The table model that was created from the WS data.
     */
    public <T, S extends BaseModel> S execute(final BaseAdapter<T, S> callable, 
                                              final String objReferenceId);
    
    /**
     * Method to perform the bulk of the work for calling the data handler
     * to get the data.
     * 
     * @param <S>       The type of object returned from the web service.
     * @param callable  The task to execute.
     * @return          The table model that was created from the WS data.
     */
    public <T, S extends BaseModel> S execute(final BaseAdapter<T, S> callable, 
                                              final SessionInfo sessionInfo);
    
    /**
     * Method to get the future task but not return the actual object.  Most
     * adapters will not need to use this.  This is used by the HeaderModel
     * so that it can collect multiple pieces of data simultaneously with blocking.
     * Since most adapters need to wait to get the data before they can return,
     * most adapters will use the execute method.
     * @param callable
     * @param sessionInfo
     * @return
     */
    public <T, S extends BaseModel> Future<S> executeNoBlock (final BaseAdapter<T, S> callable,
                                                              final SessionInfo sessionInfo);
}
