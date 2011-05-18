package com.josephblough.fluville;

import com.josephblough.fluville.ApplicationController;
import com.josephblough.fluville.adapters.PodcastFeedEntryAdapter;
import com.josephblough.fluville.data.FeedEntry;

import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

public class FluPodcasts extends FeedActivity implements OnItemSelectedListener, OnItemClickListener {
	private static final String TAG = "FluPodcasts";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Tell Android that the volume control buttons should set the
		//	media volume and not the ringer volume
		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		getListView().setOnItemSelectedListener(this);
		getListView().setOnItemClickListener(this);
		registerForContextMenu(getListView());
	}

	protected void loadFeed() {
		ApplicationController app = (ApplicationController)getApplicationContext();
		ListAdapter adapter = null;
		if (app != null && app.fluPodcastsFeed != null) {
			adapter = new PodcastFeedEntryAdapter(this, app.fluPodcastsFeed.items);
		}
		if (adapter != null)
			setListAdapter(adapter);
	}
	
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Log.d(TAG, "onItemClick");
		FeedEntry entry = ((PodcastFeedEntryAdapter)getListAdapter()).getItem(position);
		visitLink(entry);
	}

	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		Log.d(TAG, "onItemClick");
		FeedEntry entry = ((PodcastFeedEntryAdapter)getListAdapter()).getItem(position);
		visitLink(entry);
	}

	public void refreshList() {
		((PodcastFeedEntryAdapter)getListAdapter()).notifyDataSetChanged();
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Register to be notified the the podcast finishes playing
		ApplicationController app = (ApplicationController)getApplicationContext();
		app.activityToUpdateOnPlayCompletion = this;
	}

	@Override
	protected void onPause() {
		super.onPause();

		// Unregister to be notified the the podcast finishes playing
		ApplicationController app = (ApplicationController)getApplicationContext();
		app.activityToUpdateOnPlayCompletion = null;
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}
}
