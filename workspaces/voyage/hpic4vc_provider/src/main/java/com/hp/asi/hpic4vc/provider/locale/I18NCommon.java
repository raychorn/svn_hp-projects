package com.hp.asi.hpic4vc.provider.locale;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public abstract class I18NCommon {
    /** This is the error message thrown for an illegal argument. **/
    private static final String NULL_ARGUMENT_PASSED_STRING = "%1$s was passed a null %2$s object.";
    /** This is the name of the resource bundle. **/
    protected static final String RESOURCE_BUNDLE_BASE_NAME = "%1$s.Messages";
    private static final String EXCEPTION_CAUGHT = "%1$s caught when getting %2$s";
    private static final String FORMATTER_ERROR  = 
            "getInternationalString(%1$s) could not apply pattern because the tag wasn't found.";


    private static final long MILLIS_THRESHOLD = 1000000000000L;
    private final Log LOG;

    /**
     * Accessor method to get the child class.
     * 
     * @return The child class
     */
    protected abstract Class<? extends I18NCommon> getChildClass ();

    /**
     * Accessor method to get the resource bundle.
     * 
     * @param locale
     * @return The resource bundle
     */
    protected abstract ResourceBundle getResourceBundle (Locale locale);

    /**
     * The constructor sets the values of the constants.
     */
    protected I18NCommon () {
        LOG = LogFactory.getLog(getChildClass());
    }

    public String getInternationalString (Locale locale, String tag) {
        return getString(locale, tag);
    }

    public String getInternationalString (Locale locale,
                                          String tag,
                                          Object... args) {
        try {
            validateArguments(locale, tag, "getInternationalString");
        } catch (IllegalArgumentException e) {
            return null;
        }

        MessageFormat formatter = new MessageFormat("");
        String str = getInternationalString(locale, tag);
        if (null == str) {
            String errorMsg = String.format(FORMATTER_ERROR,
                                            new Object[]{tag});
            LOG.warn(errorMsg);
            return null;
        } else {
            formatter.applyPattern(str);
            return formatter.format(args);
        }
    }

    private String getString (Locale locale, String tag) {
        try {
            validateArguments(locale, tag, "getString");
        } catch (IllegalArgumentException e) {
            return null;
        }
        
        try {
            ResourceBundle rb = getResourceBundle(locale);
            return rb.getString(tag);
        } catch (Exception e) {
            String errorTitle = String.format(EXCEPTION_CAUGHT,
                                              new Object[] {e.getClass().getName(), tag});
            LOG.warn(errorTitle);
            return null;
        }
    }

    /**
     * This method validates that the arguments are not null.
     * 
     * @param locale
     *            - The locale being internationalized to.
     * @param tag
     *            - The tag to be internationalized
     */
    private void validateArguments (Locale locale, String tag, String methodName) {
        if (null == locale) {
            String errorMsg = String
                    .format(NULL_ARGUMENT_PASSED_STRING, new Object[] {
                            methodName, "Locale" });
            LOG.warn(errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }

        if (null == tag) {
            String errorMsg = String.format(NULL_ARGUMENT_PASSED_STRING,
                                            new Object[] { methodName, "Tag" });
            LOG.warn(errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }
    }

    /**
     * This method converts an epoch time to the locale date format.
     * <code>DateFormat.LONG</code> specifies the date and time in January 12,
     * 1952 3:30:32pm format for <code>en_US</code> locale.
     * 
     * @param epochTime
     *            Epoch time as a double value.  Because UIM returns double 
     *            timestamps in microseconds, converts from microseconds
     *            to milliseconds before formatting.  If the input is 0, 
     *            returns an empty string.
     * @param locale
     *            A Locale object specifying the required locale value.
     * @return A String representation of the date in the specific locale
     *         format.
     */
    public String getLocaleDateFromEpochTime (double epochTime, Locale locale) {
        long timeInMillis = convertToMillis(epochTime);
        return getLocaleDateFromEpochTime(timeInMillis, locale);
    }
    
    /**
     * This method converts an epoch time passed as a String to the locale 
     * date format.  <code>DateFormat.LONG</code> specifies the date and time
     * in January 12, 1952 3:30:32pm format for <code>en_US</code> locale.
     * 
     * @param epochTime
     *            Epoch time as a String value.  Because some of the web services
     *            return String timestamps, convert to Long in order to format.
     *            In case of error, return null.
     * @param locale
     *            A Locale object specifying the required locale value.
     * @return A String representation of the date in the specific locale
     *         format.
     */
    public String getLocaleDateFromEpochTime (String epochTime, Locale locale) {
        String formattedTimeStamp = null;
        
        if (null != epochTime && !epochTime.equals("")) {
            try {
                long timeInMillis = Long.parseLong(epochTime);
                formattedTimeStamp = getLocaleDateFromEpochTime(timeInMillis, locale);
            } catch (NumberFormatException e) {
                LOG.warn("Caught a NumberFormatException converting " + epochTime +
                         " to a long value in the getLocaleDateFromEpochTime() method.");
            }
        }

        return formattedTimeStamp;
    }
    
    /**
     * This method converts an epoch time to the locale date format.
     * <code>DateFormat.LONG</code> specifies the date and time in January 12,
     * 1952 3:30:32pm format for <code>en_US</code> locale.
     * 
     * @param date
     *            Epoch time as a long value.  If the input is 0, returns
     *            an empty string.
     * @param locale
     *            A Locale object specifying the required locale value.
     * @return A String representation of the date in the specific locale
     *         format.
     */
    public String getLocaleDateFromEpochTime (long epochTime, Locale locale) {
        if (epochTime <= 0) {
            return "";
        }
        if (null == locale) {
            String errorMsg = String
                    .format(NULL_ARGUMENT_PASSED_STRING, new Object[] {
                            "getLocaleDateFromEpochTime", "Locale" });
            LOG.warn(errorMsg);
            return null;
        }

        Date date = new Date(epochTime);

        return DateFormat.getDateTimeInstance(DateFormat.LONG,
                                              DateFormat.LONG,
                                              locale).format(date);
    }

    /**
     * This method checks for epochTime that is not provided as milliseconds. If
     * it is clearly not in milliseconds, multiply by 1000 to convert from
     * presumably microseconds to milliseconds.
     * 
     * @param epochTime
     *            The long time value
     * @return
     */
    private long convertToMillis (double epochTime) {
        double time = epochTime;

        while (time > 0 && time < MILLIS_THRESHOLD) {
            time *= 1000;
        }

        return (long) time;
    }
}
