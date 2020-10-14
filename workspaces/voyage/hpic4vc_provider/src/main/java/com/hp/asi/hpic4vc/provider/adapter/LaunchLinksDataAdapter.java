package com.hp.asi.hpic4vc.provider.adapter;

import java.util.ArrayList;
import java.util.List;

import com.hp.asi.hpic4vc.provider.data.LaunchLink;
import com.hp.asi.hpic4vc.provider.data.LaunchResult;
import com.hp.asi.hpic4vc.provider.data.LaunchTool;
import com.hp.asi.hpic4vc.provider.model.FooterModel;
import com.hp.asi.hpic4vc.provider.model.LaunchToolModel;
import com.hp.asi.hpic4vc.provider.model.LinkModel;

public class LaunchLinksDataAdapter extends DataAdapter<LaunchResult, FooterModel> {
    
    public LaunchLinksDataAdapter() {
        super(LaunchResult.class);
    }

    @Override
    public FooterModel formatData (LaunchResult rawData) {
        FooterModel footer = new FooterModel();
        
        if (null == rawData.getLaunch_tools()) {
            log.debug("LaunchResult.getLaunch_tools() is null");
            footer.errorMessage = "launchTools is null:Unable to get information for the management links.";
            return footer;
        }        

        for (LaunchTool launchTool : rawData.getLaunch_tools()) {
            footer.launchTools.add(createLaunchToolModel(launchTool));
        }
           
        return footer;
    }

    @Override
    public String getServiceName () {
        String serviceName = "";
        if (null != sessionInfo.getVobjectType()) {
            switch (sessionInfo.getVobjectType()) {
                case HOST:
                    serviceName = "agservices/launchtools"; 
                    break;
                case VM:
                    serviceName = "agservices/launchtools";
                    break;
                case DATASTORE:
                    serviceName = "agservices/launchtools";
                    break;
                case CLUSTER:
                    serviceName = "agservices/clusterlaunchtools";
                    break;
                default:
                        break;
            }
        }      
        return serviceName;
    }

    @Override
    public FooterModel getEmptyModel () {
        return new FooterModel();
    }
    
    private LaunchToolModel createLaunchToolModel (LaunchTool launchTool) {
        LaunchToolModel launchToolModel = new LaunchToolModel();
        launchToolModel.id    = launchTool.getId();
        launchToolModel.links = createLaunchLinks(launchTool.getLaunch_links());
        return launchToolModel;
    }

    private List<LinkModel> createLaunchLinks (LaunchLink[] launchLinks) {
        List<LinkModel> links = new ArrayList<LinkModel>();
        if (null != launchLinks) {
            LinkModel linkModel;
            for (LaunchLink link : launchLinks) {
                linkModel             = new LinkModel();
                linkModel.displayName = link.getLabel();
                linkModel.type        = link.getType();
                linkModel.url         = link.getUrl();
                linkModel.urlBase     = link.getUrl_base();
                linkModel.username    = link.getUsername();
                linkModel.password    = link.getPassword();
                links.add(linkModel);
            }
        }
        return links;
    }
    

}
