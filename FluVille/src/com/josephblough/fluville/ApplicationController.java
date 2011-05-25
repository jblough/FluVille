package com.josephblough.fluville;

import java.util.HashMap;
import java.util.Map;

import com.josephblough.fluville.data.Feed;
import com.josephblough.fluville.data.PodcastFeedEntry;

import android.app.Application;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.util.Log;
import android.widget.Toast;

public class ApplicationController extends Application implements OnCompletionListener, OnPreparedListener, OnBufferingUpdateListener {

	private final static String TAG = "ApplicationController";

	public Feed fluUpdatesFeed;
	public Feed fluPodcastsFeed;
	public Map<Integer, Feed> syndicatedFeeds = new HashMap<Integer, Feed>();

	public Integer currentlyPlayingPodcast = null;
	public FluPodcasts activityToUpdateOnPlayCompletion;
	private MediaPlayer player = null;
	private boolean playerIsPrepared = false;

	public void onCreate() {
		super.onCreate();

		//Do Application initialization over here
		Log.d(TAG, "onCreate");
		player = new MediaPlayer();
		player.setOnCompletionListener(this);
		player.setOnPreparedListener(this);
		player.setOnBufferingUpdateListener(this);
	}

	@Override
	public void onTerminate () {
		if (player != null) {
			if (player.isPlaying()) {
				player.stop();
			}
			player.release();
		}
	}

	public void playPodcast(final int position) {
		if (fluPodcastsFeed != null && fluPodcastsFeed.items != null && fluPodcastsFeed.items.size() > 0) {
			try {
				currentlyPlayingPodcast = position;
				PodcastFeedEntry entry = (PodcastFeedEntry)fluPodcastsFeed.items.get(position);
				//Log.d(TAG, "Downloading " + entry.mp3url);
				if (player.isPlaying()) {
					player.stop();
				}
				player.reset();
				playerIsPrepared = false;
				player.setDataSource(entry.mp3url);
				player.prepareAsync();
			}
			catch (Exception e) {
				Log.e(TAG, e.getMessage(), e);
			}
		}
	}

	public void stopPodcast() {
		currentlyPlayingPodcast = null;
		if (player.isPlaying()) {
			player.stop();
		}
	}

	public void onPrepared(MediaPlayer player) {
		playerIsPrepared = true;
		if (currentlyPlayingPodcast != null) {
			player.start();
		}
	}

	public void onCompletion(MediaPlayer player) {
		if (playerIsPrepared && player.getDuration() > 0) {
			currentlyPlayingPodcast = null;

			if (activityToUpdateOnPlayCompletion != null) {
				activityToUpdateOnPlayCompletion.refreshList();
			}
		}
	}

	public PodcastFeedEntry getCurrentlyPlayingPodcast() {
		if (currentlyPlayingPodcast == null)
			return null;

		return (PodcastFeedEntry)fluPodcastsFeed.items.get(currentlyPlayingPodcast);
	}

	public void onBufferingUpdate(MediaPlayer player, int percent) {
		Toast.makeText(this, "Buffering complete: " + percent + "%", Toast.LENGTH_SHORT);
	}
}
