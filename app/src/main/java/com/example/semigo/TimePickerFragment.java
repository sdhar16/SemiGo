package com.example.semigo;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TimePicker;

import androidx.fragment.app.DialogFragment;

import java.sql.Time;
import java.util.Calendar;

public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    public  interface OnTimeCompleteListener {
        void onTimeComplete(String time, int tag);

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        TimePickerDialog timePickerDialog  = new TimePickerDialog(getActivity(),  this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));

        return  timePickerDialog;
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user
        TimePickerFragment.OnTimeCompleteListener mListener = (TimePickerFragment.OnTimeCompleteListener) getParentFragment();
        Bundle bundle = getArguments();
        String check = bundle.get("tag").toString();
        Log.d("main", check);
        String time = "";
        time += hourOfDay + ":";
        if(minute / 10 !=0)
        {
            time += minute;
        }
        else
        {
            time += "0" + minute;
        }
        mListener.onTimeComplete(time, Integer.parseInt(check));
        this.dismiss();
    }
}