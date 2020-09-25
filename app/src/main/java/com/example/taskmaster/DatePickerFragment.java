package com.example.taskmaster;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDialogFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DatePickerFragment extends AppCompatDialogFragment implements DatePickerDialog.OnDateSetListener {
    final Calendar calendar = Calendar.getInstance();

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        MainActivity ma = (MainActivity) getActivity();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        String selectedDate = new SimpleDateFormat(MainActivity.DATE_FORMAT, Locale.ENGLISH)
                .format(calendar.getTime()).replace('/', '-');
        ma.setUserData(selectedDate);
        ma.load(selectedDate + MainActivity.USERDATA_FILE_EXTENSION);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), DatePickerFragment.this, year, month, day);
    }
}
