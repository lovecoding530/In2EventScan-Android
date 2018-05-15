package com.in2event.in2eventscan.utils;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * Created by Kangtle_R on 1/17/2018.
 */

public class MyHelper {
    public static boolean isConnectedInternet(Context context){
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    public static Bitmap getScaledBitMap(String path){

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, bmOptions);
        int photoW = bmOptions.outWidth;

        // Determine how much to scale down the image

//        int scaleFactor = photoW/targetWidth;
        int scaleFactor = 16;

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(path, bmOptions);
        return bitmap;
    }

    public static void hideKeyBoard(Activity activity, View view){
        //Method 1
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static Point getScreenSize(Activity activity){
        DisplayMetrics dMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dMetrics);
        float density = dMetrics.density;
        int w = Math.round(dMetrics.widthPixels / density);
        int h = Math.round(dMetrics.heightPixels / density);
        int widthOrientation = (w>h)?w:h;
        int heightOrientation = (w<h)?w:h;
        return new Point(widthOrientation, heightOrientation);
    }

    public static boolean isServiceRunning(Context context, Class serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> services = manager.getRunningServices(Integer.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo service : services){
            String mClassName = service.service.getClassName();
            if(serviceClass.getName().equals(mClassName)) {
                return true;
            }
        }
        return false;
    }

    public static String getPhoneNumber(Context context){
        Log.d("Kangtle", "Trying to get phone number");
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            Log.d("Kangtle", "READ_PHONE_STATE permission is not granted");
            return null;
        }else{
            Log.d("Kangtle", "READ_PHONE_STATE permission is already granted");
            String phoneNumber = telephonyManager.getLine1Number();

            if (phoneNumber == null){
                Log.d("Kangtle", "Can't get Phone number");
                return null;
            }else if (phoneNumber.toLowerCase().equals("null")){
                Log.d("Kangtle", "Phone number got as NULL");
                return null;
            }else{
                phoneNumber = phoneNumber.replaceAll("[+|\\s]", "");
                if (phoneNumber.startsWith("1555")){ // emulator
                    phoneNumber = "8617165340102"; // my phone
                }
                return phoneNumber;
            }
        }
    }

    public static String getLogs(){
        Log.d("Kangtle", "getting logs");
        StringBuilder log = new StringBuilder();
        String separator = System.getProperty("line.separator");
        try {
//            Process process = Runtime.getRuntime().exec("logcat -d");
            Process process = Runtime.getRuntime().exec("logcat -d -s Kangtle:D *:E");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                log.append(line);
                log.append(separator);
            }
        } catch (Exception e) {

        }
        String logStr = log.toString();
        String regex = "\\d{2}-\\d{2}\\s\\d{2}:\\d{2}:\\d{2}.\\d{3}\\s{1,3}\\d{3,5}\\s{1,3}\\d{3,5}\\s.\\s\\w{1,30}\\s{0,10}:\\s{0,10}";
        String replacedStr = logStr.replaceAll(regex, "");
        return replacedStr;
    }

    public static void clearLogs(){
        Log.d("Kangtle", "erase logs");
        try {
            Process process = Runtime.getRuntime().exec("logcat -c");
            process.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
