package com.josephblough.fluville.handlers;

import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.josephblough.fluville.data.FluReport;
import com.josephblough.fluville.data.State;
import com.josephblough.fluville.data.TimePeriod;


import android.util.Log;

/*
 *<flureport 
 *	title="" 
 *	subtitle="Week Ending April 09, 2011- Week 14" 
 *	defaultColor="FEE391" 
 *	timePeriod="Week" 
 *	Legend1="No Report" 
 *	LegendLabel1="No Report" 
 *	Legend2="No Activity" 
 *	LegendLabel2="No Activity" 
 *	Legend3="Sporadic" 
 *	LegendLabel3="Sporadic" 
 *	Legend4="EBE236" 
 *	LegendLabel4="Local" 
 *	Legend5="F0BB37" 
 *	LegendLabel5="Regional" 
 *	Legend6="B39874" 
 *	LegendLabel6="Widespread">
 *		<timeperiod 
 *			number="40" 
 *			year="2010" 
 *			subtitle="Week Ending October 09, 2010- Week 40">
 *				<state>
 *					<abbrev>ME</abbrev>
 *					<color>No Activity</color>
 *					<label>No Activity</label>
 *				</state>
 *				<state>
 *					<abbrev>NH</abbrev>
 *					<color>Sporadic</color>
 *					<label>Sporadic</label>
 *				</state>
 *
 *	-- with multiple <timeperiod> elements which contain multiple <state> elements
 */

public class WeeklyFluActivityXmlHandler extends DefaultHandler {

    private static final String TAG = "WeeklyFluActivityXmlHandler";
    
    private static final String FLU_REPORT_TAG = "flureport";
    private static final String TIME_PERIOD_TAG = "timeperiod";
    private static final String STATE_TAG = "state";
    private static final String STATE_ABBREVIATION_TAG = "abbrev";
    private static final String COLOR_TAG = "color";
    private static final String LABEL_TAG = "label";
    
    private static final int FLU_REPORT = 0;
    private static final int TIME_PERIOD = 1;
    private static final int STATE = 2;
    private static final int STATE_ABBREVIATION = 3;
    private static final int COLOR = 4;
    private static final int LABEL = 5;
    
    private Stack<Integer> tagStack;
    public FluReport report;
    private TimePeriod currentTimePeriod;
    private State currentState;
    private StringBuffer characterBuffer;
    
    public void startDocument() throws SAXException {
	tagStack = new Stack<Integer>();
	report = new FluReport();
	characterBuffer = new StringBuffer();
    }
    
    public void startElement(String uri, String localName, String qName, 
	    Attributes attributes) throws SAXException {
	if (FLU_REPORT_TAG.equals(localName)) {
	    tagStack.push(FLU_REPORT);
	    report.title = attributes.getValue("title");
	    report.subtitle = attributes.getValue("subtitle");
	    report.defaultColor = attributes.getValue("defaultColor");
	    report.timePeriod = attributes.getValue("timePeriod");
	}
	else if (TIME_PERIOD_TAG.equals(localName)) {
	    tagStack.push(TIME_PERIOD);
	    currentTimePeriod = new TimePeriod();
	    currentTimePeriod.subtitle = attributes.getValue("subtitle");
	    try {
		currentTimePeriod.number = Integer.parseInt(attributes.getValue("number"));
	    }
	    catch (NumberFormatException e) {
		Log.e(TAG, attributes.getValue("number") + " is not a number");
		currentTimePeriod.number = 0;
	    }
	    try {
		currentTimePeriod.year = Integer.parseInt(attributes.getValue("year"));
	    }
	    catch (NumberFormatException e) {
		Log.e(TAG, attributes.getValue("year") + " is not a number");
		currentTimePeriod.year = 0;
	    }
	}
	else if (STATE_TAG.equals(localName)) {
	    tagStack.push(STATE);
	    currentState = new State();
	}
	else if (STATE_ABBREVIATION_TAG.equals(localName)) {
	    tagStack.push(STATE_ABBREVIATION);
	}
	else if (COLOR_TAG.equals(localName)) {
	    tagStack.push(COLOR);
	}
	else if (LABEL_TAG.equals(localName)) {
	    tagStack.push(LABEL);
	}
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
	tagStack.pop();
	
	if (FLU_REPORT_TAG.equals(localName)) {
	}
	else if (TIME_PERIOD_TAG.equals(localName)) {
	    report.periods.add(currentTimePeriod);
	    currentTimePeriod = null;
	}
	else if (STATE_TAG.equals(localName)) {
	    currentTimePeriod.states.add(currentState);
	    currentState = null;
	}
	else if (STATE_ABBREVIATION_TAG.equals(localName)) {
	    currentState.abbreviation = characterBuffer.toString();
	}
	else if (COLOR_TAG.equals(localName)) {
	    currentState.color = characterBuffer.toString();
	}
	else if (LABEL_TAG.equals(localName)) {
	    currentState.label = characterBuffer.toString();
	}
	
	// Empty out the character buffer
	characterBuffer = characterBuffer.delete(0, characterBuffer.length());
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
	switch (tagStack.peek()) {
	case STATE_ABBREVIATION:
	case COLOR:
	case LABEL:
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
