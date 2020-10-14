package com.hp.asi.hpic4vc.provider.adapter;

import java.util.ArrayList;
import java.util.List;

import com.hp.asi.hpic4vc.provider.data.TabResult;
import com.hp.asi.hpic4vc.provider.locale.I18NProvider;
import com.hp.asi.hpic4vc.provider.model.PortletModel;
import com.hp.asi.hpic4vc.provider.model.TabModel;

/**
 * Returns the list of portlets to display on the vCenter's "Summary" page.
 */
public class SummaryPortletListDataAdapter extends DataAdapter<TabResult[], PortletModel>{
    
    public SummaryPortletListDataAdapter () {
        super(TabResult[].class);
    }

    @Override
    public PortletModel getEmptyModel () {
        return new PortletModel();
    }
    
    @Override
    public String getServiceName () {
        String serviceName = "";
        if (null != sessionInfo.getVobjectType()) {
            switch (sessionInfo.getVobjectType()) {
                case HOST:
                    serviceName = "config/flex/monitor/host/summary_portlet"; 
                    break;
                case VM:
                    serviceName = "config/flex/monitor/vm/summary_portlet";
                    break;
                case DATASTORE:
                    serviceName = "config/flex/monitor/datastore/summary_portlet";
                    break;
                case CLUSTER:
                    serviceName = "config/flex/monitor/cluster/summary_portlet";
                    break;
                default:
                    log.info("Invalid VMware Object type retrieved: " + 
                              sessionInfo.getVobjectType().toString());
                    break;
            }
        }      
        return serviceName;
    }
    
    @Override
    public PortletModel formatData (TabResult[] rawData) {
        PortletModel portletModel = new PortletModel();

        if (null == rawData || rawData.length < 1) {
            portletModel.informationMessage = this.i18nProvider.getInternationalString
                    (locale, I18NProvider.Info_NoRecords);
            log.debug("The data had no records.  " +
            		"Returning a page model with an error message set.");
            return portletModel;
        }

        portletModel.portlets = createPortletList(rawData);              
        return portletModel;
    }

    
    protected List<TabModel> createPortletList (TabResult[] tabResults) {
        List<TabModel> tabList = new ArrayList<TabModel>();
        
        if (null == tabResults) {
            return tabList;
        }
        
        for (TabResult result : tabResults) {
            tabList.add(createPortlet(result));
        }
        return tabList;
    }
    
    protected TabModel createPortlet(TabResult result) {
        TabModel model     = new TabModel();
        model.order        = result.getOrder();
        model.component    = result.getComponent();
        return model;
    }

}
