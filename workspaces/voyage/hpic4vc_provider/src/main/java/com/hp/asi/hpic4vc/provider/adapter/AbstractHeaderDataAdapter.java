package com.hp.asi.hpic4vc.provider.adapter;

import java.util.concurrent.CountDownLatch;

import com.hp.asi.hpic4vc.provider.adapter.DataAdapter;
import com.hp.asi.hpic4vc.provider.model.BaseModel;
import com.hp.asi.hpic4vc.provider.model.HeaderModel;

public abstract class AbstractHeaderDataAdapter <T, S extends BaseModel> extends DataAdapter<T, S> {

    private CountDownLatch countdownLatch;
    protected HeaderModel headerModelToUpdate;
    long startTime;
    
    public AbstractHeaderDataAdapter (final Class<T> clazz, 
                                      final HeaderModel modelToUpdate,
                                      final CountDownLatch countdown) {
        super(clazz);
        this.startTime           = System.currentTimeMillis();
        this.countdownLatch      = countdown;
        this.headerModelToUpdate = modelToUpdate;
        
    }
    
    @Override
    public S call() {
        S returnVal = super.call();
        if (null != this.countdownLatch) {
            this.countdownLatch.countDown();
        }
        
        long delta = System.currentTimeMillis() - startTime;
        log.info("Data for " + this.getServiceName() + " took " + delta + " milliseconds.");
        return returnVal;
    }
    

}
