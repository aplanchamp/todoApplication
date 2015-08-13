package com.example.android.mytodoapp.tasks;

import android.content.Context;


import com.example.android.mytodoapp.parse.ParseHelper;
import com.example.android.mytodoapp.parse.Todo;

import java.util.List;

/**
 * Created by aude on 01/07/2015.
 */

// see WorkerThread class for more information

public class SyncDataFromServerTask extends WorkerThread {

    public SyncDataFromServerTask(Context context,boolean showProgress) {
        super(context);
        enableProgressBar(showProgress);
    }

    // fetch in background todos from parse

    @Override
    public String onWorkInBackground() {
        try {
            List<Todo> todoList = ParseHelper.findAllTodos(true);
            List<Todo> todoListCompleted = ParseHelper.findAllTodosCompleted(true);

        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
}

