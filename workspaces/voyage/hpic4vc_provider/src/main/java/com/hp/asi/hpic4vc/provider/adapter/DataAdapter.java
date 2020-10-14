package com.hp.asi.hpic4vc.provider.adapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.codehaus.jackson.map.DeserializationConfig.Feature;
import org.codehaus.jackson.map.ObjectMapper;

import com.hp.asi.hpic4vc.provider.error.CommunicationException;
import com.hp.asi.hpic4vc.provider.error.DataMapException;
import com.hp.asi.hpic4vc.provider.error.InitializationException;
import com.hp.asi.hpic4vc.provider.error.NullDataException;
import com.hp.asi.hpic4vc.provider.locale.I18NProvider;
import com.hp.asi.hpic4vc.provider.model.BaseModel;

/**
 * 
 * @author thorntji
 * 
 * @param <T>
 *            The type of object returned from the web service call.
 * @param <S>
 *            The type of object to return to the hpic4vc_ui.
 */
public abstract class DataAdapter<T, S extends BaseModel> extends
        BaseAdapter<T, S> {

    private ObjectMapper objectMapper;
    private Class<T> tClass;

    public DataAdapter (Class<T> clazz) {
        this.objectMapper = new ObjectMapper();
        this.tClass       = clazz;
        objectMapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public S call () {
        S result = getEmptyModel();
        try {
            this.urlString = getWsUrl();
            log.info("Service Url is " + urlString);
        } catch (InitializationException e) {
            String errorMessage = this.i18nProvider
                    .getInternationalString(locale, I18NProvider.Error_CannotMakeUrl);
            result.errorMessage = errorMessage;
            log.error(errorMessage);
            log.info("Initialization exception for service " + this.getServiceName(), e);
            return result;
        }

        try {
            String jsonData = httpGet(urlString);
            log.info("Data for " + this.getServiceName() + " is " + jsonData);
            if (null == jsonData || jsonData.equals("")) {
                String errorMessage = this.i18nProvider
                        .getInternationalString(locale, I18NProvider.Error_NoWsResponse);
                result.errorMessage = errorMessage;
                log.error(errorMessage);
                log.info("Null JSON data for " + this.getServiceName());
                return result;
            }

            T wsDataObject = getWsObject(jsonData);
            if (null == wsDataObject) {
                String errorMessage = this.i18nProvider
                        .getInternationalString(locale, I18NProvider.Error_CannotMapJson);
                result.errorMessage = errorMessage;
                log.error(errorMessage);
                log.debug("Null web service object for " + this.getServiceName());
                return result;
            }
            result = formatData(wsDataObject);
        } catch (Exception e) {
            log.debug("Caught exception: " + e.getMessage(), e);
            result.errorMessage = e.getMessage();
        }
        log.info("Returning result from the provider " + result.toString());
        return result;
    }

    /**
     * Private method to make the web service call and recieve JSON data as a
     * string.
     * 
     * @param urlString
     *            The url of the web service to call.
     * 
     * @return The JSON data received from the web service call.
     * 
     * @throws NullDataException
     *             Thrown if the response is null.
     * 
     * @throws CommunicationException
     *             Thrown if there are issues communicating to the back end.
     */
    protected String httpGet (final String urlString) throws NullDataException,
            CommunicationException {
        try {
            HttpGet httpget = new HttpGet(urlString);
            HttpResponse response = httpClient.execute(httpget);
            checkHttpResponseForErrors(response);

            HttpEntity entity = response.getEntity();
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    entity.getContent()));
            StringBuffer sb = new StringBuffer();
            String line;
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
            in.close();
            String jsonString = sb.toString();
            if (isErrorPage(jsonString, urlString)) {
                log.error("Page for " + this.getServiceName()
                        + " is Page Not Found.  Returning a null JSON string.");
                return null;
            }
            return jsonString;
        } catch (IOException e) {
            String errorMessage = this.i18nProvider
                    .getInternationalString(locale,
                                            I18NProvider.Error_NoWsResponse);
            log.error("httpGet for " + this.getServiceName()
                    + " caught an IOException.  "
                    + "Throwing a CommunicationException.", e);
            throw new CommunicationException(errorMessage);
        } finally {
            httpClient.getConnectionManager().shutdown();
            this.httpClient = null;
        }
    }

    /**
     * Private helper method to map the JSON String data back to its original
     * object.
     * 
     * @param jsonData
     *            The String JSON data received from the web service call.
     * 
     * @return The original object form of the data from the web service call.
     * 
     * @throws DataMapException
     *             Thrown if there are issues mapping the data from a String
     *             back to its original object.
     */
    public T getWsObject (final String jsonData) throws DataMapException {
        try {
            T result = objectMapper.readValue(jsonData, tClass);
            log.info("JSON data mapped to object: " + result.toString());
            return result;
        } catch (Exception e) {
            log.error("JSON ERROR: error mapping JSON string to object for service "
                    + this.getServiceName()
                    + ":  "
                    + jsonData
                    + ".  Throwing a DataMapException.");
            log.debug(e.getMessage(), e);
            String message = this.i18nProvider
                    .getInternationalString(locale,
                                            I18NProvider.Error_CannotMapJson);
            throw new DataMapException(message);
        }
    }

    /**
     * Place to remove any HTML formatting that may occur in the web service
     * data.
     * 
     * @param jsonString
     *            The string returned from the web service.
     * @return The JSON string without HTML formatting.
     */
    protected String removeHtml (String jsonString) {
        String newString = null;

        if (null != jsonString) {
            newString = jsonString.replaceAll("<br>", "\n");
        }
        return newString;
    }
}
