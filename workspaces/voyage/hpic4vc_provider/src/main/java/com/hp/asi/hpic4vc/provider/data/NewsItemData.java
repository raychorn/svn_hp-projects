/**
 * Copyright 2011 Hewlett-Packard Development Company, L.P.
 */
package com.hp.asi.hpic4vc.provider.data;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "newsItem")
public class NewsItemData implements Comparable<NewsItemData> {

    private String status       = null;
    private String name         = null;
    private String vc_id        = null;
    private String vc_uuid      = null;
    private String _id          = null;
    
    private String formattedEventDate = null;
    private String eventSource        = null;
    private String[] messageArguments = null;
    private String message            = null;
    private double eventDate          = 0;
    private String pluginSource       = null;
    private String objectName         = null;

    public NewsItemData () {
    }

    @XmlElement(name = "status")
    public String getStatus () {
        return status;
    }

    public void setStatus (final String status) {
        this.status = status;
    }

    @XmlElement(name = "objectName")
    public String getObjectName () {
        return objectName;
    }

    public void setObjectName (final String objectName_in) {
        this.objectName = objectName_in;
    }

    @XmlElement(name = "vc_uuid")
    public String getVc_uuid () {
        return vc_uuid;
    }

    public void setVc_uuid (final String vc_uuid) {
        this.vc_uuid = vc_uuid;
    }

    @XmlElement(name = "formattedEventDate")
    public String getFormattedEventDate () {
        return formattedEventDate;
    }

    public void setFormattedEventDate (final String date) {
        this.formattedEventDate = date;
    }

    @XmlElement(name = "eventSource")
    public String getEventSource () {
        String friendlyName;
        
        // Storage team puts the friendly name in "objectName"
        if (null != objectName && !objectName.equals("")) {
            friendlyName = objectName;
        } else {
            friendlyName = eventSource;
        }
        return friendlyName;
    }

    public void setEventSource (final String source) {
        this.eventSource = source;
    }

    @XmlElement(name = "vc_id")
    public String getVc_id () {
        return vc_id;
    }

    public void setVc_id (final String vc_uid) {
        this.vc_uuid = vc_id;
    }

    @XmlElement(name = "messageArguments")
    public String[] getMessageArguments () {
        return messageArguments;
    }

    public void setMessageArguments (final String[] arguments) {
        this.messageArguments = arguments;
    }

    @XmlElement(name = "message")
    public String getMessage () {
        return message;
    }

    public void setMessage (final String message) {
        this.message = message;
    }

    @XmlElement(name = "_id")
    public String get_id () {
        return this._id;
    }

    public void set_id (final String id) {
        this._id = id;
    }

    @XmlElement(name = "eventDate")
    public double getEventDate () {
        return this.eventDate;
    }

    public void setEventDate (final double date) {
        this.eventDate = date;
    }

    @XmlElement(name = "pluginSource")
    public String getPluginSource () {
        return this.pluginSource;
    }

    public void setPluginSource (final String source) {
        this.pluginSource = source;
    }
    
    @XmlElement(name = "name")
    public String getName () {
        return this.name;
    }

    public void setName (final String name) {
        this.name = name;
    }

    @Override
    public String toString () {
        return "NewsItemData [status=" + status + ", vc_uuid=" + vc_uuid
                + ", formattedEventDate=" + formattedEventDate
                + ", eventSource=" + eventSource + ", vc_id=" + vc_id
                + ", messageArguments=" + Arrays.toString(messageArguments)
                + ", message=" + message + ", _id=" + _id + ", eventDate="
                + eventDate + ", pluginSource=" + pluginSource
                + ", objectName=" + objectName + ", name= " + name + "]";
    }

    @Override
    public int compareTo (NewsItemData other) {
        // Want the most recent items first
        if (this.eventDate < other.eventDate) {
            return 1;
        } else {
            return -1;
        }
    }

}
