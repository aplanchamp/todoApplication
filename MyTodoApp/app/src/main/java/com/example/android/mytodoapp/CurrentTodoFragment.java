package com.example.android.mytodoapp;

import android.app.AlertDialog;
import android.support.v4.app.Fragment;
import android.content.DialogInterface;
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
import android.widget.Toast;

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



public class CurrentTodoFragment extends Fragment {

    private EditTodo editTodo;
    private CurrentTodoFragment mActivity;


    // all those variables needed to create and populate a listView
    private TodoListAdapter todoListAdapter;
    private RecyclerView todoListView;
    private List<Todo> todos;
    private FloatingActionButton fab;
    private LinearLayoutManager mLayoutManager;
    private View viewEmpty;


    // the variable mActivity must be stored because used when calling EditTodo constructor : it is not possible to use the variable "this" inside setOnClicklistener
    // because "this" would then refer to onClickListener instead of CurrentTodoFragment
    public CurrentTodoFragment() {
        mActivity = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_current_todo, container, false);

        viewEmpty = (TextView) layout.findViewById(R.id.empty);

        // link the listView to the xml file
        todoListView = (RecyclerView) layout.findViewById(R.id.list_todos);
        todoListView.setHasFixedSize(true);
        // setting a layout manager is necessary for adding a divider item (see DividerItem class in util package)
        mLayoutManager = new LinearLayoutManager(getActivity());
        todoListView.setLayoutManager(mLayoutManager);
        todoListView.addItemDecoration(new DividerItem(getActivity()));
        fab = (FloatingActionButton) layout.findViewById(R.id.fab);


        // create a todo
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // second argument is "null" because this is a new todo
                editTodo = new EditTodo(mActivity, null, new EditTodo.OnTodoEditListener() {
                    @Override
                    public void onUpdate(Todo todo, boolean isNewTodo) {
                        findAllTodos();
                        editTodo = null;
                    }
                });
                editTodo.show();
            }
        });

        // chec for clicks on items in recycler view. Onclick : edit todo ; OnLongClick : nothing. see RecyclerTouchListener class
        todoListView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), todoListView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                final Todo todoItem = todoListAdapter.getItem(position);
                // here EditTodo is to update an existing todo, so the second argument is the Todo that has to be updated itself (not null)
                editTodo = new EditTodo(mActivity, todoItem, new EditTodo.OnTodoEditListener() {
                    @Override
                    public void onUpdate(Todo todo, boolean isNewTodo) {
                        updateAdapter();
                        editTodo = null;
                    }
                });
                editTodo.show();
//            }
            }

            @Override
            public void onLongClick(View view, int position) {


            }

        }));


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
                // callback for swipe to dismiss, removing item from adapter and cahnge flag "isCpmpleted" in database to "true" :
                // this todo now belongs to "Completed todos"

                final Todo todoItem = todoListAdapter.getItem(viewHolder.getAdapterPosition());

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Are you sure you have completed this todo ?");
                builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ParseHelper.completedTodo(todoItem, new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                findAllTodos();
                            }
                        });
                    }
                });
                builder.setNegativeButton("no", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        return;
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                AlertUtils.changeDefaultColor(dialog);
            }
        });
        swipeToDismissTouchHelper.attachToRecyclerView(todoListView);



        findAllTodos();

        Log.d("log_tag","todos current " + todos);
        return layout;

    }


    // query local database to fetch all todos in background. reload list when it's done.
    private void findAllTodos() {

        new WorkerThread(getActivity().getApplicationContext()) {

            @Override
            public String onWorkInBackground() {
                try {
                    todos = ParseHelper.findAllTodos(false);
                    return "sucess";
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected synchronized void onWorkFinished(String result) {
                updateAdapter();
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
