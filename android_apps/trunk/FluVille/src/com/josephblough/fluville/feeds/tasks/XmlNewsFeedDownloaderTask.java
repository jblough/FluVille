package com.josephblough.fluville.feeds.tasks;

import java.util.ArrayList;
import java.util.List;

import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndEntry;
import com.josephblough.fluville.ApplicationController;
import com.josephblough.fluville.FeedActivity;
import com.josephblough.fluville.FluVilleCityActivity;
import com.josephblough.fluville.feeds.DataRetriever;

import android.os.AsyncTask;
import android.util.Log;

public class XmlNewsFeedDownloaderTask extends AsyncTask<Void, Void, List<SyndEntry>> {

	private static final String TAG = "XmlNewsFeedDownloaderTask";
	
	private FluVilleCityActivity activity;
	private ApplicationController app;
	private int feed;
	
	public XmlNewsFeedDownloaderTask(FluVilleCityActivity activity, ApplicationController app, int feed) {
		this.activity = activity;
		this.app = app;
		this.feed = feed;
	}
	
	@Override
	protected List<SyndEntry> doInBackground(Void... param) {
		switch (this.feed) {
		case FeedActivity.FLU_PAGES:
			Log.d(TAG, "Downloading flu pages feed");
			return DataRetriever.retrieveXmlRssFeed(DataRetriever.FLU_PAGES_AS_XML_URL);
		case FeedActivity.FLU_UPDATES:
			Log.d(TAG, "Downloading flu updates feed");
			return DataRetriever.retrieveXmlRssFeed(DataRetriever.FLU_UPDATES_URL);
		case FeedActivity.FLU_PODCASTS:
			Log.d(TAG, "Downloading flu podcasts feed");
			return DataRetriever.retrieveXmlRssFeed(DataRetriever.FLU_PODCASTS_URL);
		case FeedActivity.CDC_FEATURE_PAGES:
			Log.d(TAG, "Downloading CDC feature pages feed");
			return DataRetriever.retrieveXmlRssFeed(DataRetriever.CDC_FEATURES_PAGES_AS_XML_URL);
		};
		return new ArrayList<SyndEntry>();
	}

	protected void onPostExecute(List<SyndEntry> result) {
		switch (this.feed) {
		case FeedActivity.FLU_PAGES:
			app.fluPagesFeed = result;
			Log.d(TAG, "Downloaded flu pages feed");
			break;
		case FeedActivity.FLU_UPDATES:
			app.fluUpdatesFeed = result;
			Log.d(TAG, "Downloaded flu updates feed");
			break;
		case FeedActivity.FLU_PODCASTS:
			app.fluPodcastsFeed = result;
			Log.d(TAG, "Downloaded flu podcasts feed");
			break;
		case FeedActivity.CDC_FEATURE_PAGES:
			app.cdcFeaturePagesFeed = result;
			Log.d(TAG, "Downloaded CDC feature pages feed");
			break;
		};
		
		if (app.fluPagesFeed != null &&
				app.fluUpdatesFeed != null &&
				app.fluPodcastsFeed != null &&
				app.cdcFeaturePagesFeed != null) {
			activity.notifyFeedsReady();
		}
	}
}
