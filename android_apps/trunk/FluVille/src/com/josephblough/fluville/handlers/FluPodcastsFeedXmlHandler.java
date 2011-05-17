package com.josephblough.fluville.handlers;

import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.josephblough.fluville.data.Feed;
import com.josephblough.fluville.data.PodcastFeedEntry;


/*
 * <?xml version="1.0" encoding="UTF-8" ?>
 * <rss xmlns:itunes="http://www.itunes.com/dtds/podcast-1.0.dtd" version="2.0">
 * 	<channel>
 * 		<title>FLU Podcasts from CDC</title>
 * 		<description>A customized podcast series from the US Centers for Disease Control and Prevention (CDC) for all podcasts related to the health topic  FLU </description>
 * 		<link>http://www2c.cdc.gov/podcasts/</link>
 * 		<image>
 * 			<title>CDC Logo</title>
 * 			<url>http://www2c.cdc.gov/podcasts/images/CDCLogo.jpg</url>
 * 			<link>http://www2c.cdc.gov/podcasts/</link>
 * 			<width>100</width>
 * 			<height>75</height>
 * 		</image>
 * 		<language>en-us</language>
 * 		<webMaster>imtech@cdc.gov (imtech)</webMaster>
 * 		<category>Health</category>
 * 		<category>Public Health</category>
 * 		<itunes:image href="http://www2c.cdc.gov/podcasts/images/CDCLogo.jpg" />
 * 		<itunes:subtitle></itunes:subtitle>
 * 		<itunes:explicit>No</itunes:explicit>
 * 		<itunes:author>CDC, US Centers for Disease Control and Prevention</itunes:author>
 * 		<itunes:owner>
 * 			<itunes:name>Centers for Disease Control and Prevention (CDC)</itunes:name>
 * 			<itunes:email>podcasts@cdc.gov</itunes:email>
 * 		</itunes:owner>
 * 		<itunes:category text="Science &amp; Medicine"/>
 * 		<item>
 * 			<title>Put Your Hands Together</title>
 * 			<description>In this podcast, learn how to help stop the spread of infection and stay healthy. It's easy when you 'Put Your Hands Together.'</description>
 * 			<link>http://www2c.cdc.gov/podcasts/download.asp?af=h&amp;f=10109</link>
 * 			<guid>http://www2c.cdc.gov/podcasts/download.asp?af=h&amp;f=10109</guid>
 * 			<pubDate>Thu, 24 Mar 2011 16:30:00 EST</pubDate>
 * 			<enclosure url="http://www2c.cdc.gov/podcasts/downloader/download.mp3?af=a&amp;f=10109" length="3829439" type="audio/mpeg"></enclosure>
 * 			<itunes:duration>00:03:59</itunes:duration>
 * 			<itunes:subtitle></itunes:subtitle>
 * 			<itunes:author>CDC, US Centers for Disease Control and Prevention</itunes:author>
 * 			<itunes:explicit>No</itunes:explicit>
 * 			<itunes:summary>In this podcast, learn how to help stop the spread of infection and stay healthy. It's easy when you 'Put Your Hands Together.'</itunes:summary>
 * 		</item>
 * 
 *		...
 *	</channel>
 * </rss> 
 */

public class FluPodcastsFeedXmlHandler extends DefaultHandler {

    private static final String TAG = "FluPodcastsFeedXmlHandler";

    private static final String RSS_TAG = "rss";
    private static final String CHANNEL_TAG = "channel";
    private static final String TITLE_TAG = "title";
    private static final String DESCRIPTION_TAG = "description";
    private static final String LINK_TAG = "link";
    private static final String FEED_IMAGE_TAG = "image";
    private static final String FEED_IMAGE_URL_TAG = "url";
    private static final String FEED_IMAGE_WIDTH_TAG = "width";
    private static final String FEED_IMAGE_HEIGHT_TAG = "height";
    private static final String FEED_LANGUAGE_TAG = "language";
    private static final String FEED_WEBMASTER_TAG = "webMaster";
    private static final String FEED_CATEGORY_TAG = "category";
    private static final String ITEM_TAG = "item";
    private static final String ITEM_GUID_TAG = "guid";
    private static final String ITEM_PUBDATE_TAG = "pubDate";
    private static final String ITEM_ENCLOSURE_TAG = "enclosure";
    private static final String ITEM_DURATION_TAG = "duration";
    
    private static final String ITEM_LENGTH_ATTRIBUTE = "length";
    private static final String ITEM_TYPE_ATTRIBUTE = "type";
    private static final String ITEM_MP3_URL_ATTRIBUTE = "url";
    
    private static final int RSS = 0;
    private static final int CHANNEL = 1;
    private static final int FEED_TITLE = 2;
    private static final int FEED_DESCRIPTION = 3;
    private static final int FEED_LINK = 4;
    private static final int FEED_ATOM_LINK = 5;
    private static final int FEED_IMAGE = 6;
    private static final int FEED_IMAGE_TITLE = 7;
    private static final int FEED_IMAGE_URL = 8;
    private static final int FEED_IMAGE_LINK = 9;
    private static final int FEED_IMAGE_WIDTH = 10;
    private static final int FEED_IMAGE_HEIGHT = 11;
    private static final int FEED_LANGUAGE = 12;
    private static final int FEED_WEBMASTER = 13;
    private static final int FEED_CATEGORY = 14;
    private static final int ITEM = 15;
    private static final int ITEM_TITLE = 16;
    private static final int ITEM_DESCRIPTION = 17;
    private static final int ITEM_LINK = 18;
    private static final int ITEM_GUID = 19;
    private static final int ITEM_PUBDATE = 20;
    private static final int UNKNOWN_ELEMENT = 21;
    private static final int ITEM_DURATION = 22;

    private Stack<Integer> tagStack;
    public Feed feed;
    private PodcastFeedEntry currentEntry;
    private StringBuffer characterBuffer;

    public void startDocument() throws SAXException {
	tagStack = new Stack<Integer>();
	feed = new Feed();
	characterBuffer = new StringBuffer();
    }

    public void startElement(String uri, String localName, String qName, 
	    Attributes attributes) throws SAXException {
	if (ITEM_TAG.equals(localName)) {
	    tagStack.push(ITEM);
	    currentEntry = new PodcastFeedEntry();
	}
	else if (TITLE_TAG.equals(localName)) {
	    switch (tagStack.peek()) {
	    case CHANNEL:
		tagStack.push(FEED_TITLE);
		break;
	    case ITEM:
		tagStack.push(ITEM_TITLE);
		break;
	    case FEED_IMAGE:
		tagStack.push(FEED_IMAGE_TITLE);
		break;
	    default:
		tagStack.push(UNKNOWN_ELEMENT);
	    }
	}
	else if (DESCRIPTION_TAG.equals(localName)) {
	    switch (tagStack.peek()) {
	    case CHANNEL:
		tagStack.push(FEED_DESCRIPTION);
		break;
	    case ITEM:
		tagStack.push(ITEM_DESCRIPTION);
		break;
	    default:
		tagStack.push(UNKNOWN_ELEMENT);
	    }
	}
	else if (LINK_TAG.equals(localName)) {
	    switch (tagStack.peek()) {
	    case CHANNEL:
		tagStack.push(FEED_LINK);
		break;
	    case ITEM:
		tagStack.push(ITEM_LINK);
		break;
	    case FEED_IMAGE:
		tagStack.push(FEED_IMAGE_LINK);
		break;
	    default:
		tagStack.push(UNKNOWN_ELEMENT);
	    }
	}
	else if (ITEM_GUID_TAG.equals(localName)) {
	    tagStack.push(ITEM_GUID);
	}
	else if (ITEM_PUBDATE_TAG.equals(localName)) {
	    tagStack.push(ITEM_PUBDATE);
	}
	else if (FEED_CATEGORY_TAG.equals(localName)) {
	    tagStack.push(FEED_CATEGORY);
	}
	else if (CHANNEL_TAG.equals(localName)) {
	    tagStack.push(CHANNEL);
	}
	else if (ITEM_ENCLOSURE_TAG.equals(localName)) {
	    try {
		currentEntry.length = Integer.valueOf(attributes.getValue(ITEM_LENGTH_ATTRIBUTE));
		currentEntry.mp3url = attributes.getValue(ITEM_MP3_URL_ATTRIBUTE);
	    }
	    catch (Exception e) {
		currentEntry.length = 0;
	    }
	    currentEntry.type = attributes.getValue(ITEM_TYPE_ATTRIBUTE);
	    tagStack.push(UNKNOWN_ELEMENT);
	}
	else if (ITEM_DURATION_TAG.equals(localName)) {
	    tagStack.push(ITEM_DURATION);
	}	
	else {
	    tagStack.push(UNKNOWN_ELEMENT);
	}
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
	tagStack.pop();

	if (ITEM_TAG.equals(localName)) {
	    feed.items.add(currentEntry);
	    currentEntry = null;
	}
	else if (TITLE_TAG.equals(localName)) {
	    switch (tagStack.peek()) {
	    case CHANNEL:
		feed.title = characterBuffer.toString();
		break;
	    case ITEM:
		currentEntry.title = characterBuffer.toString();
		break;
	    case FEED_IMAGE:
		break;
	    default:
	    }
	}
	else if (DESCRIPTION_TAG.equals(localName)) {
	    switch (tagStack.peek()) {
	    case CHANNEL:
		feed.description = characterBuffer.toString();
		break;
	    case ITEM:
		currentEntry.description = characterBuffer.toString();
		break;
	    default:
	    }
	}
	else if (LINK_TAG.equals(localName)) {
	    switch (tagStack.peek()) {
	    case CHANNEL:
		feed.link = characterBuffer.toString();
		break;
	    case ITEM:
		currentEntry.link = characterBuffer.toString();
		break;
	    case FEED_IMAGE:
		feed.logoUrl = characterBuffer.toString();
		break;
	    default:
	    }
	}
	else if (ITEM_GUID_TAG.equals(localName)) {
	    currentEntry.guid = characterBuffer.toString();
	}
	else if (ITEM_PUBDATE_TAG.equals(localName)) {
	    currentEntry.date = characterBuffer.toString();
	}
	else if (ITEM_DURATION_TAG.equals(localName)) {
	    currentEntry.duration = characterBuffer.toString();
	}
	else if (FEED_CATEGORY_TAG.equals(localName)) {
	    feed.categories.add(characterBuffer.toString());
	}
	else if (CHANNEL_TAG.equals(localName)) {
	}

	// Empty out the character buffer
	characterBuffer = characterBuffer.delete(0, characterBuffer.length());
    }	

    public void characters(char[] ch, int start, int length) throws SAXException {
	switch (tagStack.peek()) {
	case FEED_TITLE:
	case ITEM_TITLE:
	case FEED_DESCRIPTION:
	case ITEM_DESCRIPTION:
	case FEED_LINK:
	case ITEM_LINK:
	case FEED_IMAGE_LINK:
	case ITEM_GUID:
	case ITEM_PUBDATE:
	case FEED_CATEGORY:
	case ITEM_DURATION:
	    characterBuffer.append(xmlDecode(new String(ch, start, length)));
	    break;
	}
    }

    public void endDocument() throws SAXException {
    }

    private static String xmlDecode(String str) {
	return str.replaceAll("&amp;", "&").replaceAll("&apos;", "'")
	.replaceAll("&lt;", "<").replaceAll("&gt;", ">")
	.replaceAll("&quot;", "\"");
    }

}
