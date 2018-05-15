package com.in2event.in2eventscan.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.in2event.in2eventscan.utils.Contents;
import com.in2event.in2eventscan.utils.MyPreference;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SyncService extends Service {
    public static boolean isRunning = false;
    private int taskCount = 0;

    public SyncService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final MyPreference myPref = new MyPreference(this);
        RequestQueue queue = Volley.newRequestQueue(this);

        for (final String barcode : myPref.getQueuedBarcodes()) {
            JSONObject params = new JSONObject();
            try {
                params.put("barcode", barcode);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            enterTask();
            JsonRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, Contents.API_ALL_BARCODES, params, new Response.Listener<JSONObject>(){
                @Override
                public void onResponse(JSONObject response) {
                    leaveTask();
                    Log.d("Kangtle", "SyncService submitted barcode " + barcode);
                    myPref.removeBarcodeFromQueue(barcode);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    leaveTask();
                    Log.d("Kangtle", "SyncService failed to barcode " + barcode);
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("X-ACCESS-TOKEN", myPref.getAccessToken());
                    return headers;
                }
            };

            queue.add(jsonRequest);
        }

        return START_STICKY;
    }
    private void enterTask(){
        taskCount ++;
        isRunning = true;
    }

    private void leaveTask(){
        taskCount --;
        if (taskCount == 0){
            isRunning = false;
        }
    }
}
