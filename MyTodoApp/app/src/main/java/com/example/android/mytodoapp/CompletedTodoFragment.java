package com.example.android.mytodoapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.mytodoapp.util.AlertUtils;
import com.example.android.mytodoapp.util.DividerItem;
import com.example.android.mytodoapp.util.Util;
import com.melnykov.fab.FloatingActionButton;
import com.example.android.mytodoapp.parse.ParseHelper;
import com.example.android.mytodoapp.parse.Todo;
import com.example.android.mytodoapp.tasks.WorkerThread;
import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;


public class CompletedTodoFragment extends Fragment {


    // all variables needed to create and populate a listView
    private TodoListAdapter todoListAdapter;
    private RecyclerView todoListView;
    private List<Todo> todos;
    private FloatingActionButton fab;
    private LinearLayoutManager mLayoutManager;

    // view in case there are no todos yet
    private View viewEmpty;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_completed_todo, container, false);


        viewEmpty = (TextView) layout.findViewById(R.id.empty);
        fab = (FloatingActionButton) layout.findViewById(R.id.fab);
        // the floating action button is hidden here because todos can't be added. CompletedTodoFragment uses the same adapter as CurrentTodoFragment,
        // which is why the fab button has to be here but is hidden
        fab.hide();

        // link the listView to the xml file
        todoListView = (RecyclerView) layout.findViewById(R.id.list_todos);
        todoListView.setHasFixedSize(true);
        // setting a layout manager is necessary for adding a divider item (see DividerItem class in util package)
        mLayoutManager = new LinearLayoutManager(getActivity());
        todoListView.setLayoutManager(mLayoutManager);
        todoListView.addItemDecoration(new DividerItem(getActivity()));


        // init swipe to dismiss logic
        ItemTouchHelper swipeToDismissTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                // callback for drag-n-drop, false to skip this feature
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                // callback for swipe to dismiss, removing item from data and adapter

                final Todo todoItem = todoListAdapter.getItem(viewHolder.getAdapterPosition());
                todoListAdapter.delete(viewHolder.getAdapterPosition());
                ParseHelper.deleteObject(todoItem, new DeleteCallback() {
                    @Override
                    public void done(ParseException e) {
                        findAllTodos();
                    }
                });
            }
        });
        swipeToDismissTouchHelper.attachToRecyclerView(todoListView);

        findAllTodos();
        return layout;

    }


    // query local database to fetch all todos in background. reload list when it's done.
    private void findAllTodos() {

        new WorkerThread(getActivity().getApplicationContext()) {

            @Override
            public String onWorkInBackground() {
                try {
                    todos = ParseHelper.findAllTodosCompleted(false);
                    return "sucess";
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected synchronized void onWorkFinished(String result) {
                updateAdapter();
                Log.d("log_tagTest", "coucou");
            }
        }.start();

    }


    // update adapter so reload list : the new list of todos "todos" is given
    private void updateAdapter() {
        List<Todo> list = new ArrayList<Todo>();
        list.addAll(todos);
        if (todoListAdapter == null) {
            todoListAdapter = new TodoListAdapter(getActivity(), fab, list);
            todoListView.setAdapter(todoListAdapter);
            Util.setVisibilityTodo(todoListAdapter.getItemCount(), viewEmpty);
        } else {
            todoListAdapter.refresh(list);
            todoListView.setAdapter(todoListAdapter);
            Util.setVisibilityTodo(todoListAdapter.getItemCount(), viewEmpty);
        }
    }

}
