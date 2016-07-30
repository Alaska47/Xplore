package com.akotnana.xplore.fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.akotnana.xplore.R;
import com.borax12.materialdaterangepicker.date.DatePickerDialog;
import com.borax12.materialdaterangepicker.time.RadialPickerLayout;
import com.borax12.materialdaterangepicker.time.TimePickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateSmallFragment extends Fragment implements
        DatePickerDialog.OnDateSetListener,TimePickerDialog.OnTimeSetListener {

    private OnFragmentInteractionListener mListener;

    private EditText dateText1;
    private EditText timeText1;
    private EditText dateText2;
    private EditText timeText2;

    public DateSmallFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_small_date, container, false);

        dateText1 = (EditText) v.findViewById(R.id.input_from_date);
        timeText1 = (EditText) v.findViewById(R.id.input_from_time);
        dateText2 = (EditText) v.findViewById(R.id.input_to_date);
        timeText2 = (EditText) v.findViewById(R.id.input_to_time);

        dateText1.setKeyListener(null);
        timeText1.setKeyListener(null);
        dateText2.setKeyListener(null);
        timeText2.setKeyListener(null);

        TextInputLayout dateButton1 = (TextInputLayout) v.findViewById(R.id.input_layout_from_date);
        TextInputLayout timeButton1 = (TextInputLayout) v.findViewById(R.id.input_layout_from_time);
        TextInputLayout dateButton2 = (TextInputLayout) v.findViewById(R.id.input_layout_to_date);
        TextInputLayout timeButton2 = (TextInputLayout) v.findViewById(R.id.input_layout_to_time);

        View.OnClickListener dateListen = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = null;
                if(dateText1.getText().toString().equals("None") && dateText2.getText().toString().equals("None")) {
                    Calendar now = Calendar.getInstance();
                    dpd = DatePickerDialog.newInstance(
                            DateSmallFragment.this,
                            now.get(Calendar.YEAR),
                            now.get(Calendar.MONTH),
                            now.get(Calendar.DAY_OF_MONTH)
                    );
                } else {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM d yyyy");
                    if(!dateText1.getText().toString().equals("None")) {
                        Calendar now = Calendar.getInstance();
                        try {
                            now.setTime(dateFormat.parse(dateText1.getText().toString()));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        dpd = DatePickerDialog.newInstance(
                                DateSmallFragment.this,
                                now.get(Calendar.YEAR),
                                now.get(Calendar.MONTH),
                                now.get(Calendar.DAY_OF_MONTH)
                        );
                    }else if(!dateText2.getText().toString().equals("None")) {
                        Calendar now = Calendar.getInstance();
                        try {
                            now.setTime(dateFormat.parse(dateText2.getText().toString()));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        dpd = DatePickerDialog.newInstance(
                                DateSmallFragment.this,
                                now.get(Calendar.YEAR),
                                now.get(Calendar.MONTH),
                                now.get(Calendar.DAY_OF_MONTH)
                        );
                    }
                }
                dpd.show(getActivity().getFragmentManager(), "Datepickerdialog");
            }
        };

        View.OnClickListener timeListen = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog tpd = null;
                if(timeText1.getText().toString().equals("None") && timeText2.getText().toString().equals("None")) {
                    Calendar now = Calendar.getInstance();
                    tpd = TimePickerDialog.newInstance(
                            DateSmallFragment.this,
                            now.get(Calendar.HOUR_OF_DAY),
                            now.get(Calendar.MINUTE),
                            false
                    );
                } else {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm aaa");
                    if(!timeText1.getText().toString().equals("None")) {
                        Calendar now = Calendar.getInstance();
                        try {
                            now.setTime(dateFormat.parse(timeText1.getText().toString()));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        tpd = TimePickerDialog.newInstance(
                                DateSmallFragment.this,
                                now.get(Calendar.HOUR_OF_DAY),
                                now.get(Calendar.MINUTE),
                                false
                        );
                    }else if(!timeText2.getText().toString().equals("None")) {
                        Calendar now = Calendar.getInstance();
                        try {
                            now.setTime(dateFormat.parse(timeText2.getText().toString()));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        tpd = TimePickerDialog.newInstance(
                                DateSmallFragment.this,
                                now.get(Calendar.HOUR_OF_DAY),
                                now.get(Calendar.MINUTE),
                                false
                        );
                    }
                }
                tpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        Log.d("TimePicker", "Dialog was cancelled");
                    }
                });
                tpd.show(getActivity().getFragmentManager(), "Timepickerdialog");
            }
        };

        dateButton1.setOnClickListener(dateListen);
        dateButton1.setOnClickListener(dateListen);
        dateText1.setOnClickListener(dateListen);
        dateText2.setOnClickListener(dateListen);

        timeButton1.setOnClickListener(timeListen);
        timeButton2.setOnClickListener(timeListen);
        timeText1.setOnClickListener(timeListen);
        timeText2.setOnClickListener(timeListen);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        DatePickerDialog dpd = (DatePickerDialog) getActivity().getFragmentManager().findFragmentByTag("Datepickerdialog");
        if(dpd != null) dpd.setOnDateSetListener(this);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth,int yearEnd, int monthOfYearEnd, int dayOfMonthEnd) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM d yyyy");
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, monthOfYear);
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        dateText1.setText(dateFormat.format(cal.getTime()));
        cal.set(Calendar.YEAR, yearEnd);
        cal.set(Calendar.MONTH, monthOfYearEnd);
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonthEnd);
        dateText2.setText(dateFormat.format(cal.getTime()));
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int hourOfDayEnd, int minuteEnd) {
        String hourString = hourOfDay < 10 ? "0"+hourOfDay : ""+hourOfDay;
        String minuteString = minute < 10 ? "0"+minute : ""+minute;
        String hourStringEnd = hourOfDayEnd < 10 ? "0"+hourOfDayEnd : ""+hourOfDayEnd;
        String minuteStringEnd = minuteEnd < 10 ? "0"+minuteEnd : ""+minuteEnd;
        SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm aaa");
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hourString));
        cal.set(Calendar.MINUTE, Integer.parseInt(minuteString));
        timeText1.setText(dateFormat.format(cal.getTime()));
        cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hourStringEnd));
        cal.set(Calendar.MINUTE, Integer.parseInt(minuteStringEnd));
        timeText2.setText(dateFormat.format(cal.getTime()));
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
