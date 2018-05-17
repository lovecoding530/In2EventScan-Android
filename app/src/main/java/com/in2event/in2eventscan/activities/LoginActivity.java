package com.in2event.in2eventscan.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;
import com.in2event.in2eventscan.R;
import com.in2event.in2eventscan.fragments.CodeSannerFragment;
import com.in2event.in2eventscan.utils.Contents;
import com.in2event.in2eventscan.utils.MyPreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class LoginActivity extends AppCompatActivity implements CodeSannerFragment.OnScanCodeListener {

    private ProgressBar progressBar;
    private MyPreference myPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressBar = findViewById(R.id.progressBar);
        myPref = new MyPreference(this);

        CodeSannerFragment sannerFragment = CodeSannerFragment.newInstance(this);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.scanner_fragment_container, sannerFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onScanCode(final String code) {
        progressBar.setVisibility(View.VISIBLE);

        JSONObject params = new JSONObject();
        try {
            params.put("access_token", code);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, Contents.API_AUTHORIZE, params, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                progressBar.setVisibility(View.GONE);

                myPref.setAccessToken(response.optString(Contents.JsonEvent.TOKEN));
                myPref.setEventName(response.optString(Contents.JsonEvent.EVENT_NAME));
                myPref.setEventLogo(response.optString(Contents.JsonEvent.EVENT_LOGO));
                myPref.setEventDates(response.optString(Contents.JsonEvent.EVENT_DATES));
                myPref.setEventCity(response.optString(Contents.JsonEvent.EVENT_CITY));

                Intent intent = new Intent(LoginActivity.this, LoginSuccessActivity.class);
                startActivity(intent);
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Intent intent = new Intent(LoginActivity.this, LoginFailedActivity.class);
                startActivity(intent);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("X-ACCESS-TOKEN", code);
                return headers;
            }
        };

        queue.add(jsonRequest);
    }
}
