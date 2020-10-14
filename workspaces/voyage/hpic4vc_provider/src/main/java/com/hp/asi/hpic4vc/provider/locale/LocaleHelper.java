package com.hp.asi.hpic4vc.provider.locale;

import java.util.Locale;

public class LocaleHelper {    
    
    enum LocaleEnum {
        US    ("en_US", Locale.US),
        SA    ("ar_SA", new Locale("ar", "SA")),
        CN    ("zh_CN", Locale.SIMPLIFIED_CHINESE),
        TW    ("zh_TW", Locale.TAIWAN),
        NL    ("nl_NL", new Locale("nl", "NL")),
        AU    ("en_AU", Locale.ENGLISH),
        CA    ("en_CA", Locale.CANADA),
        GB    ("en_GB", Locale.UK),
        FR_CA ("fr_CA", Locale.CANADA_FRENCH),
        FR    ("fr_FR", Locale.FRANCE),
        DE    ("de_DE", Locale.GERMANY),
        IL    ("he_IL", Locale.US),
        IN    ("hi_IN", Locale.US),
        IT    ("it_IT", Locale.ITALY),
        JP    ("ja_JP", Locale.JAPAN),
        KR    ("ko_KR", Locale.KOREA),
        BR    ("pt_BR", new Locale("pt", "BR")),
        ES    ("es_ES", new Locale("es", "ES")),
        SV    ("sv_SE", new Locale ("sv", "SE"));
        
        String localeName;
        Locale locale;
        
        LocaleEnum(String str, Locale loc) {
            this.localeName = str;
            this.locale     = loc;
        }
        
        String getLocaleName() {
            return localeName;
        }
        
        Locale getLocale() {
            return locale;
        }
    }
    
    public static Locale getLocale (String localeIdString) {
        if (null == localeIdString) {
            return null;
        }
        for (LocaleEnum enumVal : LocaleEnum.values()) {
            if (enumVal.getLocaleName().equals(localeIdString)) {
                return enumVal.getLocale();
            }                
        }
        return null;
        
    }

}
