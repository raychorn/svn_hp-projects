package com.hp.asi.hpic4vc.storage.provider.dam.model;

import java.util.ArrayList;
import java.util.List;

import com.hp.asi.hpic4vc.provider.model.BaseModel;

public class HierarchialData extends BaseModel {
    public String key;
    public String rawData;
    public String formattedData;
    public List<HierarchialData> childrenData;
    public String errorMessage;
    public String informationMessage;

    /**
     * An empty constructor is required for the AMF serialization to work!
     */
    public HierarchialData () {
        super();
        this.key = null;
        this.rawData = null;
        this.formattedData = null;
        this.childrenData = new ArrayList<HierarchialData>();
    }

    public HierarchialData (String key,
                            String rawData,
                            String formattedData,
                            List<HierarchialData> childrenData) {
        super();
        this.key = key;
        this.rawData = rawData;
        this.formattedData = formattedData;
        this.childrenData = childrenData;
    }

    public HierarchialData (String key,
                            String rawData,
                            String formattedData,
                            List<String> childKeyList,
                            List<String> childRawDataList,
                            List<String> childFormattedDataList) {
        super();
        this.key = key;
        this.rawData = rawData;
        this.formattedData = formattedData;

        childrenData = new ArrayList<HierarchialData>();
        if (null != childKeyList && 
                null != childRawDataList &&
                null != childFormattedDataList &&
                childKeyList.size() == childRawDataList.size() &&
                childRawDataList.size() == childFormattedDataList.size()) {
            for (int index = 0; index < childKeyList.size(); index++) {
                childrenData.add(new HierarchialData(childKeyList.get(index),
                        childRawDataList.get(index), childFormattedDataList
                                .get(index), null));
            }
        }
    }

    @Override
    public String toString () {
        return "HierarchialData [key=" + key + ", rawData=" + rawData
                + ", formattedData=" + formattedData + ", childrenData="
                + childrenData + ", errorMessage=" + errorMessage
                + ", informationMessage=" + informationMessage + "]";
    }

}
