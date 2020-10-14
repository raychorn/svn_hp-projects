package com.hp.asi.hpic4vc.provider.adapter;

import java.util.Locale;

import com.hp.asi.hpic4vc.provider.error.InitializationException;
import com.hp.asi.hpic4vc.provider.impl.SessionInfo;
import com.hp.asi.hpic4vc.provider.impl.SessionManagerImpl;
import com.hp.asi.hpic4vc.provider.locale.I18NProvider;
import com.hp.asi.hpic4vc.provider.locale.LocaleHelper;

public class HelpMenuMaker {
	
	private static final String HELP_CONTENT = "static/webhelp/wc_content/";
	private static final String INDEX        = "index.html#";
	
	/**
	 * Returns the URL for the help for the vObject.
	 * @param sessionInfo
	 * @return A non-null URl.
	 */
    public static String makeComponentHelpUrl (final SessionInfo sessionInfo) {
    	if (null == sessionInfo) {
    		return null;
    	}
        String componentURL = getComponentUrl(sessionInfo);
        String hostName     = getHostName(sessionInfo);
        return hostName + HELP_CONTENT  + INDEX + componentURL;
    }
    
    /**
     * Returns the URL for the help for a specific topic.
     * @param sessionInfo
     * @param helpTopic
     * @return A non-null URl.
     */
    public static String makeHelpTopicUrl(final SessionInfo sessionInfo, 
    		final String helpTopic) {
    	if (null == sessionInfo || null == helpTopic) {
    		return null;
    	}
    	String hostName     = getHostName(sessionInfo);
        return hostName + HELP_CONTENT  + INDEX + helpTopic;
    }
    
    private static String getComponentUrl(final SessionInfo sessionInfo) {
    	Locale locale = LocaleHelper.getLocale(sessionInfo.getLocale());

        if (null != sessionInfo.getVobjectType()) {
            switch (sessionInfo.getVobjectType()) {
                case HOST:
                    return I18NProvider.getInstance().getInternationalString
                            (locale, I18NProvider.Help_Host_Page);
                case VM:
                    return I18NProvider.getInstance().getInternationalString
                            (locale, I18NProvider.Help_VM_Page);
                case DATASTORE:
                    return I18NProvider.getInstance().getInternationalString
                            (locale, I18NProvider.Help_Datastore_page);
                case CLUSTER:
                    return I18NProvider.getInstance().getInternationalString
                            (locale, I18NProvider.Help_Cluster_Page);
            }
        }
        return I18NProvider.getInstance().getInternationalString
                (locale, I18NProvider.Help_Default_Page);   
    }
    
    private static String getHostName(final SessionInfo sessionInfo) {
        String hostName;        
        
        try {
            hostName = SessionManagerImpl.getInstance().
                    getWSURLHostname(sessionInfo.getSessionId(),
                                     sessionInfo.getServerGuid());
        } catch (InitializationException e) {
            hostName = "http://localhost:8080/";
        }
        return hostName;
    }

}
