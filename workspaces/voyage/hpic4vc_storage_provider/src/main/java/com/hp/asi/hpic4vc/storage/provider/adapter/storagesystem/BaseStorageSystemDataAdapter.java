package com.hp.asi.hpic4vc.storage.provider.adapter.storagesystem;

import java.net.URI;
import java.util.List;

import com.hp.asi.hpic4vc.provider.adapter.DataAdapter;
import com.hp.asi.hpic4vc.provider.dam.util.ModelObjectUriResolver;
import com.hp.asi.hpic4vc.provider.error.InitializationException;
import com.hp.asi.hpic4vc.provider.impl.SessionManagerImpl;
import com.hp.asi.hpic4vc.provider.locale.I18NProvider;
import com.hp.asi.hpic4vc.provider.model.BaseModel;

public abstract class BaseStorageSystemDataAdapter<T, S extends BaseModel> extends DataAdapter<T, S> {
    private static final ModelObjectUriResolver RESOURCE_RESOLVER = new ModelObjectUriResolver();
    
    /** For passing the storage system ID parameter to the WS via URL. */
    private static final String ARRAY_PARAMETER = "?&arrayUID=";
    
    protected static enum StorageTypeEnum {
        HP_XP        ("HP_XP"),
        HP_EVA       ("HP_EVA"),
        HP_MSA       ("HP_MSA"),
        HP_LEFTHAND  ("HP_LEFTHAND"),
        HP_P2000     ("HP_P2000"),
        HP_P9000     ("HP_P9000"),
        HP_3PAR      ("HP_P10000"),
        HP_STOREONCE ("HP_STOREONCE"),
        UNKNOWN      ("");
        
        private String searchString;
        StorageTypeEnum(String searchStr) {
            this.searchString = searchStr;
        }
        
        public static StorageTypeEnum getStorageTypeEnum(final String str) {
            StorageTypeEnum enumVal = UNKNOWN;
            for (StorageTypeEnum enumItem : StorageTypeEnum.values()) {
                if (str.equals(enumItem.searchString)) {
                    enumVal = enumItem;
                    break;
                }
            }
            return enumVal;
        }
    }

    
    protected String storageSystemUid;
    
    public BaseStorageSystemDataAdapter (final Class<T> clazz,  
                                         final String storageSystemUid) {
        super(clazz);
        URI uri = URI.create(storageSystemUid);
        try {
            this.storageSystemUid = RESOURCE_RESOLVER.getObjectSpecificId(uri);
        } catch (IllegalArgumentException e) {
            log.info("Setting storageSystemUid to " + storageSystemUid, e);
            this.storageSystemUid = storageSystemUid;
        }
    }
    
    @Override
    protected String getWsUrl () throws InitializationException {
        String baseUrl = SessionManagerImpl.getInstance().getWSURLHostname
            (sessionInfo.getSessionId(), sessionInfo.getServerGuid());
        
        StringBuilder url = new StringBuilder();
        url.append(baseUrl);
        url.append(this.getServiceName());
        url.append(ARRAY_PARAMETER);
        url.append(this.storageSystemUid);
        url.append("&sessionId=");
        url.append(sessionInfo.getSessionId());
        url.append("&locale=");
        url.append(sessionInfo.getLocale());
        
        return url.toString();        
    }

    /**
     * Checks data for null before returning. If data is null, an information
     * not available string is returned.
     * 
     * @param data
     *            the web service get method
     * @return the data passed or an info not available string
     */
    protected String nullCheckData (String data) {
        if (data != null) {
            return data;
        } else {
            return i18nProvider.getInternationalString
                    (locale, I18NProvider.Info_NotAvailable);
        }
    }
    
    /**
     * Returns a list as a single string separated by commas.
     * 
     * @param stringList
     *            list to become one string
     * @return
     */
    protected String printList(final List<String> stringList) {
        StringBuilder sb = new StringBuilder();
        String delimiter = "\n";
        
        if (null != stringList && stringList.size() > 0) {
            for (String item : stringList) {
                sb.append(item);
                sb.append(delimiter);
            }
            sb.delete(sb.length() - delimiter.length(), sb.length());
        }
        
        return sb.toString();
    }
    
    protected boolean isValidData(final String strToTest) {
        boolean isValid = true;
        String notAvailable = i18nProvider.getInternationalString
                (locale, I18NProvider.Info_NotAvailable);
        
        if (null == strToTest) {
            isValid = false;
        } else if (strToTest.equals("")) {
            isValid = false;
        } else if (strToTest.equals(notAvailable)) {
            isValid = false;
        }
        
        return isValid;
    }

}
