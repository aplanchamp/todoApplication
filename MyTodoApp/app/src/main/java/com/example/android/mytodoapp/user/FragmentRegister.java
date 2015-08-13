package com.example.android.mytodoapp.user;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.android.mytodoapp.R;
import com.example.android.mytodoapp.util.InternetUtils;
import com.example.android.mytodoapp.util.Util;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class FragmentRegister extends Fragment {


    private EditText mNameUser, mPassword, mEmailUser, mPasswordVerify;
    private ProgressDialog progressDialog;


    private void dismissProgressdialog() {
        try {
            progressDialog.dismiss();
        } catch (Exception e) {

        }
    }

    public static FragmentRegister getInstance() {
        return new FragmentRegister();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_register, container, false);



        mNameUser = (EditText) layout.findViewById(R.id.edit_name);
        mPassword = (EditText) layout.findViewById(R.id.edit_password);
        mPasswordVerify = (EditText) layout.findViewById(R.id.edit_password_verify);
        mEmailUser = (EditText) layout.findViewById(R.id.email_address);

        Button mButtonConfirm = (Button) layout.findViewById(R.id.sign_up);
        mButtonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAndSignup();
            }
        });

        return layout;
    }


    private void showAlert(String msg) {
        AlertDialog dialog = new AlertDialog.Builder(getActivity()).setIcon(0).setTitle("Alert").setMessage(msg).setNeutralButton("Ok", null).show();
        Button button = dialog.getButton(DialogInterface.BUTTON_NEUTRAL);
        button.setTextColor(getResources().getColor(R.color.colorPrimary));
    }

    private void checkAndSignup() {

        String username = mNameUser.getText().toString();
        if (username == null || username.isEmpty()) {
            showAlert("Please enter username");
            mNameUser.requestFocus();
            return;
        }

        String email = mEmailUser.getText().toString();
        if (email == null || email.isEmpty()) {
            showAlert("Please enter email");
            mEmailUser.requestFocus();
            return;
        }
        if (!Util.isValidEmail(email)) {
            showAlert("Please enter valid email");
            mEmailUser.requestFocus();
            return;
        }

        String password = mPassword.getText().toString();
        if (password == null || password.isEmpty()) {
            showAlert("Please enter password");
            mPassword.requestFocus();
            return;
        }
        if (password.length() < 6) {
            showAlert("Password should be at least 6 characters long");
            mPassword.requestFocus();
            return;
        }
        String confirmPassword = mPasswordVerify.getText().toString();
        if (confirmPassword == null || confirmPassword.isEmpty()) {
            showAlert("Please enter confirm password");
            mPasswordVerify.requestFocus();
            return;
        }
        if (!confirmPassword.equals(password)) {
            showAlert("Password and confirm password does not match");
            mPassword.requestFocus();
            return;
        }

        if (!InternetUtils.isNetworkConnected(getActivity())) {
            InternetUtils.showInternetAlert(getActivity(), false);
            return;
        }

        progressDialog=ProgressDialog.show(getActivity(),"","Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress_dialog));

        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);


        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                dismissProgressdialog();
                if (e != null) {
                    e.printStackTrace();
                    showAlert(e.getLocalizedMessage());
                } else {
                    new AlertDialog.Builder(getActivity()).setIcon(0).setTitle("Success").setMessage("You successfully signup with your details. You can login now").setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(getActivity(), TabUserActivity.class));
                        }
                    }).setCancelable(false).show();
                }
            }
        });

    }


}
