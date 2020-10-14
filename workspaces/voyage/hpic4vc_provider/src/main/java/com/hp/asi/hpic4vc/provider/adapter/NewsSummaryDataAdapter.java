package com.hp.asi.hpic4vc.provider.adapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.hp.asi.hpic4vc.provider.data.NewsItemData;
import com.hp.asi.hpic4vc.provider.data.NewsItemResult;
import com.hp.asi.hpic4vc.provider.locale.I18NProvider;
import com.hp.asi.hpic4vc.provider.model.NewsFeedModel;

public class NewsSummaryDataAdapter extends DataAdapter<NewsItemResult, NewsFeedModel> {
    private static final String SERVICE_NAME = "newsfeed/?top=10&";
    
    public NewsSummaryDataAdapter() {
        super(NewsItemResult.class);
    }

    @Override
    public NewsFeedModel formatData (NewsItemResult rawData) {
        NewsFeedModel newsFeed = new NewsFeedModel();
        if (rawData.hasError()) {
            newsFeed.errorMessage = rawData.getErrorMessage();
            log.debug("NewsItemResult had an error message.  " +
                    "Returning a NewsFeedModel with the error message");
            return newsFeed;
        }
        if (null == rawData.getResult() || rawData.getResult().length == 0) {
            String errorMessage = this.i18nProvider.getInternationalString
                    (locale, I18NProvider.News_NoData);
            newsFeed.informationMessage = errorMessage;
            if (null == rawData.getResult()) {
                log.debug("NewsItemResult.getResult() is null.  No news items to report.");
            } else {
                log.debug("NewsItemResult.getResult() has no records.  No news items to report.");
            }
            return newsFeed;
        }
        pouplateNewsFeedModel(newsFeed, rawData);
        return newsFeed;
    }

	@Override
    public String getServiceName () {
        return SERVICE_NAME;
    }
    
    @Override
    public NewsFeedModel getEmptyModel () {
        return new NewsFeedModel();
    }
    
    
    private void pouplateNewsFeedModel(NewsFeedModel model, final NewsItemResult data) {
        Collection<NewsItemData> newsItems = getSortedNewsItemsByDate(data.getResult());
        for (NewsItemData newsItem : newsItems) {
            String message         = getFormattedMessage(newsItem);
            String eventTimeStamp  = I18NProvider.getInstance().getLocaleDateFromEpochTime
                    (newsItem.getEventDate(), locale); 
            model.addNewsItem(newsItem.getStatus(),
                                 newsItem.getStatus(),
                                 message,
                                 eventTimeStamp);
        }
    }
    
    
    private List<NewsItemData> getSortedNewsItemsByDate(
			NewsItemData[] result) {
		List<NewsItemData> data = new ArrayList<NewsItemData>();
		data.addAll(Arrays.asList(result));
		Collections.sort(data);
		return data;
	}
    
    private String getFormattedMessage (NewsItemData newsItem) {
        if (newsItem.getPluginSource().equalsIgnoreCase("server") || 
                null == newsItem.getMessageArguments() ||
                newsItem.getMessageArguments().length < 2) {
            return newsItem.getMessage();
        }

        String message   = newsItem.getMessage();
        String formatter = I18NProvider.News_Tasks_Message;
        if (message.equalsIgnoreCase("task.queued")) {
            formatter = I18NProvider.News_Tasks_Queued;
        } else if (message.equalsIgnoreCase("task.error")) {
            formatter = I18NProvider.News_Tasks_Error;
        } else if (message.equalsIgnoreCase("task.success")) {
            formatter = I18NProvider.News_Tasks_Success;
        } else if (message.equalsIgnoreCase("task.running")) {
            formatter = I18NProvider.News_Tasks_Running;
        } else if (message.equalsIgnoreCase("task.jobstepmessage")) {
            formatter = I18NProvider.News_Tasks_JobStepMessage;
        } 

        return this.i18nProvider.getInternationalString
                (locale, 
                 formatter, 
                 (Object[])newsItem.getMessageArguments());
    } 

}
