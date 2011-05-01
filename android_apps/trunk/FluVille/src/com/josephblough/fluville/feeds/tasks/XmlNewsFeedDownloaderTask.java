package com.josephblough.fluville.feeds.tasks;

import java.util.ArrayList;
import java.util.List;

import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndEntry;
import com.josephblough.fluville.ApplicationController;
import com.josephblough.fluville.FeedActivity;
import com.josephblough.fluville.feeds.DataRetriever;

import android.os.AsyncTask;

public class XmlNewsFeedDownloaderTask extends AsyncTask<Void, Void, List<SyndEntry>> {

	private ApplicationController app;
	private int feed;
	
	public XmlNewsFeedDownloaderTask(ApplicationController app, int feed) {
		this.app = app;
		this.feed = feed;
	}
	
	@Override
	protected List<SyndEntry> doInBackground(Void... param) {
		switch (this.feed) {
		case FeedActivity.FLU_PAGES:
			return DataRetriever.retrieveXmlRssFeed(DataRetriever.FLU_PAGES_AS_XML_URL);
		case FeedActivity.FLU_UPDATES:
			return DataRetriever.retrieveXmlRssFeed(DataRetriever.FLU_UPDATES_URL);
		case FeedActivity.FLU_PODCASTS:
			return DataRetriever.retrieveXmlRssFeed(DataRetriever.FLU_PODCASTS_URL);
		case FeedActivity.CDC_FEATURE_PAGES:
			return DataRetriever.retrieveXmlRssFeed(DataRetriever.CDC_FEATURES_PAGES_AS_XML_URL);
		};
		return new ArrayList<SyndEntry>();
	}

	protected void onPostExecute(List<SyndEntry> result) {
		switch (this.feed) {
		case FeedActivity.FLU_PAGES:
			app.fluPagesFeed = result;
			break;
		case FeedActivity.FLU_UPDATES:
			app.fluUpdatesFeed = result;
			break;
		case FeedActivity.FLU_PODCASTS:
			app.fluPodcastsFeed = result;
			break;
		case FeedActivity.CDC_FEATURE_PAGES:
			app.cdcFeaturePagesFeed = result;
			break;
		};
	}
}
