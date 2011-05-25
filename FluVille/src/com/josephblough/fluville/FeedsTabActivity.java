package com.josephblough.fluville;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;

public class FeedsTabActivity extends TabActivity {

	private static final String TAG = "FeedsActivity";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.d(TAG, "onCreate");
		// Tell Android that the volume control buttons should set the
		//	media volume and not the ringer volume
		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

		Resources res = getResources();
		TabHost tabHost = getTabHost();
		TabHost.TabSpec spec;
		Intent intent;

		intent = new Intent().setClass(this, FeedActivity.class);
		intent.putExtra(FeedActivity.FEED_EXTRA, FeedActivity.FLU_PAGES);
		spec = tabHost.newTabSpec("pages").setIndicator("Flu Pages", res.getDrawable(R.drawable.flu_pages_tab)).setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, FeedActivity.class);
		intent.putExtra(FeedActivity.FEED_EXTRA, FeedActivity.FLU_UPDATES);
		spec = tabHost.newTabSpec("updates").setIndicator("Flu Updates", res.getDrawable(R.drawable.flu_updates_tab)).setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, FluPodcasts.class);
		spec = tabHost.newTabSpec("podcasts").setIndicator("Flu Podcasts", res.getDrawable(R.drawable.flu_podcasts_tab)).setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, FeedActivity.class);
		intent.putExtra(FeedActivity.FEED_EXTRA, FeedActivity.CDC_FEATURE_PAGES);
		spec = tabHost.newTabSpec("features").setIndicator("CDC Feature Pages", res.getDrawable(R.drawable.cdc_feature_pages_tab)).setContent(intent);
		tabHost.addTab(spec);

		tabHost.setCurrentTab(0);
	}
}
