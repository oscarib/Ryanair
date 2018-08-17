package com.ryanair.oif.routes.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class RyanairCommons {

    public static String getDateISOAsTxt(String year, String month, String day, String time) {

        String ISODay = day;
        String ISOMonth = month;

        if (day.length()==1) {
            ISODay = "0"+day;
        }

        if (month.length()==1) {
            ISOMonth = "0"+month;
        }

        return year + "-" + ISOMonth + "-" + ISODay + "T" + time;
    }

    public static Date parse2Date(String ISODate) {

        TimeZone tz = TimeZone.getDefault();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        df.setTimeZone(tz);

        try {
            return df.parse(ISODate);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Date parse2Date(String year, String month, String day, String time) {
        return parse2Date(getDateISOAsTxt(year, month, day, time));
    }

    public static String getYear(String ISODate){
        //2018-08-20T20:30
        int indexOfSeparator = ISODate.indexOf("-");
        return ISODate.substring(0, indexOfSeparator);
    }

    public static String getMonth(String ISODate){
        //2018-08-20T20:30
        int firstIndex = ISODate.indexOf("-");
        int secondIndex = ISODate.indexOf("-", ++firstIndex);
        return ISODate.substring(firstIndex, secondIndex);
    }

    public static String getDay(String ISODate){
        //2018-08-20T20:30
        int indexOfSeparator = ISODate.indexOf("T");
        return ISODate.substring(indexOfSeparator-2, indexOfSeparator);
    }

    public static String getTime(String ISODate){
        //2018-08-20T20:30
        int indexOfSeparator = ISODate.indexOf("T");
        return ISODate.substring(++indexOfSeparator);
    }
}
