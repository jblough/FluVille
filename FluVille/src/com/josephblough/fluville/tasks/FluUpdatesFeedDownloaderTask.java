package com.josephblough.fluville.tasks;

import com.josephblough.fluville.ApplicationController;
import com.josephblough.fluville.FluVilleCityActivity;
import com.josephblough.fluville.transport.DataRetriever;
import com.josephblough.fluville.data.Feed;
import com.josephblough.fluville.data.SyndicatedFeed;

import android.os.AsyncTask;
import android.util.Log;

public class FluUpdatesFeedDownloaderTask extends AsyncTask<Void, Void, Feed> {

	private static final String TAG = "FluUpdatesFeedDownloaderTask";
	
	private ApplicationController app;
	private FluVilleCityActivity activity;
	
	public FluUpdatesFeedDownloaderTask(ApplicationController app, FluVilleCityActivity activity) {
	    this.app = app;
	    this.activity = activity;
	}
	
	@Override
	protected Feed doInBackground(Void... param) {
	    return DataRetriever.getFluUpdates();
	}

	protected void onPostExecute(Feed result) {
		app.fluUpdatesFeed = result;
		activity.updateCdcFeedsReadyFlag();
	}
}
