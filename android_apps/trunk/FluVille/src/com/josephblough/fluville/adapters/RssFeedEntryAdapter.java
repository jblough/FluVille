package com.josephblough.fluville.adapters;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.josephblough.fluville.data.FeedEntry;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class RssFeedEntryAdapter extends ArrayAdapter<FeedEntry> {

    private final String TAG = "RssFeedEntryAdapter";

    SimpleDateFormat inputFormatter1 = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
    SimpleDateFormat inputFormatter2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    SimpleDateFormat outputFormatter = new SimpleDateFormat();
    Activity activity = null;

    public RssFeedEntryAdapter(Activity activity, List<FeedEntry> entries) {
    	super(activity, android.R.layout.simple_list_item_2, entries);
    	this.activity = activity;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
	View row = convertView;

	if (row == null) {
	    LayoutInflater inflater = activity.getLayoutInflater();
	    row = inflater.inflate(android.R.layout.simple_list_item_2, null);
	}
	FeedEntry entry = super.getItem(position);
	((TextView)row.findViewById(android.R.id.text1)).setText(entry.title);
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
		((TextView) row.findViewById(android.R.id.text2)).setText(outputFormatter.format(date));
	    }
	    else {
		((TextView) row.findViewById(android.R.id.text2)).setText(entry.date);
	    }
	}
	return row;
    }
}
