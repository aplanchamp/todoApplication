package com.example.android.mytodoapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.mytodoapp.parse.Todo;
import com.example.android.mytodoapp.util.Util;
import com.melnykov.fab.FloatingActionButton;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by aude on 25/06/2015.
 */
public class TodoListAdapter extends RecyclerView.Adapter<TodoListAdapter.MyViewHolder> {


    private Context mContext;
    private List<Todo> mList = Collections.emptyList();
    private LayoutInflater mInflater;
    private FloatingActionButton fab;


    public TodoListAdapter(Activity context, FloatingActionButton fab, List<Todo> list) {
        this.mContext = context;
        this.mList = list;
        this.fab = fab;
        mInflater = LayoutInflater.from(context);
    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title, date, description;
        ImageView iconTodo;

        public MyViewHolder(View itemView) {

            super(itemView);
            date = (TextView) itemView.findViewById(R.id.todo_date);
            title = (TextView) itemView.findViewById(R.id.todo_title);
            description = (TextView) itemView.findViewById(R.id.todo_description);
            iconTodo = (ImageView) itemView.findViewById(R.id.icon_todo);
        }
    }

    // in case the adapter needs to be reloaded (edit/add todo)
    // notifyDateSetChanged tells the adapter something has changed
    public void refresh(List<Todo> todos) {
        this.mList = todos;
        notifyDataSetChanged();
    }

    public void delete(int position) {
        mList.remove(position);
        notifyItemRemoved(position);
    }

    public Todo getItem(int position) {
        return mList.get(position);
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.list_item_todo, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }


    // this function populates each item view using the holder class definied before and the todos list given
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        //fetch the todo for this position in the list
        final Todo todo = mList.get(position);


        if (todo.getDescription() != null) {
            holder.description.setText(todo.getDescription());
        }
        holder.title.setText(todo.getTitle());
        // set style and colors
        Date dateToday = new Date();
        boolean isToday = todo.getDateTodo() != null && compareDate(dateToday, todo.getDateTodo());
        boolean isLate = todo.getDateTodo() != null && isLate(dateToday, todo.getDateTodo());
        Log.d("log_tag", "idLAte " + isLate);

        if (todo.getIsTodoCompleted() == false) {


            if (isToday) {
                holder.date.setText("Today");
                holder.iconTodo.setImageResource(R.drawable.ic_action_assignment);
                holder.iconTodo.setColorFilter(mContext.getResources().getColor(R.color.colorPrimary));
                holder.title.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
                holder.date.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
                if (todo.getDescription() != null) {
                    holder.description.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
                }
            } else if (isLate) {

                holder.iconTodo.setImageResource(R.drawable.ic_action_assignment_late);

                String fDate = Util.NORMAL_DATE_ENGLISH_FORMAT.format(todo.getDateTodo());
                holder.date.setText(fDate);
                holder.iconTodo.setColorFilter(mContext.getResources().getColor(R.color.todo_late));
                holder.title.setTextColor(mContext.getResources().getColor(R.color.todo_late));
                holder.date.setTextColor(mContext.getResources().getColor(R.color.todo_late));
                if (todo.getDescription() != null) {
                    holder.description.setTextColor(mContext.getResources().getColor(R.color.todo_late));
                }

            } else {
                String fDate = todo.getDateTodo() != null ? Util.NORMAL_DATE_ENGLISH_FORMAT.format(todo.getDateTodo()) : "Reminder";
                holder.date.setText(fDate);
                holder.iconTodo.setImageResource(R.drawable.ic_action_assignment);
                holder.title.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryText));
                holder.date.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryText));
                if (todo.getDescription() != null) {
                    holder.description.setTextColor(mContext.getResources().getColor(R.color.colorSecondaryText));
                }
            }

            if (todo.getImportance() == 1) {
                holder.title.setTypeface(null, Typeface.BOLD);
                holder.date.setTypeface(null, Typeface.BOLD);
            } else {
                holder.title.setTypeface(null, Typeface.NORMAL);
                holder.date.setTypeface(null, Typeface.NORMAL);
            }

        } else {
            holder.iconTodo.setImageResource(R.drawable.ic_action_assignment_turned_in);
            holder.title.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryText));
            if (todo.getDescription() != null) {
                holder.description.setTextColor(mContext.getResources().getColor(R.color.colorSecondaryText));
            }
        }
    }

    public boolean compareDate(Date dateFirst, Date dateSecond) {
        if ((dateFirst.getYear() == dateSecond.getYear())
                && (dateFirst.getMonth() == dateSecond.getMonth())
                && (dateFirst.getDate() == dateSecond.getDate()))
            return true;
        else
            return false;
    }

    public boolean isLate(Date dateFirst, Date dateSecond) {
        if (dateFirst.getYear() > dateSecond.getYear())
            return true;
        else if ((dateFirst.getYear() == dateSecond.getYear()) && (dateFirst.getMonth() > dateSecond.getMonth()))
            return true;
        else if ((dateFirst.getYear() == dateSecond.getYear()) && (dateFirst.getMonth() == dateSecond.getMonth()) && (dateFirst.getDate() > dateSecond.getDate()))
            return true;
        else
            return false;
    }
}

