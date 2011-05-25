package com.josephblough.fluville.data;

import java.util.ArrayList;
import java.util.List;

public class Feed {

    public static final int FEED_FLU_UPDATES = -1;
    public static final int FEED_FLU_PODCASTS = -2;
    
    public String title;
    public String description;
    public String link;
    public String logoUrl;
    public List<String> categories = new ArrayList<String>();
    public List<FeedEntry> items = new ArrayList<FeedEntry>();
}
