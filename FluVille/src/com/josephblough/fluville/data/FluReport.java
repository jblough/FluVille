package com.josephblough.fluville.data;

import java.util.ArrayList;
import java.util.List;

public class FluReport {

    public String title;
    public String subtitle;
    public String defaultColor;
    public String timePeriod;
    // How to store Legend and LegendLabel combinations???
    public List<TimePeriod> periods = new ArrayList<TimePeriod>();
}
