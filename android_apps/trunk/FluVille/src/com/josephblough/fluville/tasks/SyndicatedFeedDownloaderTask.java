package com.josephblough.fluville.tasks;

import com.josephblough.fluville.ApplicationController;
import com.josephblough.fluville.FluVilleCityActivity;
import com.josephblough.fluville.transport.DataRetriever;
import com.josephblough.fluville.data.SyndicatedFeed;

import android.os.AsyncTask;
import android.util.Log;

public class SyndicatedFeedDownloaderTask extends AsyncTask<Void, Void, SyndicatedFeed> {

	private static final String TAG = "XmlNewsFeedDownloaderTask";
	
	private ApplicationController app;
	private FluVilleCityActivity activity;
	private int topic;
	
	public SyndicatedFeedDownloaderTask(ApplicationController app, FluVilleCityActivity activity, int topic) {
	    this.app = app;
	    this.activity = activity;
	    this.topic = topic;
	}
	
	@Override
	protected SyndicatedFeed doInBackground(Void... param) {
	    Log.d(TAG, "Retrieving topic " + this.topic);
	    return DataRetriever.retrieveSyndicatedFeed(this.topic);
	}

	protected void onPostExecute(SyndicatedFeed result) {
	    app.syndicatedFeeds.put(this.topic, result);
		activity.updateCdcFeedsReadyFlag();
	}
}
