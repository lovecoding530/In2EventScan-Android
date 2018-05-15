package com.in2event.in2eventscan.utils;

import android.util.Log;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Kangtle_R on 1/26/2018.
 */

public class JsonHelper {
    public static void saveJsonObject(JSONObject jsonObject, String path){
        FileHelper.writeStringToFile(jsonObject.toString(), path);
    }

    public static JSONObject readJsonFromFile(String path){
        String jsonString = FileHelper.readStringFromFile(path);
        if (jsonString.isEmpty()) return null;
        try {
            return new JSONObject(jsonString);
        } catch (Exception e) {
            Log.e("JsonHelper", "Failed to parse the json", e);
            return null;
        }
    }

    public static JSONObject readJsonFromAsset(String fileName){
        String jsonString = FileHelper.readStringFromAsset(fileName);
        try {
            return new JSONObject(jsonString);
        } catch (Exception e) {
            Log.e("JsonHelper", "Failed to parse the json", e);
            return null;
        }
    }

    public static int indexOf(JSONArray array, String field, Object value){
        int index = -1;
        for (int i = 0; i < array.length(); i++){
            try {
                JSONObject jsonObject = array.getJSONObject(i);
                if (jsonObject.get(field).equals(value)){
                    index = i;
                    break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return index;
    }

    public static JSONArray remove(JSONArray array, int index){
        JSONArray newArray = new JSONArray();
        int len = array.length();
        for (int i=0;i<len;i++)
        {
            //Excluding the item at position
            if (i != index)
            {
                try {
                    newArray.put(array.get(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        return newArray;
    }
}
