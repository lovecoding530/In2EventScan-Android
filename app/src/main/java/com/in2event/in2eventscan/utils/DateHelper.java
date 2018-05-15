package com.in2event.in2eventscan.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Kangtle_R on 1/25/2018.
 */

public class DateHelper {
    public static SimpleDateFormat dateFormat = new SimpleDateFormat(Contents.DEFAULT_DATE_FORMAT, Locale.US);
    public static Date stringToDate(String dateStr){
        try {
            return dateFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }

    public static Date timestampToDate(long timeStamp){
        return new Date(timeStamp);
    }

    public static String dateToString(Date date){
        return dateFormat.format(date);
    }

    public static String dateToString(Date date, String format){
        if (date == null) return "";
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.US);
        return dateFormat.format(date);
    }

    public static String datetimeToString(Date date){
        if (date == null) return "";
        String format = Contents.DEFAULT_DATE_FORMAT + " h:m:s";
        return datetimeToString(date, format);
    }

    public static String datetimeToString(Date date, String format){
        if (date == null) return "";
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.US);
        return dateFormat.format(date);
    }
}
