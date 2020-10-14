package com.hp.asi.hpic4vc.provider.adapter;

import com.hp.asi.hpic4vc.provider.data.NewsItemData;
import com.hp.asi.hpic4vc.provider.data.NewsItemResult;
import com.hp.asi.hpic4vc.provider.locale.I18NProvider;
import com.hp.asi.hpic4vc.provider.model.TableModel;
import com.hp.asi.hpic4vc.provider.model.TableModel.TableRow;

public class NewsDetailDataAdapter extends NewsTasksDetailsBaseAdapter<NewsItemResult> {
    private static final String SERVICE_NAME = "newsfeed/";
    
    public NewsDetailDataAdapter () {
        super(NewsItemResult.class);
    }

    @Override
    public String getServiceName () {
        return SERVICE_NAME;
    }

    @Override
    public String getErrorMsg (NewsItemResult rawData) {
        return rawData.getErrorMessage();
    }

    @Override
    public boolean isResultEmpty (NewsItemResult rawData) {
        if (null == rawData.getResult()) {
            return true;
        }
        return false;
    }
    
    @Override
    public void setRows (TableModel table, NewsItemResult rawData) {
        for (NewsItemData newsItem : rawData.getResult()) {
            TableRow row = new TableRow();
            if (isCluster) {
                populateClusterData(row, newsItem);
            }
            populateNormalData(row, newsItem);
            table.addRow(row);
        }
    }
    
    @Override
    public void addNormalColumns (TableModel table) {
        String status = this.i18nProvider
                .getInternationalString(locale, I18NProvider.News_Status);
        String message = this.i18nProvider
                .getInternationalString(locale, I18NProvider.News_Message);
        String source = this.i18nProvider
                .getInternationalString(locale, I18NProvider.News_Source);
        String dateTime = this.i18nProvider
                .getInternationalString(locale, I18NProvider.News_DateTime);
        
        table.addColumn(status,   null, 	"150", null, true, false, true);
        table.addColumn(message,  null,     "500", null, true, false, false);
        table.addColumn(source,   null,     "200", null, true, false, false);
        table.addColumn(dateTime, null,     "200", null, true, false, false);
    }
    
    @Override
    public void addClusterColumns (TableModel table) {
        String host = this.i18nProvider
                .getInternationalString(locale, I18NProvider.Tasks_Target);

        table.addColumn(host, null, "150", null, true, false, false);
    }


    private void populateNormalData (TableRow row, final NewsItemData newsItem) {
        String id = null;
        String eventTimeMillis = Double.toString(newsItem.getEventDate());
        String eventTimeStamp = I18NProvider.getInstance()
                .getLocaleDateFromEpochTime(newsItem.getEventDate(), locale);

        String iconClass = getIconClass(newsItem.getStatus());
        String message   = getMessage(newsItem);
        
        row.addCell(id, newsItem.getStatus(),      newsItem.getStatus(),      iconClass);
        row.addCell(id, message,                   message,                   null);
        row.addCell(id, newsItem.getEventSource(), newsItem.getEventSource(), null);
        row.addCell(id, eventTimeMillis,           eventTimeStamp,  		  null);
    }

    private void populateClusterData(TableRow row, final NewsItemData newsItem) {
        String id = null;
        row.addCell(id, newsItem.getName(), newsItem.getName(), null);
    }
    
    private String getMessage (final NewsItemData newsItem) {
        String formattedMessage;
        if (newsItem.getPluginSource().equalsIgnoreCase("server")) {
            formattedMessage = newsItem.getMessage();
        } else {
            formattedMessage = getFormattedMessage(newsItem.getMessage(), 
                                                   newsItem.getMessageArguments());
        }
        return formattedMessage;
    }

}
