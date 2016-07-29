package com.akotnana.xplore.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.akotnana.xplore.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.maps.android.SphericalUtil;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;

public class DashboardFragment extends Fragment implements OnMapReadyCallback {

    private OnFragmentInteractionListener mListener;
    private static final int MESSAGE_ID_SAVE_CAMERA_POSITION = 1;
    private static final int MESSAGE_ID_READ_CAMERA_POSITION = 2;
    private static int reloadedTimes = 0;
    private static CameraPosition lastCameraPosition;
    private static Location userLoc;
    private Handler handler;
    private EditText et;
    private DiscreteSeekBar discreteSeekBar1;
    private MapView mMapView;
    private static GoogleMap mMap;
    private Bundle mBundle;
    private Toast mToast;

    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBundle = savedInstanceState;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_dashboard, container, false);
        try {
            MapsInitializer.initialize(getActivity());
        } catch (Exception e) {
            Log.e("mapview", "", e);
        }

        Button search = (Button) inflatedView.findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayDialog(v);
            }
        });

        Button current = (Button) inflatedView.findViewById(R.id.currentLoc);
        current.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnToCurrent(v);
            }
        });

        Button toggle = (Button) inflatedView.findViewById(R.id.toggle);
        toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleMode(v);
            }
        });

        mMapView = (MapView) inflatedView.findViewById(R.id.map);
        mMapView.onCreate(mBundle);
        setUpMapIfNeeded(inflatedView);
        String intermediate = (getData("callsMade"));
        if(intermediate.equals("")) {
            reloadedTimes = 0;
            storeData("callsMade", Integer.toString(reloadedTimes));
            storeData("lastRetrieved", Long.toString(System.currentTimeMillis()));
            Log.d("DashboardFragment", "create callsMade");
            Log.d("DashboardFragment", getData("lastRetrieved"));
        } else {
            reloadedTimes = Integer.parseInt(intermediate);
            Log.d("DashboardFragment", "stored reloaded times");
        }
        return inflatedView;
    }

    private void toggleMode(View v) {
        if(mMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL) {
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        } else if(mMap.getMapType() == GoogleMap.MAP_TYPE_SATELLITE) {
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        } else if(mMap.getMapType() == GoogleMap.MAP_TYPE_HYBRID) {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        } else {}
    }

    private void displayDialog(View v) {
        boolean wrapInScrollView = true;
        MaterialDialog dialog = new MaterialDialog.Builder(getContext())
                .title("Search Events")
                .customView(R.layout.search_dialog, wrapInScrollView)
                .positiveText("Search")
                .negativeText("Cancel")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        showToast("Query: " + et.getText().toString() + " and range: " + discreteSeekBar1.getProgress());
                        //get data here
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
        discreteSeekBar1 = (DiscreteSeekBar) inflated.findViewById(R.id.sliderLayout);
        discreteSeekBar1.setNumericTransformer(new DiscreteSeekBar.NumericTransformer() {
            @Override
            public int transform(int value) {
                return (int) (value * 2.5d);
            }
        });
        et = (EditText) inflated.findViewById(R.id.query);
        dialog.setCancelable(false);
        dialog.show();
    }

    private void returnToCurrent(View v) {
        SmartLocation.with(getContext()).location()
                .oneFix()
                .start(new OnLocationUpdatedListener() {
                    @Override
                    public void onLocationUpdated(Location location) {
                        userLoc = location;
                        Log.d("Got location", location.toString());
                    }
                });

        new Thread(new Runnable() {
            public void run() {
                while(userLoc == null ) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                LatLng newLatLng = new LatLng(userLoc.getLatitude(), userLoc.getLongitude());
                LatLngBounds bounds = new LatLngBounds.Builder().
                        include(SphericalUtil.computeOffset(newLatLng, 5 * 1609.344d, 0)).
                        include(SphericalUtil.computeOffset(newLatLng, 5 * 1609.344d, 90)).
                        include(SphericalUtil.computeOffset(newLatLng, 5 * 1609.344d, 180)).
                        include(SphericalUtil.computeOffset(newLatLng, 5 * 1609.344d, 270)).build();
                final CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 0);
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        mMap.animateCamera(cameraUpdate);
                    }
                });
            }
        }).start();
    }

    private void showToast(String message) {
        if (mToast != null) {
            mToast.cancel();
            mToast = null;
        }
        mToast = Toast.makeText(getContext(), message, Toast.LENGTH_SHORT);
        mToast.show();
    }

    private void setUpMapIfNeeded(View inflatedView) {
        ((MapView) inflatedView.findViewById(R.id.map)).getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Log.d("DashboardFragment", "map ready");

        SmartLocation.with(getContext()).location()
                .oneFix()
                .start(new OnLocationUpdatedListener() {
                    @Override
                    public void onLocationUpdated(Location location) {
                        userLoc = location;
                        Log.d("Got location", location.toString());
                    }
                });

        new Thread(new Runnable() {
            public void run() {
                while(userLoc == null ) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                LatLng newLatLng = new LatLng(userLoc.getLatitude(), userLoc.getLongitude());
                LatLngBounds bounds = new LatLngBounds.Builder().
                        include(SphericalUtil.computeOffset(newLatLng, 5 * 1609.344d, 0)).
                        include(SphericalUtil.computeOffset(newLatLng, 5 * 1609.344d, 90)).
                        include(SphericalUtil.computeOffset(newLatLng, 5 * 1609.344d, 180)).
                        include(SphericalUtil.computeOffset(newLatLng, 5 * 1609.344d, 270)).build();
                final CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 0);
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        mMap.moveCamera(cameraUpdate);
                    }
                });
            }
        }).start();

        handler = new MapStateHandler();

        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                handler.removeMessages(MESSAGE_ID_SAVE_CAMERA_POSITION);
                handler.removeMessages(MESSAGE_ID_READ_CAMERA_POSITION);
                handler.sendEmptyMessageDelayed(MESSAGE_ID_SAVE_CAMERA_POSITION, 500);
                handler.sendEmptyMessageDelayed(MESSAGE_ID_READ_CAMERA_POSITION, 1000);
            }
        });
    }
    public void storeData(String key, String value) {
        SharedPreferences.Editor editor = getContext().getSharedPreferences("XPLORE_PREFS", Context.MODE_PRIVATE).edit();
        editor.putString(key,value);
        editor.apply();
    }

    public String getData(String key) {
        return getContext().getSharedPreferences("XPLORE_PREFS",Context.MODE_PRIVATE).getString(key, "");
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
        storeData("callsMade", Integer.toString(reloadedTimes));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onStop() {
        super.onStop();
        storeData("callsMade", Integer.toString(reloadedTimes));
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private double getMapRadius(GoogleMap googleMap) {
        VisibleRegion vr = mMap.getProjection().getVisibleRegion();
        double left = vr.latLngBounds.southwest.longitude;
        double top = vr.latLngBounds.northeast.latitude;
        double right = vr.latLngBounds.northeast.longitude;
        double bottom = vr.latLngBounds.southwest.latitude;

        Location center=new Location("center");
        center.setLatitude(vr.latLngBounds.getCenter().latitude);
        center.setLongitude(vr.latLngBounds.getCenter().longitude);
        Location middleLeft = new Location("middleLeftCenter");
        middleLeft.setLatitude(center.getLatitude());
        middleLeft.setLongitude(left);

        float dis = (center.distanceTo(middleLeft));//calculate distane between middleLeftcorner and center

        dis = getMiles(dis);

        return roundToHalf(dis);
    }

    public float roundToHalf(float d) {
        return Math.round(d * 2f) / 2.0f;
    }

    public float getMiles(float i) {
        return i*0.000621371192f;
    }

    public float getMeters(float i) {
        return i*1609.344f;
    }

    class MapStateHandler extends Handler {
        public void handleMessage(Message msg) {
            if (msg.what == MESSAGE_ID_SAVE_CAMERA_POSITION) {
                lastCameraPosition = mMap.getCameraPosition();
            } else if (msg.what == MESSAGE_ID_READ_CAMERA_POSITION) {
                if (lastCameraPosition.equals(mMap.getCameraPosition())) {
                    if(reloadedTimes % 6 == 0 && reloadedTimes > 0) {
                        long current = System.currentTimeMillis();
                        long prev = Long.parseLong(getData("lastRetrieved"));
                        Log.d("DashboardFragment", "measured: " + Long.toString(prev));
                        Log.d("DashboardFragment", "diff: " + Long.toString(current - prev));
                        if(current - prev <= 60.0 * 1000.0) {
                            Log.d("DashboardFragment", "too many requests");
                            storeData("isPaused", "true");
                            storeData("lastRetrieved", Long.toString(current + 60 * 1000));
                            reloadedTimes += 1;
                        } else {
                            storeData("lastRetrieved", Long.toString(current));
                            Log.d("DashboardFragment", "new last retrieved: " + Long.toString(current));
                        }
                    }
                    if(getData("isPaused").equals("true")) {
                        long current = System.currentTimeMillis();
                        long prev = Long.parseLong(getData("lastRetrieved"));
                        if(current >= prev) {
                            storeData("isPaused", "false");
                        } else {
                            showToast("Slow down! Please wait " + (((int) ((prev - current) / 1000))) + " more seconds.");
                        }
                    }
                    if(!getData("isPaused").equals("true")) {
                        reloadedTimes += 1;
                        Log.d("DashboardFragment", "Camera position stable, retrieved " + Integer.toString(reloadedTimes) + ", with radius of " + Double.toString(getMapRadius(mMap)));
                        //get data here
                    }
                }
            }
        }
    }


}

