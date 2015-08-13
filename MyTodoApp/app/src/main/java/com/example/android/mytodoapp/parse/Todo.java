package com.example.android.mytodoapp.parse;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Date;

/**
 * Created by aude on 25/06/2015.
 */

// create the todo table

@ParseClassName("Todo")
public class Todo extends ParseObject{

    public String getTitle() {
        return getString("title");
    }
    public void setTitle(String value) {
        put("title", value);
    }

    public String getDescription() {
        return getString("description");
    }
    public void setDescription(String value) {
        put("description", value);
    }

    public ParseUser getAuthor() {
        return getParseUser("author");
    }

    public void setAuthor(ParseUser currentUser) {
        put("author", currentUser);
    }

    public static ParseQuery<Todo> getQuery() {
        return ParseQuery.getQuery(Todo.class);
    }

    public Date getDateTodo(){
        return getDate("date");
    }

    public void setDateTodo(Date date){
        put("date",date);
    }

    public int getImportance() {
        return getInt("importance");
    }

    public void setImportance (int value){
        put("importance",value);
    }

    public void setIsTodoCompleted(boolean value){
        put("isCompleted", value);
    }

    public boolean getIsTodoCompleted(){
        return getBoolean("isCompleted");
    }

}
