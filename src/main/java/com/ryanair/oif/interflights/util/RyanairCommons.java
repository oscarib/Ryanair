package com.ryanair.oif.interflights.util;

public class RyanairCommons {

    public static String getDateISO(String year, String month, String day, String time) {
        return year + "-" + month + "-" + day + "T" + time;
    }
}
