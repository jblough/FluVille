package com.josephblough.fluville.adapters;

import java.util.List;

import com.josephblough.fluville.data.SyndicatedFeedEntryTopic;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SyndicatedFeedTopicAdapter extends
	ArrayAdapter<SyndicatedFeedEntryTopic> {

    private Activity activity;
    public SyndicatedFeedTopicAdapter(Activity activity, List<SyndicatedFeedEntryTopic> objects) {
	super(activity, android.R.layout.simple_list_item_1, objects);
	this.activity = activity;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
	View row = convertView;

	if (row == null) {
	    LayoutInflater inflater = activity.getLayoutInflater();
	    row = inflater.inflate(android.R.layout.simple_list_item_1, null);
	}
	SyndicatedFeedEntryTopic topic = super.getItem(position);
	((TextView)row.findViewById(android.R.id.text1)).setText(topic.name);
	
	return row;
    }
}
