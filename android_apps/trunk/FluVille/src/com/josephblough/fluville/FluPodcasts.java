package com.josephblough.fluville;

import com.josephblough.fluville.ApplicationController;
import com.josephblough.fluville.R;
import com.josephblough.fluville.adapters.PodcastFeedEntryAdapter;
import com.josephblough.fluville.adapters.RssFeedEntryAdapter;
import com.josephblough.fluville.data.FeedEntry;
import com.josephblough.fluville.data.PodcastFeedEntry;
import com.josephblough.fluville.data.SyndicatedFeed;
//import com.josephblough.fluville.services.FluPodcastsFeedDownloaderService;
//import com.josephblough.fluville.services.PodcastDownloaderService;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

public class FluPodcasts extends FeedActivity implements OnItemSelectedListener, OnItemClickListener {
	private static final String TAG = "FluPodcasts";

	private ProgressDialog progress = null;
	private final String ERROR_MSG = "There was an error downloading the Flu podcasts feed";

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
