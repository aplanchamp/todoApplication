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
import com.parse.RequestPasswordResetCallback;

public class FragmentForgottenPassword extends Fragment {

    private EditText mEmail;
    private ProgressDialog progressDialog;
    private void dismissProgressdialog()
    {
        try
        {
            progressDialog.dismiss();
        }catch (Exception e)
        {

        }
    }


    public static FragmentForgottenPassword getInstance() {
        return new FragmentForgottenPassword();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_forgotten_password, container, false);

        mEmail = (EditText) layout.findViewById(R.id.email_password_forgotten);

        layout.findViewById(R.id.reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAndReset();
            }
        });

        return layout;
    }


    private void showAlert(String msg)
    {
        AlertDialog dialog = new AlertDialog.Builder(getActivity()).setIcon(0).setTitle("Alert").setMessage(msg).setNeutralButton("Ok", null).show();

        Button button = dialog.getButton(DialogInterface.BUTTON_NEUTRAL);
        button.setTextColor(getResources().getColor(R.color.colorPrimary));
    }

    private void checkAndReset() {

        String email = mEmail.getText().toString();
        if(email==null || email.isEmpty())
        {
            showAlert("Please enter email");
            mEmail.requestFocus();
            return;
        }

        if (!Util.isValidEmail(email)) {
            showAlert("Please enter valid email");
            mEmail.requestFocus();
            return;
        }
        if(!InternetUtils.isNetworkConnected(getActivity()))
        {
            InternetUtils.showInternetAlert(getActivity(), false);
            return;
        }

        progressDialog=ProgressDialog.show(getActivity(),"","Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress_dialog));

        ParseUser.requestPasswordResetInBackground(email, new RequestPasswordResetCallback() {
            @Override
            public void done(ParseException e) {
                dismissProgressdialog();
                if (e != null) {
                    e.printStackTrace();
                    showAlert(e.getLocalizedMessage());
                } else {
                    new AlertDialog.Builder(getActivity()).setIcon(0).setTitle("Success").setMessage("Please check your emails").setNeutralButton("Ok", new DialogInterface.OnClickListener() {
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