package com.example.android.mytodoapp.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by aude on 01/07/2015.
 */
public class InternetUtils {

    // check if device is connected to network

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            return false;
        } else
            return true;
    }

    // AlertDialog in case no network was found, force closing the app depending on "isForceClose

    public static void showInternetAlert(final Context context, final boolean isForseClose)
    {
        new AlertDialog.Builder(context).setIcon(0).setTitle("No Connection").setMessage("Please check your internet connection and try again!").setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (isForseClose) {
                    ((Activity)context).finish();
                }
            }
        }).show();
    }
}