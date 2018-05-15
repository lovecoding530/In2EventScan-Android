package com.in2event.in2eventscan.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by Kangtle_R on 2/1/2018.
 */

public class AlertHelper {
    public static void question(Context context, String tittle, String question,
                                String btnPositive, String btnNegative,
                                DialogInterface.OnClickListener positiveClickListener, DialogInterface.OnClickListener negativeClickListener){
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setCancelable(false);
        alertDialog.setTitle(tittle);
        alertDialog.setMessage(question);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, btnPositive, positiveClickListener);
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, btnNegative, negativeClickListener);
        alertDialog.show();
    }

    public static void message(Context context, String tittle, String message){
        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setCancelable(false);
        alertDialog.setTitle(tittle);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    public static void message(Context context, String tittle, String message, DialogInterface.OnClickListener onClickListener){
        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setCancelable(false);
        alertDialog.setTitle(tittle);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", onClickListener);
        alertDialog.show();
    }
}
