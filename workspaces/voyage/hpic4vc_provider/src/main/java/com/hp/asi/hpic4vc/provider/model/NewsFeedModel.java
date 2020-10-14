package com.hp.asi.hpic4vc.provider.model;

import java.util.ArrayList;
import java.util.List;

public class NewsFeedModel extends BaseModel {

    public List<NewsItem> newsItems;
    
    public NewsFeedModel() {
        super();
        this.newsItems = new ArrayList<NewsItem>();
    }
    
    public NewsFeedModel(final List<NewsItem> newsItems) {
        super();
        this.newsItems = newsItems;
    }
    
    public void addNewsItem(final String rawStatus, 
                       final String formattedStatus, 
                       final String formattedMessage, 
                       final String formattedTimeStamp) {
        
        if (null == this.newsItems) {
            this.newsItems = new ArrayList<NewsItem>();
        }
        
        this.newsItems.add(new NewsItem(rawStatus, 
                                        formattedStatus, 
                                        formattedMessage, 
                                        formattedTimeStamp));
    }

	@Override
	public String toString() {
		return "NewsFeedModel [newsItems=" + newsItems + ", errorMessage="
				+ errorMessage + ", informationMessage=" + informationMessage
				+ "]";
	}

}
