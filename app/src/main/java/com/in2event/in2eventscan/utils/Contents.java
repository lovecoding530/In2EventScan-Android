package com.in2event.in2eventscan.utils;

import android.content.Context;
import android.os.Environment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Kangtle_R on 1/26/2018.
 */

public class Contents {

//    public static String API_ROOT = "http://workers.hetsysteem.com/api/1"; //domo
    public static String API_ROOT = "http://api.in2event.com/api/1"; //live
    public static String API_AUTHORIZE = API_ROOT + "/authorize";
    public static String API_ALL_BARCODES= API_ROOT + "/barcodes";

    public static String DEFAULT_DATE_FORMAT = "dd/MM/yyyy";

    public static class JsonEvent {
        public static String TOKEN = "token";
        public static String EVENT_NAME = "event_name";
        public static String EVENT_LOGO = "event_logo";
        public static String EVENT_DATES = "event_dates";
        public static String EVENT_CITY = "event_city";
        public static String STATUS = "status";
    }

    public static JSONArray cachedBarcodes = null;
    public static ArrayList<String> scannedBarcodes = new ArrayList<>();
}
