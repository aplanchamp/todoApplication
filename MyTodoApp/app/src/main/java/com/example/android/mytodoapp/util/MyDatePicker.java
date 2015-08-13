package com.example.android.mytodoapp.util;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * Created by aude on 07/07/2015.
 */

// implements DatePicker : dialog to choose a date with default OS dialog for picking dates

public class MyDatePicker {
    private DatePickerDialog dialog;

    public MyDatePicker(Context context, long minDate,long maxDate,long defaultDate,DatePickerDialog.OnDateSetListener listener) {

        // instanciates the calendar to the current date
        final Calendar c = Calendar.getInstance();

        // set the calendar c to default date given in the arguments
        c.setTimeInMillis(defaultDate);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // create a DatePickerDialog set to c (so default date)
        dialog = new DatePickerDialog(context, listener, year, month, day);
        DatePicker picker = dialog.getDatePicker();
        picker.setCalendarViewShown(false);
        picker.setSpinnersShown(true);


        // set default set in the dialog : to prevent user from picking dates before a certain date, use minDate

        if (minDate != -1) {
            picker.setMinDate(minDate);
        }
        if (maxDate != -1) {
            picker.setMaxDate(maxDate);
        }
    }

    public void show() {
        if (dialog != null) {
            dialog.show();
            AlertUtils.changeDefaultColor(dialog);
            ;
        }
    }
}
