package com.example.android.mytodoapp.parse;

import android.content.Context;
import android.util.Log;

import com.example.android.mytodoapp.parse.Todo;
import com.parse.DeleteCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

/**
 * Created by aude on 01/07/2015.
 */
public class ParseHelper {

    private static final String TAG = "ParseHelper";

    public static void initParse(Context context) {

        // Enable Local Datastore.
        Parse.enableLocalDatastore(context);

        ParseObject.registerSubclass(Todo.class);

        //Initialization
        Parse.initialize(context, "a6yXCPvwZDKs1z2wtG3p4cn4okNcR4YTNG4xopG8", "QK44MHop1S5U1FGhXMOk8WT8Aqt6pwwFdtQ5PbN1");
    }


    public static boolean isLogin() {
        return ParseUser.getCurrentUser() != null;
    }

    private static void showLog(String msg) {
        Log.i(TAG, msg);
    }


    /** query to get all todos.
     * if "isFromServer" is true, query parse and put all todos inthe local database. (internet needed)
     * if "isFromServer" is false, query the localdatabase.
     * returns list of todos fetched.
    */

    public static List<Todo> findAllTodos(boolean isFromServer) throws ParseException {
        ParseQuery<Todo> query = Todo.getQuery();
        query.whereEqualTo("author", ParseUser.getCurrentUser());
        query.whereEqualTo("isCompleted", false);
        query.addAscendingOrder("date");
        if (!isFromServer) {
            query.fromLocalDatastore();
        }
        List<Todo> todos = query.find();
        if (isFromServer) {
            Todo.pinAll(todos);
        }
        return todos;
    }


    public static List<Todo> findAllTodosCompleted(boolean isFromServer) throws ParseException {
        ParseQuery<Todo> query = Todo.getQuery();
        query.whereEqualTo("author", ParseUser.getCurrentUser());
        query.whereEqualTo("isCompleted", true);
        query.addAscendingOrder("date");
        if (!isFromServer) {
            query.fromLocalDatastore();
        }
        List<Todo> todos = query.find();
        if (isFromServer) {
            Log.d("log_tag", "todos " +todos);
            Todo.pinAll(todos);
        }
        return todos;
    }

    public static void completedTodo(Todo todo, SaveCallback callback){
        if (todo != null){
            todo.setIsTodoCompleted(true);
            updateEventually(todo, callback);
        }
    }

    public static void saveEventually(ParseObject object, SaveCallback callback) {
        object.pinInBackground(callback);
        object.saveEventually();
    }

    public static void deleteObject(ParseObject object, DeleteCallback callback) {
        object.deleteEventually(callback);
    }

    public static void updateEventually(ParseObject object, SaveCallback callback) {
        object.saveEventually(callback);
    }

}
