package com.in2event.in2eventscan.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.Result;
import com.in2event.in2eventscan.R;
import com.in2event.in2eventscan.fragments.CodeSannerFragment;
import com.in2event.in2eventscan.services.SyncService;
import com.in2event.in2eventscan.utils.AlertHelper;
import com.in2event.in2eventscan.utils.Contents;
import com.in2event.in2eventscan.utils.JsonHelper;
import com.in2event.in2eventscan.utils.MyHelper;
import com.in2event.in2eventscan.utils.MyPreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, CodeSannerFragment.OnScanCodeListener{

    private ConnectivityReceiver connectivityReceiver;
    private MyPreference myPref;
    private ProgressBar progressBar;
    private Toolbar toolbar;
    private LinearLayout conectoinLostView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        conectoinLostView = findViewById(R.id.connection_lost);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        myPref = new MyPreference(this);
        
        progressBar = findViewById(R.id.progressBar);
        
        CodeSannerFragment sannerFragment = CodeSannerFragment.newInstance(this);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.scanner_fragment_container, sannerFragment);
        fragmentTransaction.commit();

        Contents.cachedBarcodes = myPref.getCachedBarcodes();
        updateBarcodesHandler.post(updateBarcodesRunnable);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_scan) {
            // Handle the camera action
        } else if (id == R.id.nav_logout) {
            AlertHelper.question(this, "In2EventScan", "Are you sure to log out?", "OK", "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    myPref.setAccessToken(null);
                    finish();
                    Intent intent = new Intent(MainActivity.this, StartActivity.class);
                    startActivity(intent);
                }
            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }else{

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onScanCode(final String code) {
        if(MyHelper.isConnectedInternet(this)){
            progressBar.setVisibility(View.VISIBLE);

            JSONObject params = new JSONObject();
            try {
                params.put("barcode", code);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            RequestQueue queue = Volley.newRequestQueue(this);
            JsonRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, Contents.API_ALL_BARCODES, params, new Response.Listener<JSONObject>(){
                @Override
                public void onResponse(JSONObject response) {
                    progressBar.setVisibility(View.GONE);
                    Contents.scannedBarcodes.add(code);

                    String resp = response.optString("response");
                    String message = response.optString("message");
                    String customer = response.optString("customer");
                    String product = response.optString("product");
                    gotoResultActivity(resp, message, customer, product);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressBar.setVisibility(View.GONE);
                    gotoResultActivity("ERROR", "An error happens", "", "");
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    Locale current = getResources().getConfiguration().locale;
                    headers.put("X-ACCESS-TOKEN", myPref.getAccessToken());
                    headers.put("LANG", current.getLanguage());
                    return headers;
                }
            };

            queue.add(jsonRequest);
        }
        /*
        else{
            if (Contents.scannedBarcodes.contains(code)){
                gotoResultActivity(false, "Barcode already scanned", "", "");
            }else {
                int indexOfCached = JsonHelper.indexOf(Contents.cachedBarcodes, "barcode", code);
                if(indexOfCached >= 0){
                    myPref.addBarcodeToQueue(code);

                    JSONObject jsonObject = (JSONObject) Contents.cachedBarcodes.opt(indexOfCached);
                    Contents.cachedBarcodes = JsonHelper.remove(Contents.cachedBarcodes, indexOfCached);

                    String message = jsonObject.optString("message");
                    String customer = jsonObject.optString("customer");
                    String product = jsonObject.optString("product");

                    gotoResultActivity(true, "Successfully Scanned", customer, product);
                }else {
                    gotoResultActivity(false, "Barcode not found", "", "");
                }
            }
        }
        */
    }

    private void gotoResultActivity(String response, String message, String customer, String product){
        Intent intent = new Intent(MainActivity.this, ScanResultActivity.class);
        intent.putExtra("response", response);
        intent.putExtra("message", message);
        intent.putExtra("customer", customer);
        intent.putExtra("product", product);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerConnectivityAction();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(connectivityReceiver);
    }

    private void registerConnectivityAction(){
        connectivityReceiver = new ConnectivityReceiver();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(connectivityReceiver, intentFilter);
    }

    private Handler updateBarcodesHandler = new Handler();
    private Runnable updateBarcodesRunnable = new Runnable() {
        @Override
        public void run() {
            updateBarcodesHandler.postDelayed(updateBarcodesRunnable, 5 * 60 * 1000);
            getAllBarcodes();
        }
    };

    public void getAllBarcodes(){
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest arrayRequest = new JsonArrayRequest(Contents.API_ALL_BARCODES, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d("Kangtle", response.toString());
                Contents.cachedBarcodes = response;
                myPref.setCachedBarcodes(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Kangtle", "All bar codes onErrorResponse");
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                Log.d("Kangtle", "saved token " + myPref.getAccessToken());
                headers.put("X-ACCESS-TOKEN", myPref.getAccessToken());
                return headers;
            }
        };
        queue.add(arrayRequest);
    }

    private class ConnectivityReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isConnected = MyHelper.isConnectedInternet(MainActivity.this);
            Log.d("Kangtle", "network is connected " + isConnected);
            if(MyHelper.isConnectedInternet(MainActivity.this)){
                Intent syncServiceIntent = new Intent(context, SyncService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(syncServiceIntent);
                }else{
                    context.startService(syncServiceIntent);
                }
                MainActivity.this.toolbar.setVisibility(View.VISIBLE);
                MainActivity.this.conectoinLostView.setVisibility(View.GONE);
            }else{
                MainActivity.this.toolbar.setVisibility(View.GONE);
                MainActivity.this.conectoinLostView.setVisibility(View.VISIBLE);
            }
        }
    }
}
