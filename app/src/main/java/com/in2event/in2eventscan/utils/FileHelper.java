package com.in2event.in2eventscan.utils;

import android.graphics.Bitmap;
import android.util.Log;

import com.in2event.in2eventscan.MyApp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * Created by Kangtle_R on 1/26/2018.
 */

public class FileHelper {
    public static void writeStringToFile(String data, String path) {
        try {
            File file = new File(path);
            File directory = file.getParentFile();
            if(!directory.exists()){
                directory.mkdirs();
            }

            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(file));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("File Helper", "File write failed: " + e.toString());
        }
    }


    public static String readStringFromFile(String path) {

        String ret = "";
        if (!exists(path)) return ret;
        try {
            InputStream inputStream = new FileInputStream(path);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String receiveString = "";
            StringBuilder stringBuilder = new StringBuilder();

            while ( (receiveString = bufferedReader.readLine()) != null ) {
                stringBuilder.append(receiveString).append("\n");
            }

            inputStream.close();
            ret = stringBuilder.toString();
        }
        catch (FileNotFoundException e) {
            Log.e("File Helper", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("File Helper", "Can not read file: " + e.toString());
        }

        return ret;
    }



    public static String readStringFromAsset(String fileName) {

        String ret = "";

        try {
            InputStream inputStream = MyApp.getContext().getAssets().open(fileName);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String receiveString = "";
            StringBuilder stringBuilder = new StringBuilder();

            while ( (receiveString = bufferedReader.readLine()) != null ) {
                stringBuilder.append(receiveString).append("\n");
            }

            inputStream.close();
            ret = stringBuilder.toString();
        }
        catch (FileNotFoundException e) {
            Log.e("File Helper", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("File Helper", "Can not read file: " + e.toString());
        }

        return ret;
    }

    public static boolean copyFile(String srcPath, String destPath){
        if(srcPath.equals(destPath)){
            return true;
        }else{
            try {
                InputStream is=new FileInputStream(srcPath);
                OutputStream os=new FileOutputStream(destPath);
                byte[] buff=new byte[1024];
                int len;
                while((len=is.read(buff))>0){
                    os.write(buff,0,len);
                }
                is.close();
                os.close();
            }catch (IOException e){
                Log.e("File Helper", "File write failed: " + e.toString());
            }
        }
        return true;
    }

    public static void saveBitmap(Bitmap bitmap, int quality, String path){
        try {
            OutputStream outputStream = new FileOutputStream(path);
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean exists(String filePath){
        File file = new File(filePath);
        return file.exists();
    }

    public static void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
    }
}
