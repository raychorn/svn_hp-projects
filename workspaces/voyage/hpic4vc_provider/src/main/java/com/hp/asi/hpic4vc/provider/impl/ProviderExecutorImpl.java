package com.hp.asi.hpic4vc.provider.impl;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.asi.hpic4vc.provider.adapter.BaseAdapter;
import com.hp.asi.hpic4vc.provider.api.ProviderExecutor;
import com.hp.asi.hpic4vc.provider.model.BaseModel;

public final class ProviderExecutorImpl implements ProviderExecutor {

    private   static final Log log = LogFactory.getLog(ProviderExecutorImpl.class);
	protected static final ExecutorService EXECUTOR_SERVICE;

    
	static {
	    class HPThreadFactory implements ThreadFactory {
	        int i = 0;
	        public Thread newThread(Runnable r) {
	          return new Thread(r, "hpic4vc-thread-pool-" + i++);
	        }
	      }

        EXECUTOR_SERVICE = Executors.newCachedThreadPool(new HPThreadFactory());
    };
    
    public ProviderExecutorImpl() {
      }


    /**
     * Method to perform the bulk of the work for calling the base adapter to
     * get the data.
     * 
     * @param <S>
     *            The type of object returned from the web service.
     * @param callable
     *            The task to execute.
     * @return The table model that was created from the WS data.
     */
    public <T, S extends BaseModel> S execute (final BaseAdapter<T, S> callable,
                                               final String objReferenceId) {
        return execute(callable, new SessionInfo(objReferenceId));
    }

    /**
     * Method to perform the bulk of the work for calling the base adapter to
     * get the data.
     * 
     * @param <S>
     *            The type of object returned from the web service.
     * @param callable
     *            The task to execute.
     * @return The table model that was created from the WS data.
     */
    public <T, S extends BaseModel> S execute (final BaseAdapter<T, S> callable,
                                               final SessionInfo sessionInfo) {
        long startTime   = System.currentTimeMillis();
        S formattedData  = callable.getEmptyModel(); 
        Future<S> future = executeNoBlock(callable, sessionInfo);
        
        try {
            formattedData = future.get(callable.getTimeoutSeconds(), TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            // TODO Shailesh - create internationalized user-friendly error message.
            // If timeout - retry??
            formattedData.errorMessage = "Timeout occurred while requesting data.  Refresh the page.";
            log.info("Caught a TimeoutException for " + callable.getServiceName(), e);
        } catch (InterruptedException e) {
            // TODO Shailesh - create internationalized user-friendly error message.
            // If interrupted - retry??
            formattedData.errorMessage = "Interruption occurred while requesting data.  Refresh the page.";
            log.info("Caught an InterruptedException for " + callable.getServiceName(), e);
        } catch (ExecutionException e) {
            // TODO Shailesh - create internationalized user-friendly error message.
            formattedData.errorMessage = "Interruption occurred while requesting data.  Refresh the page.";
            log.info("Caught an ExecutionException for " + callable.getServiceName(), e);
        } 
        long stopTime = System.currentTimeMillis();
        long delta    = stopTime - startTime;
        log.info("Operation for " + callable.getServiceName() + " took " + delta + " milliseconds to execute.");
        return formattedData;
    }

    public <T, S extends BaseModel> Future<S> executeNoBlock (final BaseAdapter<T, S> callable,
                                               final SessionInfo sessionInfo) {
        callable.initialize(sessionInfo);
        return getExecutor().submit(callable);
    }
    
	ExecutorService getExecutor() {
	    return EXECUTOR_SERVICE;
    }

}