package com.example.android.mytodoapp.user;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.android.mytodoapp.MyNavigationDrawerActivity;
import com.example.android.mytodoapp.R;
import com.example.android.mytodoapp.tasks.SyncDataFromServerTask;
import com.example.android.mytodoapp.util.AlertUtils;
import com.example.android.mytodoapp.util.InternetUtils;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;


public class FragmentLogin extends Fragment {

    private EditText mUsername, mPassword;
    private ProgressDialog progressDialog;


    private void dismissProgressdialog() {
        try {
            progressDialog.dismiss();
        } catch (Exception e) {

        }
    }

    public static FragmentLogin getInstance() {
        return new FragmentLogin();
    }


    private void showAlert(String msg) {
        AlertDialog dialog = new AlertDialog.Builder(getActivity()).setIcon(0).setTitle("Alert").setMessage(msg).setNeutralButton("Ok", null).show();
        AlertUtils.changeDefaultColor(dialog);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_login, container, false);


        mUsername = (EditText) layout.findViewById(R.id.name);
        mPassword = (EditText) layout.findViewById(R.id.password);

        Button login = (Button) layout.findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAndLogin();
            }
        });

        return layout;
    }

    private void checkAndLogin() {

        String username = mUsername.getText().toString();
        if (username == null || username.isEmpty()) {
            showAlert("Please enter username");
            mUsername.requestFocus();
            return;
        }

        String password = mPassword.getText().toString();
        if (password == null || password.isEmpty()) {
            showAlert("Please enter password");
            mPassword.requestFocus();
            return;
        }

        if (!InternetUtils.isNetworkConnected(getActivity())) {
            InternetUtils.showInternetAlert(getActivity(), false);
            return;
        }

        progressDialog = ProgressDialog.show(getActivity(), "", "Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress_dialog));

        ParseUser.logInInBackground(username, password, new LogInCallback() {
            public void done(ParseUser user, ParseException e) {

                if (user != null) {
                    afterLoginSucess();
                } else {
                    // Signup failed. Look at the ParseException to see what happened.
                    dismissProgressdialog();
                    showAlert("" + e.getLocalizedMessage());
                    mUsername.requestFocus();
                }
            }
        });
    }

    // SyncDateFromServer fetch data from ParseServer and puts it in local database : internet is needed
    private void afterLoginSucess() {
        new SyncDataFromServerTask(getActivity(), false) {
            @Override
            protected synchronized void onWorkFinished(String result) {
                super.onWorkFinished(result);
                dismissProgressdialog();
                startActivity(new Intent(getActivity(), MyNavigationDrawerActivity.class));
                getActivity().finish();
            }
        }.start();
    }


}
