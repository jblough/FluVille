package com.josephblough.fluville;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;

public class FeedsTabActivity extends TabActivity {

	private static final String TAG = "FeedsActivity";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.d(TAG, "onCreate");

		Resources res = getResources();
		TabHost tabHost = getTabHost();
		TabHost.TabSpec spec;
		Intent intent;

		intent = new Intent().setClass(this, FeedActivity.class);
		intent.putExtra(FeedActivity.FEED_EXTRA, FeedActivity.FLU_PAGES);
		//spec = tabHost.newTabSpec("words").setIndicator("Words", res.getDrawable(R.drawable.index_cards)).setContent(intent);
		spec = tabHost.newTabSpec("pages").setIndicator("Pages").setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, FeedActivity.class);
		intent.putExtra(FeedActivity.FEED_EXTRA, FeedActivity.FLU_UPDATES);
		//spec = tabHost.newTabSpec("wordlists").setIndicator("Word Lists", res.getDrawable(R.drawable.shoebox)).setContent(intent);
		spec = tabHost.newTabSpec("updates").setIndicator("Updates").setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, FeedActivity.class);
		intent.putExtra(FeedActivity.FEED_EXTRA, FeedActivity.FLU_PODCASTS);
		//spec = tabHost.newTabSpec("download").setIndicator("Download", res.getDrawable(R.drawable.inbox)).setContent(intent);
		spec = tabHost.newTabSpec("podcasts").setIndicator("Podcasts").setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, FeedActivity.class);
		intent.putExtra(FeedActivity.FEED_EXTRA, FeedActivity.CDC_FEATURE_PAGES);
		//spec = tabHost.newTabSpec("download").setIndicator("Download", res.getDrawable(R.drawable.inbox)).setContent(intent);
		spec = tabHost.newTabSpec("features").setIndicator("CDC\nFeature Pages").setContent(intent);
		tabHost.addTab(spec);

		tabHost.setCurrentTab(0);
	}
}
