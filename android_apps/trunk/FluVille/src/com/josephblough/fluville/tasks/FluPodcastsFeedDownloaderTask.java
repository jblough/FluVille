package com.josephblough.fluville.tasks;

import com.josephblough.fluville.ApplicationController;
import com.josephblough.fluville.FluVilleCityActivity;
import com.josephblough.fluville.transport.DataRetriever;
import com.josephblough.fluville.data.Feed;

import android.os.AsyncTask;
import android.util.Log;

public class FluPodcastsFeedDownloaderTask extends AsyncTask<Void, Void, Feed> {

	private static final String TAG = "FluPodcastsFeedDownloaderTask";
	
	private ApplicationController app;
	private FluVilleCityActivity activity;
	
	public FluPodcastsFeedDownloaderTask(ApplicationController app, FluVilleCityActivity activity) {
	    this.app = app;
	    this.activity = activity;
	}
	
	@Override
	protected Feed doInBackground(Void... param) {
	    return DataRetriever.getFluPodcasts();
	}

	protected void onPostExecute(Feed result) {
	    app.fluPodcastsFeed = result;
		activity.updateCdcFeedsReadyFlag();
	}
}
