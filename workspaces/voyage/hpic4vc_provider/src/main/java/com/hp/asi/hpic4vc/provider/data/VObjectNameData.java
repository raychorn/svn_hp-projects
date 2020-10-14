package com.hp.asi.hpic4vc.provider.data;

import javax.xml.bind.annotation.XmlElement;

public class VObjectNameData {
    private String vmware_name;
    private String cluster_name;
    private String host_name;
    private String enclosure;
    private String iloServerName;
    private String bay;
    private String proudctName;
    private boolean blade;

    public VObjectNameData () {
    }
    
    @XmlElement(name="vmware_name") 
    public String getVmware_name () {
        return vmware_name;
    }
    
    public void setVmware_name (String name) {
        this.vmware_name = name;
    }
    
    @XmlElement(name="cluster_name") 
    public String getCluster_name () {
        return cluster_name;
    }
    
    public void setCluster_name (String name) {
        this.cluster_name = name;
    }
    
    @XmlElement(name="host_name") 
    public String getHost_name () {
        return host_name;
    }
    
    public void setHost_name (String name) {
        this.host_name = name;
    }
    
    @XmlElement(name="enclosure") 
    public String getEnclosure () {
        return this.enclosure;
    }
    
    public void setEnclosure (String enclosure) {
        this.enclosure = enclosure;
    }
    
    @XmlElement(name="ilo_server_name") 
    public String getIlo_server_name () {
        return this.iloServerName;
    }
    
    public void setIlo_server_name (String serverName) {
        this.iloServerName = serverName;
    }
    
    @XmlElement(name="product_name") 
    public String getProduct_name () {
        return this.proudctName;
    }
    
    public void setProduct_name (String name) {
        this.proudctName = name;
    }
    
    @XmlElement(name="bay") 
    public String getBay () {
        return this.bay;
    }
    
    public void setBay (String bay) {
        this.bay = bay;
    }
    
    public boolean isBlade () {
        return blade;
    }

    @XmlElement(name="blade")
    public void setBlade (boolean blade) {
        this.blade = blade;
    }

    @Override
    public String toString () {
        return "VObjectNameData [vmware_name=" + vmware_name
                + ", cluster_name=" + cluster_name + ", host_name=" + host_name
                + ", enclosure=" + enclosure + ", iloServerName="
                + iloServerName + ", bay=" + bay + ", proudctName="
                + proudctName + ", blade=" + blade + "]";
    }

}
