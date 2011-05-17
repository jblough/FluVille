package com.josephblough.fluville.transport;

import java.io.IOException;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.josephblough.fluville.data.Feed;
import com.josephblough.fluville.data.FluReport;
import com.josephblough.fluville.data.SyndicatedFeed;
import com.josephblough.fluville.handlers.FluPodcastsFeedXmlHandler;
import com.josephblough.fluville.handlers.FluUpdatesFeedXmlHandler;
import com.josephblough.fluville.handlers.SyndicatedFeedXmlHandler;
import com.josephblough.fluville.handlers.WeeklyFluActivityXmlHandler;

import android.util.Log;

public class DataRetriever {
    private final static String TAG = "DataRetriever";

    private static final String FLU_VACCINATION_ESTIMATES_URL = "http://www.cdc.gov/flue/professionals/vaccination/reporti1011/resources/2010-11_Coverage.xls";
    private static final String WEEKLY_FLU_ACTIVITY_REPORT_URL = "http://www.cdc.gov/flu/weekly/flureport.xml";
    private static final String FLU_PAGES_AS_XML_URL = "http://t.cdc.gov/feed.aspx?tpc=26829&days=90";
    //private static final String FLU_PAGES_AS_JSON_URL = "http://t.cdc.gov/feed.aspx?tpc=26829&days=90&fmt=json";
    private static final String FLU_UPDATES_URL = "http://www2c.cdc.gov/podcasts/createrss.asp?t=r&c=20";
    private static final String FLU_PODCASTS_URL = "http://www2c.cdc.gov/podcasts/searchandcreaterss.asp?topic=flu";
    private static final String CDC_FEATURES_PAGES_AS_XML_URL = "http://t.cdc.gov/feed.aspx?tpc=26816&fromdate=1/1/2011";
    //private static final String CDC_FEATURES_PAGES_AS_JSON_URL = "http://t.cdc.gov/feed.aspx?tpc=26816&fromdate=1/1/2011&fmt=json";
    private static final String SYNDICATED_FEED_AS_XML_URL = "http://t.cdc.gov/feed.aspx?tpc=%d&days=90";
    
    
    public static void getFluVaccinationEstimates() {
	// Excel spreadsheet
	HttpClient client = new DefaultHttpClient();
	HttpGet get = new HttpGet(FLU_VACCINATION_ESTIMATES_URL);
	try {
	    HttpResponse response = client.execute(get);
	    Log.d(TAG, "Spreadsheet has " + response.getEntity().getContentLength() + " bytes");
	} catch (IOException e) {
	    Log.e(TAG, e.getMessage(), e);
	}
    }
    
    public static FluReport getFluActivityReport() {
	Log.d(TAG, "Flu activity report:");
	try {
	    HttpClient client = new DefaultHttpClient();
	    HttpGet httpMethod = new HttpGet(WEEKLY_FLU_ACTIVITY_REPORT_URL);
	    HttpResponse response = client.execute(httpMethod);
	    HttpEntity entity = response.getEntity();

	    SAXParserFactory parserFactory = SAXParserFactory.newInstance();
	    SAXParser parser = parserFactory.newSAXParser();
	    XMLReader reader = parser.getXMLReader();
	    WeeklyFluActivityXmlHandler handler = new WeeklyFluActivityXmlHandler();
	    reader.setContentHandler(handler);
	    reader.parse(new InputSource(entity.getContent()));
	    Log.d(TAG, "Report: " + handler.report.subtitle);
	    Log.d(TAG, "Report has " + handler.report.periods.size() + " time periods");
	    return handler.report;
	}
	catch (Exception e) {
	    Log.e(TAG, e.getMessage(), e);
	}
	return new FluReport();
    }
    
    public static SyndicatedFeed getFluPagesAsXml() {
	Log.d(TAG, "Flu pages:");
	return retrieveSyndicatedFeed(FLU_PAGES_AS_XML_URL);
    }
    
    public static Feed getFluUpdates() {
	Log.d(TAG, "Flu updates:");
	try {
	    HttpClient client = new DefaultHttpClient();
	    HttpGet httpMethod = new HttpGet(FLU_UPDATES_URL);
	    HttpResponse response = client.execute(httpMethod);
	    HttpEntity entity = response.getEntity();

	    SAXParserFactory parserFactory = SAXParserFactory.newInstance();
	    SAXParser parser = parserFactory.newSAXParser();
	    XMLReader reader = parser.getXMLReader();
	    FluUpdatesFeedXmlHandler handler = new FluUpdatesFeedXmlHandler();
	    reader.setContentHandler(handler);
	    reader.parse(new InputSource(entity.getContent()));
	    Log.d(TAG, "Report: " + handler.feed.title);
	    Log.d(TAG, "Report has " + handler.feed.items.size() + " items");
	    return handler.feed;
	}
	catch (Exception e) {
	    Log.e(TAG, e.getMessage(), e);
	}
	return new Feed();
    }
    
    public static Feed getFluPodcasts() {
	Log.d(TAG, "Flu podcasts:");
	try {
	    HttpClient client = new DefaultHttpClient();
	    HttpGet httpMethod = new HttpGet(FLU_PODCASTS_URL);
	    HttpResponse response = client.execute(httpMethod);
	    HttpEntity entity = response.getEntity();

	    SAXParserFactory parserFactory = SAXParserFactory.newInstance();
	    SAXParser parser = parserFactory.newSAXParser();
	    XMLReader reader = parser.getXMLReader();
	    FluPodcastsFeedXmlHandler handler = new FluPodcastsFeedXmlHandler();
	    reader.setContentHandler(handler);
	    reader.parse(new InputSource(entity.getContent()));
	    Log.d(TAG, "Report: " + handler.feed.title);
	    Log.d(TAG, "Report has " + handler.feed.items.size() + " items");
	    return handler.feed;
	}
	catch (Exception e) {
	    Log.e(TAG, e.getMessage(), e);
	}
	return new Feed();
    }
    
    public static SyndicatedFeed getCdcFeaturesPagesAsXml() {
	Log.d(TAG, "CDC Features pages:");
	return retrieveSyndicatedFeed(CDC_FEATURES_PAGES_AS_XML_URL);
    }

    public static SyndicatedFeed retrieveSyndicatedFeed(final int topic) {
	String url = SYNDICATED_FEED_AS_XML_URL.replace("%d", Integer.toString(topic));
	return retrieveSyndicatedFeed(url);
    }
    
    public static SyndicatedFeed retrieveSyndicatedFeed(final String url) {
	try {
	    HttpClient client = new DefaultHttpClient();
	    HttpGet httpMethod = new HttpGet(url);
	    HttpResponse response = client.execute(httpMethod);
	    HttpEntity entity = response.getEntity();

	    SAXParserFactory parserFactory = SAXParserFactory.newInstance();
	    SAXParser parser = parserFactory.newSAXParser();
	    XMLReader reader = parser.getXMLReader();
	    SyndicatedFeedXmlHandler handler = new SyndicatedFeedXmlHandler();
	    reader.setContentHandler(handler);
	    reader.parse(new InputSource(entity.getContent()));
	    Log.d(TAG, "Report: " + handler.feed.title);
	    Log.d(TAG, "Report has " + handler.feed.items.size() + " items");
	    return handler.feed;
	}
	catch (Exception e) {
	    Log.e(TAG, e.getMessage(), e);
	}
	return new SyndicatedFeed();
    }
}
