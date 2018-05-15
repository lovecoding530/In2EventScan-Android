package com.in2event.in2eventscan.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by Kangtle_R on 12/30/2017.
 */

public class MyPreference {
    private SharedPreferences configSP;
    private SharedPreferences.Editor configEditor;

    private Context context;

    public MyPreference(Context context){
        this.context = context;
        this.configSP = PreferenceManager.getDefaultSharedPreferences(context);
        this.configEditor = configSP.edit();
    }

    public String getAccessToken(){
        return configSP.getString(Contents.JsonEvent.TOKEN, null);
    }

    public void setAccessToken(String accessToken){
        configEditor.putString(Contents.JsonEvent.TOKEN, accessToken).commit();
    }

    public String getEventName(){
        return configSP.getString(Contents.JsonEvent.EVENT_NAME, null);
    }

    public void setEventName(String eventName){
        configEditor.putString(Contents.JsonEvent.EVENT_NAME, eventName).commit();
    }

    public String getEventLogo(){
        return configSP.getString(Contents.JsonEvent.EVENT_LOGO, null);
    }

    public void setEventLogo(String eventLogo){
        configEditor.putString(Contents.JsonEvent.EVENT_LOGO, eventLogo).commit();
    }

    public String getEventDates(){
        return configSP.getString(Contents.JsonEvent.EVENT_DATES, null);
    }

    public void setEventDates(String eventDates){
        configEditor.putString(Contents.JsonEvent.EVENT_DATES, eventDates).commit();
    }

    public String getEventCity(){
        return configSP.getString(Contents.JsonEvent.EVENT_CITY, null);
    }

    public void setEventCity(String eventCity){
        configEditor.putString(Contents.JsonEvent.EVENT_CITY, eventCity).commit();
    }

    public void addBarcodeToQueue(String barcode){
        Set<String> queuedBarcodes = getQueuedBarcodes();
        queuedBarcodes.add(barcode);
        configEditor.putStringSet("queued_barcodes", queuedBarcodes).commit();
    }

    public void removeBarcodeFromQueue(String barcode){
        Set<String> queuedBarcodes = getQueuedBarcodes();
        queuedBarcodes.remove(barcode);
        configEditor.putStringSet("queued_barcodes", queuedBarcodes).commit();
    }

    public Set<String> getQueuedBarcodes(){
        return configSP.getStringSet("queued_barcodes", new HashSet<String>());
    }
}
