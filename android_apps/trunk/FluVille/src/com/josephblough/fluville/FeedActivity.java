package com.josephblough.fluville;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndEntry;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;

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
	
	private void loadFeed() {
		ApplicationController app = (ApplicationController)getApplicationContext();
		RssFeedEntryAdapter adapter = null;
		switch (feed) {
		case FLU_PAGES:
			if (app != null && app.fluPagesFeed != null) {
				adapter = new RssFeedEntryAdapter(app.fluPagesFeed);
			}
			break;
		case FLU_UPDATES:
			if (app != null && app.fluUpdatesFeed != null) {
				adapter = new RssFeedEntryAdapter(app.fluUpdatesFeed);
			}
			break;
		case FLU_PODCASTS:
			if (app != null && app.fluPodcastsFeed != null) {
				adapter = new RssFeedEntryAdapter(app.fluPodcastsFeed);
			}
			break;
		case CDC_FEATURE_PAGES:
			if (app != null && app.cdcFeaturePagesFeed != null) {
				adapter = new RssFeedEntryAdapter(app.cdcFeaturePagesFeed);
			}
			break;
		};
		
		if (adapter != null)
			setListAdapter(adapter);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		SyndEntry entry = ((RssFeedEntryAdapter)getListAdapter()).getItem(position);
		visitLink(entry);
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		SyndEntry entry = ((RssFeedEntryAdapter)getListAdapter()).getItem(position);
		visitLink(entry);
	}
	
	private void visitLink(final SyndEntry entry) {
		Log.d(TAG, "Selected: " + entry.getLink());
		final Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(entry.getLink()));
		startActivity(intent);
	}
	
	
	
	private class RssFeedEntryAdapter extends ArrayAdapter<SyndEntry> {
		SimpleDateFormat formatter = new SimpleDateFormat();
		RssFeedEntryAdapter(List<SyndEntry> entries) {
			super(FeedActivity.this, android.R.layout.simple_list_item_2, entries);
		}
		
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			
			if (row == null) {
				LayoutInflater inflater = getLayoutInflater();
				row = inflater.inflate(android.R.layout.simple_list_item_2, null);
			}
			SyndEntry entry = super.getItem(position);
			((TextView)row.findViewById(android.R.id.text1)).setText(entry.getTitle());
			Date date = (entry.getUpdatedDate() == null) ? entry.getPublishedDate() : entry.getUpdatedDate();
			synchronized (this) {
				((TextView) row.findViewById(android.R.id.text2)).setText(formatter.format(date));
			}
			return row;
		}
	}
}
