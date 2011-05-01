package com.josephblough.fluville;

import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndEntry;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

public class FeedActivity extends ListActivity {

	private static final String TAG = "FeedActivity";
	
	public static final String FEED_EXTRA = "feed";
	
	public static final int FLU_PAGES = 0;
	public static final int FLU_UPDATES = 1;
	public static final int FLU_PODCASTS = 2;
	public static final int CDC_FEATURE_PAGES = 3;

	private int feed;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.d(TAG, "onCreate");
		
		feed = getIntent().getIntExtra(FEED_EXTRA, FLU_PAGES);
		loadFeed();
	}
	
	private void loadFeed() {
		ApplicationController app = (ApplicationController)getApplicationContext();
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
		switch (feed) {
		case FLU_PAGES:
			if (app != null && app.fluPagesFeed != null) {
			    for (SyndEntry entry : app.fluPagesFeed) {
			    	adapter.add(entry.getTitle());
			    }
			}
			break;
		case FLU_UPDATES:
			if (app != null && app.fluUpdatesFeed != null) {
				for (SyndEntry entry : app.fluUpdatesFeed) {
					adapter.add(entry.getTitle());
				}
			}
			break;
		case FLU_PODCASTS:
			if (app != null && app.fluPodcastsFeed != null) {
				for (SyndEntry entry : app.fluPodcastsFeed) {
					adapter.add(entry.getTitle());
				}
			}
			break;
		case CDC_FEATURE_PAGES:
			if (app != null && app.cdcFeaturePagesFeed != null) {
				for (SyndEntry entry : app.cdcFeaturePagesFeed) {
					adapter.add(entry.getTitle());
				}
			}
			break;
		};
		
		if (adapter != null)
			setListAdapter(adapter);
	}
}
