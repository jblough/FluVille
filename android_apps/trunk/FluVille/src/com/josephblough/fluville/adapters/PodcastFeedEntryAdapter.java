package com.josephblough.fluville.adapters;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.josephblough.fluville.ApplicationController;
import com.josephblough.fluville.FluPodcasts;
import com.josephblough.fluville.R;
import com.josephblough.fluville.data.FeedEntry;
import com.josephblough.fluville.data.PodcastFeedEntry;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class PodcastFeedEntryAdapter extends ArrayAdapter<FeedEntry> {

	private final String TAG = "RssFeedEntryAdapter";

	SimpleDateFormat inputFormatter1 = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
	SimpleDateFormat inputFormatter2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	SimpleDateFormat outputFormatter = new SimpleDateFormat();
	FluPodcasts activity = null;
	final Bitmap playImage;
	final Bitmap stopImage;

	public PodcastFeedEntryAdapter(FluPodcasts activity, List<FeedEntry> entries) {
		super(activity, R.layout.podcast_row, entries);
		this.activity = activity;
		this.playImage = BitmapFactory.decodeResource(activity.getResources(), R.drawable.ic_media_play);
		this.stopImage = BitmapFactory.decodeResource(activity.getResources(), R.drawable.ic_media_stop);
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		View row = convertView;

		if (row == null) {
			LayoutInflater inflater = activity.getLayoutInflater();
			row = inflater.inflate(R.layout.podcast_row, null);
		}
		final PodcastFeedEntry entry = (PodcastFeedEntry)super.getItem(position);
		((TextView)row.findViewById(R.id.podcast_title)).setText(entry.title);
		synchronized (this) {
			Date date = null;
			try {
				date = inputFormatter1.parse(entry.date);
			}
			catch (Exception e1) {
				try {
					date = inputFormatter2.parse(entry.date);
				}
				catch (Exception e2) {
					Log.e(TAG, "Feed date in unrecognized format");
				}
			}

			if (date != null) {
				((TextView) row.findViewById(R.id.podcast_updated)).setText(outputFormatter.format(date));
			}
			else {
				((TextView) row.findViewById(R.id.podcast_updated)).setText(entry.date);
			}

			((TextView) row.findViewById(R.id.podcast_duration)).setText(entry.duration);

			final ImageView control = (ImageView) row.findViewById(R.id.podcast_control);
			final ApplicationController app = (ApplicationController)activity.getApplicationContext();
			PodcastFeedEntry playingPodcast = app.getCurrentlyPlayingPodcast();
			if (playingPodcast != null && playingPodcast.mp3url.equals(entry.mp3url)) {
				control.setImageBitmap(this.stopImage);
			}
			else {
				control.setImageBitmap(this.playImage);
			}

			control.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					Log.d(TAG, "Podcast control tapped for " + position);
					PodcastFeedEntry playingPodcast = app.getCurrentlyPlayingPodcast();
					if (playingPodcast != null && playingPodcast.mp3url.equals(entry.mp3url)) {
						// This podcast is currently being played, stop it
						Log.d(TAG, "Changing image back to play image");
						control.setImageBitmap(playImage);
						new Thread(new Runnable() {

							public void run() {
								app.stopPodcast();
							}
						}).start();
					}
					else {
						// This podcast is NOT being played, play it
						control.setImageBitmap(stopImage);
						//activity.playPodcast(position);
						new Thread(new Runnable() {

							public void run() {
								app.playPodcast(position);
							}
						}).start();
						Toast toast = Toast.makeText(activity, "Downloading podcast", Toast.LENGTH_LONG);
						toast.setGravity(Gravity.BOTTOM, 0, 0);
						toast.show();
					}
					activity.refreshList();
				}
			});
		}
		return row;
	}
}
