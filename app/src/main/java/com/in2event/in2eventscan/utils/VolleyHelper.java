package com.in2event.in2eventscan.utils;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Kangtle_R on 1/26/2018.
 */

public class VolleyHelper {
    private Context context;
    private RequestQueue requestQueue;
    private Callback callback;
    private int count = 0;
    public VolleyHelper(Context context){
        this.context = context;
        requestQueue = Volley.newRequestQueue(context);
    }

    public VolleyHelper(Context context, final Callback callback){
        this.context = context;
        this.callback = callback;
        requestQueue = Volley.newRequestQueue(context);
        count = 0;
        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                count--;
                if(count == 0){
                    callback.onFinishedAllRequests();
                }
            }
        });
    }

    public void add(Request request){
        requestQueue.add(request);
        count++;
    }

    public interface Callback {
        void onFinishedAllRequests();
    }
}
