package com.josephblough.fluville.feeds;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndEntry;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndFeed;
import com.google.code.rome.android.repackaged.com.sun.syndication.io.SyndFeedInput;
import com.google.code.rome.android.repackaged.com.sun.syndication.io.XmlReader;
import com.josephblough.fluville.feeds.reports.FluReport;

import android.util.Log;

@SuppressWarnings("unchecked")
public class DataRetriever {
	private final static String TAG = "DataRetriever";

	private static final String FLU_VACCINATION_ESTIMATES_URL = "http://www.cdc.gov/flue/professionals/vaccination/reporti1011/resources/2010-11_Coverage.xls";
	private static final String WEEKLY_FLU_ACTIVITY_REPORT_URL = "http://www.cdc.gov/flu/weekly/flureport.xml";
	private static final String FLU_PAGES_AS_XML_URL = "http://t.cdc.gov/feed.aspx?tpc=26829&days=90";
	private static final String FLU_PAGES_AS_JSON_URL = "http://t.cdc.gov/feed.aspx?tpc=26829&days=90&fmt=json";
	private static final String FLU_UPDATES_URL = "http://www2c.cdc.gov/podcasts/createrss.asp?t=r&c=20";
	private static final String FLU_PODCASTS_URL = "http://www2c.cdc.gov/podcasts/searchandcreaterss.asp?topic=flue";
	private static final String CDC_FEATURES_PAGES_AS_XML_URL = "http://t.cdc.gov/feed.aspx?tpc=26816&fromdate=1/1/2011";
	private static final String CDC_FEATURES_PAGES_AS_JSON_URL = "http://t.cdc.gov/feed.aspx?tpc=26816&fromdate=1/1/2011&fmt=json";


	public void getFluVaccinationEstimates() {
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

	public List<SyndEntry> getFluPagesAsXml() {
		Log.d(TAG, "Flu pages:");
		return retrieveXmlRssFeed(FLU_PAGES_AS_XML_URL);
	}

	public JSONArray getFluPagesAsJson() {
		Log.d(TAG, "Flu pages:");
		return retrieveJsonRssFeed(FLU_PAGES_AS_JSON_URL);
	}

	public List<SyndEntry> getFluUpdates() {
		Log.d(TAG, "Flu updates:");
		return retrieveXmlRssFeed(FLU_UPDATES_URL);
	}

	public List<SyndEntry> getFluPodcasts() {
		Log.d(TAG, "Flu podcasts:");
		return retrieveXmlRssFeed(FLU_PODCASTS_URL);
	}

	public List<SyndEntry> getCdcFeaturesPagesAsXml() {
		Log.d(TAG, "CDC Features pages:");
		return retrieveXmlRssFeed(CDC_FEATURES_PAGES_AS_XML_URL);
	}

	public JSONArray getCdcFeaturesPagesAsJson() {
		Log.d(TAG, "CDC Features pages:");
		return retrieveJsonRssFeed(CDC_FEATURES_PAGES_AS_JSON_URL);
	}

	public static List<SyndEntry> retrieveXmlRssFeed(final String url) {
		try {
			URL feedUrl = new URL(url);
			SyndFeedInput input = new SyndFeedInput();
			SyndFeed feed = input.build(new XmlReader(feedUrl));
			/*Log.d(TAG, "SyndFeed Array has " + feed.getEntries().size() + " elements");
	    List<SyndEntry> entries = (List<SyndEntry>)feed.getEntries();
	    for (SyndEntry entry : entries) {
		Log.d(TAG, entry.getTitle());
	    }*/
			return feed.getEntries();
		}
		catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		}
		return new ArrayList<SyndEntry>();
	}

	public static JSONArray retrieveJsonRssFeed(final String url) {
		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(url);
		try {
			ResponseHandler<String> handler = new BasicResponseHandler();
			String response = client.execute(get, handler);
			return new JSONArray(response);
		} catch (JSONException e) {
			Log.e(TAG, e.getMessage(), e);
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
		}
		return new JSONArray();
	}
}
