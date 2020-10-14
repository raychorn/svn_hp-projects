package com.hp.asi.hpic4vc.provider.data;

import javax.xml.bind.annotation.XmlElement;

import com.hp.asi.hpic4vc.provider.model.BaseModel;

public class VCPageLinksData extends BaseModel {

    private PageLinksData cluster_overview;
    private PageLinksData datastore_overview;
    private PageLinksData storage_system_overview;
    private PageLinksData vm_overview;
    private PageLinksData vmTemplate_overview;
    private PageLinksData home_settings;
    private PageLinksData host_overview;

    public VCPageLinksData () {
    }

    @XmlElement(name = "cluster_overview")
    public PageLinksData getCluster_overview () {
        return this.cluster_overview;
    }

    public void setCluster_overview (PageLinksData cluster_overview) {
        this.cluster_overview = cluster_overview;
    }

    @XmlElement(name = "datastore_overview")
    public PageLinksData getDatastore_overview () {
        return this.datastore_overview;
    }

    public void setDatastore_overview (PageLinksData datastore_overview) {
        this.datastore_overview = datastore_overview;
    }

    @XmlElement(name = "storage_system_overview")
    public PageLinksData getStorage_system_overview () {
        return this.storage_system_overview;
    }

    public void setStorage_system_overview (PageLinksData storage_system_overview) {
        this.storage_system_overview = storage_system_overview;
    }

    @XmlElement(name = "vm_overview")
    public PageLinksData getVm_overview () {
        return this.vm_overview;
    }

    public void setVm_overview (PageLinksData vm_overview) {
        this.vm_overview = vm_overview;
    }

    @XmlElement(name = "vmTemplate_overview")
    public PageLinksData getVmTemplate_overview () {
        return this.vmTemplate_overview;
    }

    public void setVmTemplate_overview (PageLinksData vmTemplate_overview) {
        this.vmTemplate_overview = vmTemplate_overview;
    }

    @XmlElement(name = "home_settings")
    public PageLinksData getHome_settings () {
        return this.home_settings;
    }

    public void setHome_settings (PageLinksData home_settings) {
        this.home_settings = home_settings;
    }

    @XmlElement(name = "host_overview")
    public PageLinksData getHost_overview () {
        return this.host_overview;
    }

    public void setHost_overview (PageLinksData host_overview) {
        this.host_overview = host_overview;
    }

}
