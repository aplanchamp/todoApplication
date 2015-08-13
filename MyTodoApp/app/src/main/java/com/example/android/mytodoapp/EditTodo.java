package com.example.android.mytodoapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.android.mytodoapp.parse.ParseHelper;
import com.example.android.mytodoapp.parse.Todo;

import com.example.android.mytodoapp.util.AlertUtils;
import com.example.android.mytodoapp.util.MyDatePicker;
import com.example.android.mytodoapp.util.Util;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Calendar;


public class EditTodo{

    private static final String TAG = "TodoEditDialog";

    private EditText mTitle, mDescription;
    private Spinner mImportance;
    private TextView todoDate;
    private Calendar mDate = null;
    private AlertDialog dialog;
    private boolean todoExist, isDateSelected;
    private CurrentTodoFragment mActivity;
    private Todo todo;
    private  OnTodoEditListener listener;

    // implement listener
    public interface OnTodoEditListener {
        public void onUpdate(Todo todo, boolean isNewTodo);
    }

    // implement constructor
    public EditTodo(CurrentTodoFragment activity, final Todo t, final OnTodoEditListener listener) {
        mActivity = activity;
        todo = t;
        this.listener = listener;
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity.getActivity());

        LayoutInflater inflater = LayoutInflater.from(mActivity.getActivity());
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.edit_todo, null);

        todoExist = todo != null;

        mTitle = (EditText) layout.findViewById(R.id.todo_title);
        mDescription = (EditText) layout.findViewById(R.id.todo_description);
        todoDate = (TextView) layout.findViewById(R.id.date_todo);
        mImportance = (Spinner) layout.findViewById(R.id.category);

        // if todo exists, set textViews to this todo parameters

        if (todoExist) {
            mTitle.setText(todo.getTitle());
            mDescription.setText(todo.getDescription());
            mImportance.setSelection(todo.getImportance());
            isDateSelected = todo.getDateTodo() != null;
            if (isDateSelected) {
                mDate = Calendar.getInstance();
                mDate.setTime(todo.getDateTodo());
                todoDate.setText(Util.NORMAL_DATE_ENGLISH_FORMAT.format(mDate.getTime()));
            }

        }

        // when SET button for set date is selected
        (layout.findViewById(R.id.pick_dateButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MyDatePicker(mActivity.getActivity(), Calendar.getInstance().getTimeInMillis(), - 1, Calendar.getInstance().getTimeInMillis(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        mDate = Calendar.getInstance();
                        mDate.set(year, monthOfYear, dayOfMonth);
                        mDate.set(Calendar.HOUR_OF_DAY, mDate.getMinimum(Calendar.HOUR_OF_DAY));
                        mDate.set(Calendar.MINUTE, mDate.getMinimum(Calendar.MINUTE));
                        mDate.set(Calendar.SECOND, mDate.getMinimum(Calendar.SECOND));

                        todoDate.setText(Util.NORMAL_DATE_ENGLISH_FORMAT.format(mDate.getTime()));
                        isDateSelected = true;
                    }
                }).show();
            }
        });

        // implements dialog buttons and title
        builder.setView(layout);
        builder.setTitle(todoExist ? R.string.edit_todo : R.string.add_todo);
        builder.setPositiveButton(todoExist ? R.string.save : R.string.add, null);
        builder.setNegativeButton(R.string.cancel, null);

        // Create the AlertDialog object and return it
        dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface d) {
                AlertUtils.changeDefaultColor(dialog);

                // fetch positive button of dialog
                final Button addButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);

                // if title is blank, positive button is disabled
                if (mTitle.getText().length() <= 0) {
                    addButton.setEnabled(false);
                }

                // save/add todo when positive button is pressed
                addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // TODO Do something
                        checkAndSaveDetails();
                    }
                });

                // change add button dynamically : enable it when something is written in title EditText
                mTitle.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (s.length() > 0) {
                            addButton.setEnabled(true);
                        } else {
                            addButton.setEnabled(false);
                        }
                    }
                });
            }
        });
    }

    // create/add todo
    private void checkAndSaveDetails() {

        String name = mTitle.getText().toString();
        if (name == null || name.isEmpty()) {
            showAlert("Please enter name");

            mTitle.requestFocus();
            return;
        }


        // set todo  information
        if (!todoExist) {
            todo = new Todo();
        }
        todo.setTitle(name);
        todo.setDescription(mDescription.getText().toString());
        todo.setImportance(mImportance.getSelectedItemPosition());
        todo.setIsTodoCompleted(false);
        if (isDateSelected)
            todo.setDateTodo(mDate.getTime());

        // update/save todo
        saveTodo();

    }

    public void show() {
        if (dialog != null) {
            dialog.show();

        }
    }


    private void saveTodo() {
        if(todoExist)
        {
            updateTodo();
        }
        else
        {
            createNewTodo();
        }
    }


    // add todo to local database and in parse as soon as network allows it
    private void createNewTodo() {
       todo.setAuthor(ParseUser.getCurrentUser());
        ParseHelper.saveEventually(todo, new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    sendCallback();
                } else {
                    e.printStackTrace();
                    showAlert(mActivity.getString(R.string.add_fail));
                }

            }
        });
    }

    // update todo to local database and in parse as soon as network allows it
    private void updateTodo()
    {
        ParseHelper.updateEventually(todo, null);
        sendCallback();

    }

    // notification for change
    private void sendCallback()
    {
        if (listener!=null)
        {
            listener.onUpdate(todo, !todoExist);
        }
        dialog.dismiss();
    }

    public void showAlert(String msg) {
        AlertDialog dialog = new AlertDialog.Builder(mActivity.getActivity()).setIcon(0).setTitle("Alert").setMessage(msg).setNeutralButton("Ok", null).show();
        AlertUtils.changeDefaultColor(dialog);
    }



}

// PREVIOUS CODE WITH ACTITIVY INSTEAD OF DIALOG

//public class EditTodo extends ActionBarActivity {
//
//    private EditText mTitle;
//    private EditText mDescription;
//    private TextView mPlm;
//    private Spinner mImportance;
//    private static Date date;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.edit_todo);
//
//        String todoId = null;
//
//        if (getIntent().hasExtra("ID")) {
//            todoId = getIntent().getExtras().getString("ID");
//        }
//
//        mTitle = (EditText) findViewById(R.id.todo_title);
//        mDescription = (EditText) findViewById(R.id.todo_description);
//        mPlm = (TextView) findViewById(R.id.todo_add_plm);
//        mImportance = (Spinner) findViewById(R.id.category);
//
//        final Button mButtonEdit = (Button) findViewById(R.id.todo_edit_button);
//        Button mpickDate = (Button) findViewById(R.id.pick_dateButton);
//
//
//
//        final ParseUser currentUser = ParseUser.getCurrentUser();
//        final String finalTodoId = todoId;
//        if (finalTodoId != null){
//
//            ParseQuery<Todo> query = Todo.getQuery();
//            query.getInBackground(finalTodoId, new GetCallback<Todo>() {
//                public void done(Todo todo, ParseException e) {
//                    if (e == null) {
//                        mTitle.setText(todo.getTitle());
//                        mDescription.setText(todo.getDescription());
//                        mImportance.setSelection(todo.getImportance());
//
//                    } else {
//                        // something went wrong
//                        Log.e("log_tag", "echec du chargement du todo");
//                    }
//                }
//            });
//
//
//        }
//
//        mButtonEdit.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View view) {
//                if (mTitle.getText().length() == 0) {
//                    mPlm.setText(R.string.no_title);
//                    return;
//                }
//                if (finalTodoId == null)
//                    createTodo();
//                else {
//                    updateTodo();
//                }
//            }
//
//            private void createTodo() {
//                if (currentUser != null) {
//                    // do stuff with the user
//                    Todo todoObject = new Todo();
//                    todoObject.setTitle(mTitle.getText().toString());
//                    todoObject.setDescription(mDescription.getText().toString());
//                    todoObject.setImportance(mImportance.getSelectedItemPosition());
//                    todoObject.setAuthor(currentUser);
//                    if (date != null)
//                        todoObject.setDateTodo(date);
//                    date = null;
//                    ParseHelper.saveEventually(todoObject, new SaveCallback() {
//                        @Override
//                        public void done(ParseException e) {
//                            if (e == null) {
//                                startActivity(new Intent(EditTodo.this,
//                                        CurrentTodoFragment.class));
//                            } else {
//                                e.printStackTrace();
//                            }
//                        }
//                    });
//
//                } else {
//                    mPlm.setText(R.string.add_fail);
//                }
//
//
//            }
//
//            private void updateTodo() {
//                if (currentUser != null) {
//                    ParseQuery<Todo> query = Todo.getQuery();
//                    query.fromLocalDatastore();
//                    query.getInBackground(finalTodoId, new GetCallback<Todo>() {
//                        public void done(Todo todo, ParseException e) {
//                            if (e == null) {
//                                todo.setTitle(mTitle.getText().toString());
//                                todo.setDescription(mDescription.getText().toString());
//                                todo.setImportance(mImportance.getSelectedItemPosition());
//                                todo.setAuthor(currentUser);
//                                if (date != null)
//                                    todo.setDateTodo(date);
//                                else
//                                    todo.setDateTodo(todo.getDateTodo());
//                                date = null;
//                                ParseHelper.updateObject(todo, new SaveCallback() {
//                                    @Override
//                                    public void done(ParseException e) {
//                                        if (e == null) {
//                                            startActivity(new Intent(EditTodo.this,
//                                                    CurrentTodoFragment.class));
//                                        } else {
//                                            e.printStackTrace();
//                                            mPlm.setText(R.string.add_fail);
//                                        }
//                                    }
//                                });
//
//                            } else {
//                                mPlm.setText(R.string.add_fail);
//                                // something went wrong
//                                Log.e("log_tag", "echec du chargement du todo");
//                            }
//                        }
//                    });
//                }
//
//
//            }
//
//        });
//
//        mpickDate.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View view) {
//                DialogFragment newFragment = new DateTimePickerFragment();
//                newFragment.show(getFragmentManager(), "datePicker");
//
//            }
//
//        });
//
//    }
//
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.todo_menu, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_logout) {
//            ParseUser.logOut();
//            startActivity(new Intent(EditTodo.this, FragmentLogin.class));
//            finish();
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
//
//
//
//    public static class DateTimePickerFragment extends DialogFragment
//            implements DatePickerDialog.OnDateSetListener {
//
//
//
//        @Override
//        public Dialog onCreateDialog(Bundle savedInstanceState) {
//            // Use the current date as the default date in the picker
//            final Calendar c = Calendar.getInstance();
//            int year = c.get(Calendar.YEAR);
//            int month = c.get(Calendar.MONTH);
//            int day = c.get(Calendar.DAY_OF_MONTH);
//
//            // Create a new instance of DatePickerDialog and return it
//            return new DatePickerDialog(getActivity(), this, year, month, day);
//        }
//
//        @Override
//        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//            Calendar calendar = Calendar.getInstance();
//            calendar.set(year, monthOfYear, dayOfMonth);
//            date = calendar.getTime();
//        }
//    }
//}
//
//
