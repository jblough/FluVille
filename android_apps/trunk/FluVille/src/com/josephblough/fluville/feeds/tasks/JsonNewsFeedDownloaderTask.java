package com.josephblough.fluville.feeds.tasks;

import org.json.JSONArray;

import com.josephblough.fluville.feeds.DataRetriever;
import android.os.AsyncTask;

public class JsonNewsFeedDownloaderTask extends AsyncTask<String, Void, JSONArray> {

	@Override
	protected JSONArray doInBackground(String... urls) {
		return DataRetriever.retrieveJsonRssFeed(urls[0]);
	}

	protected void onPostExecute(JSONArray result) {
	}
}
