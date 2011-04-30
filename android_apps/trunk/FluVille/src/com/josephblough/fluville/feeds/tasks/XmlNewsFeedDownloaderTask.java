package com.josephblough.fluville.feeds.tasks;

import java.util.List;

import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndEntry;
import com.josephblough.fluville.feeds.DataRetriever;

import android.os.AsyncTask;

public class XmlNewsFeedDownloaderTask extends AsyncTask<String, Void, List<SyndEntry>> {

	@Override
	protected List<SyndEntry> doInBackground(String... urls) {
		return DataRetriever.retrieveXmlRssFeed(urls[0]);
	}

	protected void onPostExecute(List<SyndEntry> result) {
	}
}
