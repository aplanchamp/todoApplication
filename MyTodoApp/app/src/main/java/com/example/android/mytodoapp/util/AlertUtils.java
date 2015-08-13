package com.example.android.mytodoapp.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.mytodoapp.R;


/**
 * Created by irishapps on 02/07/2015.
 */

// set colors for each AlertDialog

public class AlertUtils {

    public static  void showAlert(Context context,String msg)
    {
        AlertDialog dialog=new AlertDialog.Builder(context).setIcon(0).setTitle("Alert").setMessage(msg).setNeutralButton("Ok", null).show();
        changeDefaultColor(dialog);
    }


    public static void changeDefaultColor(AlertDialog dialog) {
        try {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // only for gingerbread and newer versions
                Button b = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                if (b != null)
                    b.setTextColor(dialog.getContext().getResources()
                            .getColor(R.color.colorPrimaryText));

                b = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                if (b != null)
                    b.setTextColor(dialog.getContext().getResources()
                            .getColor(R.color.colorPrimaryText));

                b = dialog.getButton(DialogInterface.BUTTON_NEUTRAL);
                if (b != null)
                    b.setTextColor(dialog.getContext().getResources()
                            .getColor(R.color.colorPrimaryText));
            } else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                ViewGroup decorView = (ViewGroup) dialog.getWindow()
                        .getDecorView();
                FrameLayout windowContentView = (FrameLayout) decorView
                        .getChildAt(0);
                FrameLayout contentView = (FrameLayout) windowContentView
                        .getChildAt(0);
                LinearLayout parentPanel = (LinearLayout) contentView
                        .getChildAt(0);
                LinearLayout topPanel = (LinearLayout) parentPanel
                        .getChildAt(0);
                View titleDivider = topPanel.getChildAt(2);
                LinearLayout titleTemplate = (LinearLayout) topPanel
                        .getChildAt(1);
                TextView alertTitle = (TextView) titleTemplate.getChildAt(1);

                int textColor = dialog.getContext().getResources()
                        .getColor(R.color.colorPrimaryText);
                alertTitle.setTextColor(textColor);

                int primaryColor = dialog.getContext().getResources()
                        .getColor(R.color.colorPrimaryText);
                titleDivider.setBackgroundColor(primaryColor);
            }
            else
            {
            }

        } catch (Exception e) {
             e.printStackTrace();

        }
    }
}
