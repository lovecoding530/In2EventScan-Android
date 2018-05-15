package com.in2event.in2eventscan.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.Result;
import com.in2event.in2eventscan.R;
import com.in2event.in2eventscan.fragments.CodeSannerFragment;
import com.in2event.in2eventscan.services.SyncService;
import com.in2event.in2eventscan.utils.Contents;
import com.in2event.in2eventscan.utils.JsonHelper;
import com.in2event.in2eventscan.utils.MyHelper;
import com.in2event.in2eventscan.utils.MyPreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, CodeSannerFragment.OnScanCodeListener{

    private ConnectivityReceiver connectivityReceiver;
    private MyPreference myPref;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
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

                    boolean success = response.optBoolean("success");
                    String message = response.optString("message");
                    String customer = response.optString("customer");
                    String product = response.optString("product");
                    gotoResultActivity(success, message, customer, product);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressBar.setVisibility(View.GONE);
                    gotoResultActivity(false, "An error happens", "", "");
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
        }else{
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
    }

    private void gotoResultActivity(boolean success, String message, String customer, String product){
        Intent intent = new Intent(MainActivity.this, ScanResultActivity.class);
        intent.putExtra("success", success);
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
            }
        }
    }
}
