package com.josephblough.fluville.handlers;

import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.josephblough.fluville.data.SyndicatedFeed;
import com.josephblough.fluville.data.SyndicatedFeedEntry;
import com.josephblough.fluville.data.SyndicatedFeedEntryTopic;

import android.util.Log;

public class SyndicatedFeedXmlHandler extends DefaultHandler {

    private static final String TAG = "SyndicatedFeedXmlHandler";
    
    private static final String FEED_TAG = "feed";
    private static final String TITLE_TAG = "title";
    private static final String SUBTITLE_TAG = "subtitle";
    private static final String ID_TAG = "id";
    private static final String UPDATED_TAG = "updated";
    private static final String LINK_TAG = "link";
    private static final String ENTRY_TAG = "entry";
    private static final String TOPIC_TAG = "Topic";
    private static final String SUMMARY_TAG = "summary";
    
    private static final String TOPIC_ID_ATTRIBUTE = "TopicId";
    private static final String TOPIC_NAME_ATTRIBUTE = "TopicName";
    private static final String LINK_HREF_ATTRIBUTE = "href";
    
    /*
    private static final String DESCRIPTION_TAG = "description";
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
    */
    /*
    private static final int FEED = 0;
    private static final int FEED_TITLE = 1;
    private static final int FEED_DESCRIPTION = 2;
    private static final int FEED_LINK = 3;
    private static final int FEED_ATOM_LINK = 4;
    private static final int FEED_IMAGE = 5;
    private static final int FEED_IMAGE_TITLE = 6;
    private static final int FEED_IMAGE_URL = 7;
    private static final int FEED_IMAGE_LINK = 8;
    private static final int FEED_IMAGE_WIDTH = 9;
    private static final int FEED_IMAGE_HEIGHT = 10;
    private static final int FEED_LANGUAGE = 11;
    private static final int FEED_WEBMASTER = 12;
    private static final int FEED_CATEGORY = 13;
    private static final int ITEM = 14;
    private static final int ITEM_TITLE = 15;
    private static final int ITEM_DESCRIPTION = 16;
    private static final int ITEM_LINK = 17;
    private static final int ITEM_GUID = 18;
    private static final int ITEM_PUBDATE = 19;
    private static final int UNKNOWN_ELEMENT = 20;
    */
    private static final int FEED = 0;
    private static final int FEED_TITLE = 1;
    private static final int ENTRY_TITLE = 2;
    private static final int SUBTITLE = 3;
    private static final int FEED_ID = 4;
    private static final int ENTRY_ID = 5;
    private static final int FEED_UPDATED = 6;
    private static final int ENTRY_UPDATED = 7;
    private static final int FEED_LINK = 8;
    private static final int ENTRY_LINK = 9;
    private static final int ENTRY = 10;
    private static final int TOPIC = 11;
    private static final int SUMMARY = 12;
    private static final int UNKNOWN_ELEMENT = 13;
    
    
    private Stack<Integer> tagStack;
    public SyndicatedFeed feed;
    private SyndicatedFeedEntry currentEntry;
    private StringBuffer characterBuffer;
    
    public void startDocument() throws SAXException {
	tagStack = new Stack<Integer>();
	feed = new SyndicatedFeed();
	characterBuffer = new StringBuffer();
    }
    
    public void startElement(String uri, String localName, String qName, 
	    Attributes attributes) throws SAXException {
	if (ENTRY_TAG.equals(localName)) {
	    tagStack.push(ENTRY);
	    currentEntry = new SyndicatedFeedEntry();
	}
	else if (TITLE_TAG.equals(localName)) {
	    switch (tagStack.peek()) {
	    case FEED:
		tagStack.push(FEED_TITLE);
		break;
	    case ENTRY:
		tagStack.push(ENTRY_TITLE);
		break;
	    default:
		tagStack.push(UNKNOWN_ELEMENT);
	    }
	}
	else if (SUBTITLE_TAG.equals(localName)) {
	    tagStack.push(SUBTITLE);
	}
	else if (ID_TAG.equals(localName)) {
	    switch (tagStack.peek()) {
	    case FEED:
		tagStack.push(FEED_ID);
		break;
	    case ENTRY:
		tagStack.push(ENTRY_ID);
		break;
	    default:
		tagStack.push(UNKNOWN_ELEMENT);
	    }
	}
	else if (UPDATED_TAG.equals(localName)) {
	    switch (tagStack.peek()) {
	    case FEED:
		tagStack.push(FEED_UPDATED);
		break;
	    case ENTRY:
		tagStack.push(ENTRY_UPDATED);
		break;
	    default:
		tagStack.push(UNKNOWN_ELEMENT);
	    }
	}
	else if (LINK_TAG.equals(localName)) {
	    switch (tagStack.peek()) {
	    case FEED:
		tagStack.push(FEED_LINK);
		feed.link = attributes.getValue(LINK_HREF_ATTRIBUTE);
		break;
	    case ENTRY:
		tagStack.push(ENTRY_LINK);
		currentEntry.link = attributes.getValue(LINK_HREF_ATTRIBUTE);
		break;
	    default:
		tagStack.push(UNKNOWN_ELEMENT);
	    }
	}
	else if (TOPIC_TAG.equals(localName)) {
	    tagStack.push(TOPIC);
	    SyndicatedFeedEntryTopic topic = new SyndicatedFeedEntryTopic();
	    try {
		topic.id = Integer.parseInt(attributes.getValue(TOPIC_ID_ATTRIBUTE));
		topic.name = attributes.getValue(TOPIC_NAME_ATTRIBUTE);
		currentEntry.topics.add(topic);
	    }
	    catch (NumberFormatException e) {
		Log.e(TAG, e.getMessage(), e);
	    }
	}
	else if (SUMMARY_TAG.equals(localName)) {
	    tagStack.push(SUMMARY);
	}
	else if (FEED_TAG.equals(localName)) {
	    tagStack.push(FEED);
	}
	else {
	    tagStack.push(UNKNOWN_ELEMENT);
	}
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
	tagStack.pop();
	
	if (ENTRY_TAG.equals(localName)) {
	    feed.items.add(currentEntry);
	    currentEntry = null;
	}
	else if (TITLE_TAG.equals(localName)) {
	    switch (tagStack.peek()) {
	    case FEED:
		feed.title = characterBuffer.toString();
		break;
	    case ENTRY:
		currentEntry.title = characterBuffer.toString();
		break;
	    default:
	    }
	}
	else if (SUBTITLE_TAG.equals(localName)) {
	    feed.subtitle = characterBuffer.toString();
	}
	else if (ID_TAG.equals(localName)) {
	    switch (tagStack.peek()) {
	    case FEED:
		feed.id = characterBuffer.toString();
		break;
	    case ENTRY:
		currentEntry.guid = characterBuffer.toString();
		break;
	    default:
	    }
	}
	else if (UPDATED_TAG.equals(localName)) {
	    switch (tagStack.peek()) {
	    case FEED:
		feed.date = characterBuffer.toString();
		break;
	    case ENTRY:
		currentEntry.date = characterBuffer.toString();
		break;
	    default:
	    }
	}
	else if (SUMMARY_TAG.equals(localName)) {
	    currentEntry.description = characterBuffer.toString();
	}
	
	// Empty out the character buffer
	characterBuffer = characterBuffer.delete(0, characterBuffer.length());
    }	
    
    public void characters(char[] ch, int start, int length) throws SAXException {
	switch (tagStack.peek()) {
	case FEED_TITLE:
	case ENTRY_TITLE:
	case SUBTITLE:
	case FEED_ID:
	case ENTRY_ID:
	case FEED_UPDATED:
	case ENTRY_UPDATED:
	case FEED_LINK:
	case ENTRY_LINK:
	case SUMMARY:
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
