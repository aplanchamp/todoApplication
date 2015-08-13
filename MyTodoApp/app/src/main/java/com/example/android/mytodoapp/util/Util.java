package com.example.android.mytodoapp.util;

import android.text.TextUtils;
import android.view.View;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by aude on 01/07/2015.
 */
public class Util {

    private static final String TAG = "Util";

    // set up a proper date format for UI
    public static final DateFormat NORMAL_DATE_ENGLISH_FORMAT = new SimpleDateFormat("MM/dd/yyyy");

    // check is email is valid
    public final static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public static void setVisibilityTodo(int adapterEmpty, View view){
        if (adapterEmpty == 0){
            view.setVisibility(View.VISIBLE);
        }
        else
            view.setVisibility(View.INVISIBLE);
    }



}
