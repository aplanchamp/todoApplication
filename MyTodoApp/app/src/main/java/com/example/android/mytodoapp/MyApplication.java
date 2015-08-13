package com.example.android.mytodoapp;

import android.util.Log;

import com.example.android.mytodoapp.parse.ParseHelper;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.SaveCallback;

/**
 * Created by aude on 24/06/2015.
 */


// a class extending from Application is needed to use parse

public class MyApplication extends android.app.Application {

    @Override
    public void onCreate() {

        super.onCreate();

        ParseHelper.initParse(MyApplication.this);
    }
}
