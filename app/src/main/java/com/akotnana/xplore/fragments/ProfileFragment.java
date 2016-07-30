package com.akotnana.xplore.fragments;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.akotnana.xplore.R;
import com.wefika.flowlayout.FlowLayout;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

public class ProfileFragment extends Fragment {

    private static String TAG = "ProfileFragment";

    private OnFragmentInteractionListener mListener;
    String[] OCCUPATIONLIST = {"None", "Student", "Professional", "Other"};

    private FlowLayout flowLayout;
    private ImageButton addButton;
    private Button tempButton;
    private EditText nextInterest;
    private String interestName;

    public ProfileFragment() {
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
        View inflated =  inflater.inflate(R.layout.fragment_profile, container, false);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_dropdown_item_1line, OCCUPATIONLIST);
        MaterialBetterSpinner materialDesignSpinner = (MaterialBetterSpinner)
                inflated.findViewById(R.id.android_material_design_spinner);
        materialDesignSpinner.setAdapter(arrayAdapter);

        flowLayout = (FlowLayout) inflated.findViewById(R.id.interest_layout);
        addButton = (ImageButton) inflated.findViewById(R.id.add_button);
        tempButton = (Button) inflated.findViewById(R.id.temp_button);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addInterestToLayout();
            }
        });

        return inflated;
    }

    public void addInterestToLayout() {
        MaterialDialog dialog = new MaterialDialog.Builder(getContext())
                .title("Add Interest")
                .customView(R.layout.search_simple, false)
                .positiveText("Add")
                .negativeText("Cancel")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        interestName = nextInterest.getText().toString();
                        LinearLayout view = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.interest_button, null);
                        final Button button = (Button) view.findViewById(R.id.temp_button);
                        FlowLayout.LayoutParams layoutParams = new FlowLayout.LayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT, FlowLayout.LayoutParams.WRAP_CONTENT);
                        layoutParams.setMargins(10,10,10,10);
                        button.setPadding(43,43,43,43);
                        button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                        button.setLayoutParams(layoutParams);
                        view.removeView(button);
                        Log.d(TAG, addButton.getHeight() + "");
                        button.setText(interestName);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new MaterialDialog.Builder(getContext())
                                        .title("Delete Interest")
                                        .content("Are you sure you want to delete that interest?")
                                        .positiveText("Delete")
                                        .negativeText("Cancel")
                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                flowLayout.removeView(button);
                                            }
                                        })
                                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                dialog.cancel();
                                            }
                                        })
                                        .show();
                            }
                        });

                        /*
                        final Button button = new Button(getContext());
                        button.setText(interestName);
                        button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        );
                        int s = dpToPx(5);
                        params.setMargins(s, s, s, s);
                        button.setGravity(Gravity.CENTER);
                        button.setLayoutParams(params);
                        button.setClickable(true);
                        button.setBackgroundResource(R.drawable.rounded_button);
                        button.setIncludeFontPadding(false);
                        */

                        flowLayout.addView(button, 0);


                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.cancel();
                    }
                })
                .build();
        View inflated = dialog.getCustomView();
        nextInterest = (EditText) inflated.findViewById(R.id.next_interest);
        dialog.setCancelable(false);
        dialog.show();
    }

    public int pxToDp(int px) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
