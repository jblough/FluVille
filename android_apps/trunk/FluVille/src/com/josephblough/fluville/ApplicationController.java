package com.josephblough.fluville;

import java.util.List;

import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndEntry;
import com.josephblough.fluville.feeds.reports.FluReport;

import android.app.Application;

public class ApplicationController extends Application {

	public FluReport fluReport;
	public List<SyndEntry> fluPagesFeed;
	public List<SyndEntry> fluUpdatesFeed;
	public List<SyndEntry> fluPodcastsFeed;
	public List<SyndEntry> cdcFeaturePagesFeed;
	
	public void updateLastGoodFeed(int feed) {
		
	}
	
	public void updateLastGoodFluReport() {
		
	}
}
