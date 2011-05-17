package com.josephblough.fluville;

import com.josephblough.fluville.adapters.RssFeedEntryAdapter;
import com.josephblough.fluville.data.FeedEntry;
import com.josephblough.fluville.data.SyndicatedFeed;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

public class FeedActivity extends ListActivity implements OnItemSelectedListener, OnItemClickListener {

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
		
		getListView().setOnItemSelectedListener(this);
		getListView().setOnItemClickListener(this);
	}
	
	protected void loadFeed() {
		ApplicationController app = (ApplicationController)getApplicationContext();
		ListAdapter adapter = null;
		switch (feed) {
		case FLU_PAGES:
			if (app != null && app.syndicatedFeeds.get(SyndicatedFeed.FLU_PAGES_TOPIC_ID) != null) {
				adapter = new RssFeedEntryAdapter(this, app.syndicatedFeeds.get(SyndicatedFeed.FLU_PAGES_TOPIC_ID).items);
			}
			break;
		case FLU_UPDATES:
			if (app != null && app.fluUpdatesFeed != null) {
				adapter = new RssFeedEntryAdapter(this, app.fluUpdatesFeed.items);
			}
			break;
		case CDC_FEATURE_PAGES:
			if (app != null && app.syndicatedFeeds.get(SyndicatedFeed.CDC_PAGES_TOPIC_ID) != null) {
				adapter = new RssFeedEntryAdapter(this, app.syndicatedFeeds.get(SyndicatedFeed.CDC_PAGES_TOPIC_ID).items);
			}
			break;
		};
		
		if (adapter != null)
			setListAdapter(adapter);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		//SyndEntry entry = ((RssFeedEntryAdapter)getListAdapter()).getItem(position);
		FeedEntry entry = ((RssFeedEntryAdapter)getListAdapter()).getItem(position);
		visitLink(entry);
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		FeedEntry entry = ((RssFeedEntryAdapter)getListAdapter()).getItem(position);
		visitLink(entry);
	}
	
	protected void visitLink(final FeedEntry entry) {
		Log.d(TAG, "Selected: " + entry.link);
		final Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(entry.link));
		startActivity(intent);
	}
}
