package com.example.HarIPart;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.DataBase.sqlConnector;
import com.example.ladyapp.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.SphericalUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;


public class regularUserUnsafeMapView extends Fragment {

    ArrayList<LatLngDistanceKeeper> LatLngs = new ArrayList<>();

    private ImageView existingImageView;
    private TextView existingTextView;

    private int locationUpdateCount;
    FusedLocationProviderClient fusedLocationProviderClient;

    private GeofencingClient geofencingClient;
    private float GEOFENCE_RADIUS = 75;
    private MakeMeGeofences geofenceHelper;
    private static final String REASONSFILE = "reasons_file";
    private static final String NOTIFDISABLEDFILE = "notif_is_disabled";
    private GoogleMap googleMapObjectForWork;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_regular_user_unsafe_map_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Getting zone descriptions ready for the notifications
        try {
            sqlConnector connect = new sqlConnector();
            String result = connect.getUnsafeReasons();
            connect.closeConnection();

            //Check if the zonereasons file exists, and if it does, load the files in
            File reasonsFile = new File(requireContext().getFilesDir(), REASONSFILE);
            if (reasonsFile.exists()) {
                reasonsFile.delete();
                try (FileOutputStream fos = requireContext().openFileOutput(REASONSFILE, Context.MODE_PRIVATE)) {
                    fos.write(result.getBytes());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                try (FileOutputStream fos = requireContext().openFileOutput(REASONSFILE, Context.MODE_PRIVATE)) {
                    fos.write(result.getBytes());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        //Initializing the Map here
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.RegularMap);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

        //Getting existing imageview and textview:
        this.existingImageView = view.findViewById(R.id.UnsafeZoneDescription);
        this.existingTextView = view.findViewById(R.id.UnsafeZoneDescriptionText);

        //Making them invisible:
        existingImageView.setVisibility(View.GONE);
        existingTextView.setVisibility(View.GONE);

        //Getting map location ready:
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(view.getContext());

        //Getting the geofencing client ready
        geofencingClient = LocationServices.getGeofencingClient(requireContext());
        geofenceHelper = new MakeMeGeofences(requireContext());

        //Adding button callback to mark unsafe zone
        Button markUnsafeButton = view.findViewById(R.id.MarkUnsafeButton);
        markUnsafeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Bringing up the reasons panels
                ImageView unsafeReasonBanner = view.findViewById(R.id.unsafeMarkRequestReasonRegular);
                TextView unsafeReasonText = view.findViewById(R.id.textView12);
                EditText reasonEntry = view.findViewById(R.id.regularUserUnsafeReason);
                Button reasonEntryButton = view.findViewById(R.id.continueButtonReasonAddingUnsafeZoneRegular);
                Button cancelButton = view.findViewById(R.id.cancelButtonReasonAddingUnsafeZoneRegular);
                unsafeReasonBanner.setVisibility(View.VISIBLE);
                unsafeReasonBanner.bringToFront();
                unsafeReasonText.setVisibility(View.VISIBLE);
                unsafeReasonText.bringToFront();
                reasonEntry.setVisibility(View.VISIBLE);
                reasonEntry.bringToFront();
                reasonEntryButton.setVisibility(View.VISIBLE);
                reasonEntryButton.bringToFront();
                cancelButton.setVisibility(View.VISIBLE);
                cancelButton.bringToFront();

                //if cancel pressed
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //clear text in reason edit text
                        reasonEntry.getText().clear();

                        //Making everything invisible again
                        unsafeReasonBanner.setVisibility(View.GONE);
                        unsafeReasonText.setVisibility(View.GONE);
                        reasonEntry.setVisibility(View.GONE);
                        reasonEntryButton.setVisibility(View.GONE);
                        cancelButton.setVisibility(View.GONE);
                    }
                });

                //if continue pressed
                reasonEntryButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Obtaining Reason Entered
                        String Reason = reasonEntry.getText().toString();

                        //Clearing whatever old text was stored
                        reasonEntry.getText().clear();

                        //make everything invisible again
                        unsafeReasonBanner.setVisibility(View.GONE);
                        unsafeReasonText.setVisibility(View.GONE);
                        reasonEntry.setVisibility(View.GONE);
                        reasonEntryButton.setVisibility(View.GONE);
                        cancelButton.setVisibility(View.GONE);

                        //Adding to the zone requests
                        //finding current latlng of position pointed by user
                        LatLng point = googleMapObjectForWork.getCameraPosition().target;

                        //Send information to the database
                        try {
                            sqlConnector connect = new sqlConnector();
                            connect.insertNewUnsafeZoneRequest(point.latitude, point.longitude, Reason);
                            connect.closeConnection();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        //Make the final banner visible again
                        ImageView unsafeBanner = view.findViewById(R.id.UnsafeMarkRequestPopup);
                        TextView unsafeText = view.findViewById(R.id.UnsafeMarkRequestText);
                        Button unsafeZoneButton = view.findViewById(R.id.okButtonUnsafeZoneAddedRegular);
                        unsafeBanner.setVisibility(View.VISIBLE);
                        unsafeBanner.bringToFront();
                        unsafeText.setVisibility(View.VISIBLE);
                        unsafeText.bringToFront();
                        unsafeZoneButton.setVisibility(View.VISIBLE);
                        unsafeZoneButton.bringToFront();

                        unsafeZoneButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                unsafeBanner.setVisibility(View.GONE);
                                unsafeText.setVisibility(View.GONE);
                                unsafeZoneButton.setVisibility(View.GONE);
                            }
                        });
                    }
                });
            }
        });

        //Adding a file to act as the setting which shows whether notifications are enabled or not
        //Handling notifications here
        Button notifButton = view.findViewById(R.id.NotifButton);
        notifButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Making everything visible first
                ImageView notifsettingpopup = view.findViewById(R.id.NotifSettingPopUp);
                TextView textview8 = view.findViewById(R.id.textView8);
                CheckBox notifsettingcheckbox = view.findViewById(R.id.NotifSettingCheckBox);
                Button okbuttonNotifs = view.findViewById(R.id.okButtonNotifications);
                notifsettingpopup.setVisibility(View.VISIBLE);
                notifsettingpopup.bringToFront();
                textview8.setVisibility(View.VISIBLE);
                textview8.bringToFront();
                notifsettingcheckbox.setVisibility(View.VISIBLE);
                notifsettingcheckbox.bringToFront();
                okbuttonNotifs.setVisibility(View.VISIBLE);
                okbuttonNotifs.bringToFront();

                //Setting checkbox based on whether the notif_is_disabled file exists
                //if notif_is_disabled exists, then no notifications
                //else, notifications will be sent
                File notifFile = new File(requireContext().getFilesDir(), NOTIFDISABLEDFILE);
                if (!notifFile.exists()) {
                    notifsettingcheckbox.setChecked(true);
                }
                else {
                    notifsettingcheckbox.setChecked(false);
                }

                //Now, when ok button clicked, on the basis of how it was earlier, the setting will be dealt with
                okbuttonNotifs.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (notifsettingcheckbox.isChecked()) {
                            if (notifFile.exists()) {
                                notifFile.delete();
                            }
                        }
                        else {
                            if (!notifFile.exists()) {
                                try (FileOutputStream fos = requireContext().openFileOutput(NOTIFDISABLEDFILE, Context.MODE_PRIVATE)) {
                                    String filler = "if this file exists, notifications will not be displayed";
                                    fos.write(filler.getBytes());
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        //Make everything disappear
                        notifsettingpopup.setVisibility(View.GONE);
                        textview8.setVisibility(View.GONE);
                        notifsettingcheckbox.setVisibility(View.GONE);
                        okbuttonNotifs.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(@NonNull GoogleMap googleMap) {

            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 99);
            }

            //Adding googlemap to instance variable
            googleMapObjectForWork = googleMap;

            //Defaulting to FSKTM at first
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(3.128131, 101.650625), 17));

            //This ensures that when the section loads, it waits a bit and immediately locks onto the current location at first
            //the jankiest solutions give the nicest results - hvepyc lmao
            googleMap.setMyLocationEnabled(true);
            locationUpdateCount = 0;
            googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                @SuppressLint("MissingPermission")
                @Override
                public void onMyLocationChange(@NonNull Location location) {
                    if (locationUpdateCount < 1) {
                        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 17));
                            }
                        });
                        locationUpdateCount++;
                    }
                }
            });

            //Getting unsafe area coords from table
            ArrayList<unsafeCoordsFromDB> unsafecoords = new ArrayList<>();
            try {
                sqlConnector connect = new sqlConnector();
                unsafecoords = connect.getUnsafeCoords();
                connect.closeConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            if (unsafecoords.size() != 0) {
                for (unsafeCoordsFromDB i: unsafecoords) {
                    //Adding new LatLngs object
                    LatLng toadd = new LatLng(i.getX(), i.getY());

                    //creating new imageview and textview for this
                    HashMap<ImageView, Integer> a = returnNewImageView();
                    HashMap<TextView, Integer> b = returnNewTextView();
                    HashMap.Entry<ImageView, Integer> entry = a.entrySet().iterator().next();
                    ImageView IVforthisLatLong = entry.getKey();
                    int IDofIV = entry.getValue();
                    HashMap.Entry<TextView, Integer> entry2 = b.entrySet().iterator().next();
                    TextView TVforthisLatLong = entry2.getKey();
                    int IDofTV = entry2.getValue();

                    //Add to LatLngs arraylist
                    LatLngs.add(new LatLngDistanceKeeper(toadd, i.getReasonForMarked() , IVforthisLatLong, IDofIV, TVforthisLatLong, IDofTV));

                    //draws circle there
                    googleMap.addCircle(new CircleOptions().center(toadd).radius(GEOFENCE_RADIUS).fillColor(Color.argb(62, 255, 0, 0)).strokeWidth(0));

                    //adds a geofence in that location
                    addGeofence(toadd, GEOFENCE_RADIUS, i.getID());
                }
            }

            googleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
                @Override
                public void onCameraMove() {

                    LatLng location = googleMap.getCameraPosition().target;
                    if (LatLngs.size()>0) {
                        //Keep updating all distance values between all spheres for everything

                        for (LatLngDistanceKeeper i: LatLngs) {
                            //First sets distance
                            double dist = SphericalUtil.computeDistanceBetween(location, i.getLatLng());
                            i.setDistance(dist);

                            //Then checks if anything is in range
                            if (i.getDistance() < GEOFENCE_RADIUS) {

                                ConstraintLayout constraintLayout = requireView().findViewById(R.id.regularMapViewConstraintLayout);
//                                System.out.println("You are in range"+i.returnCounter());
                                if (doesImageViewWithThisIDExist(i.getIVID()) == false && doesTextViewWithThisIDExist(i.getTVID()) == false) {
                                    ImageView newIV = i.getIV();
                                    constraintLayout.addView(newIV);
                                    newIV.bringToFront();
                                    newIV.setVisibility(View.VISIBLE);
                                    TextView newTV = i.getTV();
                                    String toShow = "Reason: "+i.getStoredText();
                                    newTV.setText(toShow);
                                    constraintLayout.addView(newTV);
                                    newTV.bringToFront();
                                    newTV.setVisibility(View.VISIBLE);
                                }
                                else {
                                    ImageView existingIV = returnImageViewWithThisID(i.getIVID());
                                    TextView existingTV = returnTextViewWithThisID(i.getTVID());
                                    existingIV.setVisibility(View.VISIBLE);
                                    existingTV.setVisibility(View.VISIBLE);
                                }
                            }
                            else {

                                if (doesImageViewWithThisIDExist(i.getIVID()) && doesTextViewWithThisIDExist(i.getTVID())) {
                                    ImageView existingIV = returnImageViewWithThisID(i.getIVID());
                                    TextView existingTV = returnTextViewWithThisID(i.getTVID());
                                    existingIV.setVisibility(View.GONE);
                                    existingTV.setVisibility(View.GONE);
                                }
                            }
                        }
                    }
                }
            });
        }
    };

    private HashMap<ImageView,Integer> returnNewImageView() {

        //For imageview first:
        ImageView newImageView = new ImageView(requireContext());
        int newID = View.generateViewId();
        newImageView.setId(newID);
        newImageView.setImageResource(R.drawable.baseline_format_bold_24);
        newImageView.setBackgroundColor(getResources().getColor(R.color.ladypink));
        ConstraintLayout.LayoutParams existingImageLayoutParams = (ConstraintLayout.LayoutParams) existingImageView.getLayoutParams();
        ConstraintLayout.LayoutParams newImageLayoutParams = new ConstraintLayout.LayoutParams(existingImageLayoutParams.width, existingImageLayoutParams.height);

        //Copying all old layout params over
        newImageLayoutParams.width = existingImageLayoutParams.width;
        newImageLayoutParams.height = existingImageLayoutParams.height;
        newImageLayoutParams.bottomToBottom = existingImageLayoutParams.bottomToBottom;
        newImageLayoutParams.endToEnd = existingImageLayoutParams.endToEnd;
        newImageLayoutParams.horizontalBias = existingImageLayoutParams.horizontalBias;
        newImageLayoutParams.startToStart = existingImageLayoutParams.startToStart;
        newImageLayoutParams.topToTop = existingImageLayoutParams.topToTop;
        newImageLayoutParams.verticalBias = existingImageLayoutParams.verticalBias;
        newImageView.setLayoutParams(newImageLayoutParams);
        newImageView.setVisibility(View.GONE);

        HashMap<ImageView, Integer> returnval = new HashMap<>();
        returnval.put(newImageView, newID);
        return returnval;
    }

    private HashMap<TextView, Integer> returnNewTextView() {
        //For textview first:
        TextView newTextView = new TextView(requireContext());
        newTextView.setText("Some default Value");
        int anotherNewID = View.generateViewId();
        newTextView.setId(anotherNewID);
//        this.NewTextViewID = anotherNewID;
        ConstraintLayout.LayoutParams existingTextLayoutParams = (ConstraintLayout.LayoutParams) existingTextView.getLayoutParams();
        ConstraintLayout.LayoutParams newTextLayoutParams = new ConstraintLayout.LayoutParams(existingTextLayoutParams.width, existingTextLayoutParams.height);

        //Copying all old layout params over
        newTextLayoutParams.width = existingTextLayoutParams.width;
        newTextLayoutParams.height = existingTextLayoutParams.height;
        newTextLayoutParams.topToTop = existingTextLayoutParams.topToTop;
        newTextLayoutParams.startToStart = existingTextLayoutParams.startToStart;
        newTextLayoutParams.endToEnd = existingTextLayoutParams.endToEnd;
        newTextLayoutParams.bottomToBottom = existingTextLayoutParams.bottomToBottom;
        newTextLayoutParams.horizontalBias = existingTextLayoutParams.horizontalBias;
        newTextLayoutParams.verticalBias = existingTextLayoutParams.verticalBias;

        //Setting layoutparams for the new TextView
        newTextView.setLayoutParams(newTextLayoutParams);
        newTextView.setVisibility(View.GONE);
        newTextView.setGravity(Gravity.CENTER);
        newTextView.setTextColor(Color.parseColor("#000000"));
        newTextView.setTextSize(15);

        HashMap<TextView, Integer> returnval = new HashMap<>();
        returnval.put(newTextView, anotherNewID);
        return returnval;
    }

    private boolean doesImageViewWithThisIDExist(int id) {
        boolean returnval = false;
        ConstraintLayout constraintLayout = requireView().findViewById(R.id.regularMapViewConstraintLayout);
        for (int i=0; i<constraintLayout.getChildCount(); i++) {
            View childView = constraintLayout.getChildAt(i);
            if (childView.getId() == id && childView instanceof ImageView) {
                returnval = true;
                break;
            }
        }
        return returnval;
    }

    private boolean doesTextViewWithThisIDExist(int id) {
        boolean returnval = false;
        ConstraintLayout constraintLayout = requireView().findViewById(R.id.regularMapViewConstraintLayout);
        for (int i=0; i<constraintLayout.getChildCount(); i++) {
            View childView = constraintLayout.getChildAt(i);
            if (childView.getId() == id && childView instanceof TextView) {
                returnval = true;
                break;
            }
        }
        return returnval;
    }

    private ImageView returnImageViewWithThisID(int id) {
        ImageView returnval = null;
        ConstraintLayout constraintLayout = requireView().findViewById(R.id.regularMapViewConstraintLayout);
        for (int i=0; i<constraintLayout.getChildCount(); i++) {
            View childView = constraintLayout.getChildAt(i);
            if (childView.getId() == id && childView instanceof ImageView) {
                returnval = (ImageView) childView;
                break;
            }
        }
        return returnval;
    }

    private TextView returnTextViewWithThisID(int id) {
        TextView returnval = null;
        ConstraintLayout constraintLayout = requireView().findViewById(R.id.regularMapViewConstraintLayout);
        for (int i=0; i<constraintLayout.getChildCount(); i++) {
            View childView = constraintLayout.getChildAt(i);
            if (childView.getId() == id && childView instanceof TextView) {
                returnval = (TextView) childView;
                break;
            }
        }
        return returnval;
    }

    //Note, if geofence is created with the same geofenceID, the older one is overwritten with the new one
    //Hence, we dont have to delete all older geofences.
    @SuppressLint("MissingPermission")
    private void addGeofence(LatLng latlng, float radius, String geofenceID) {
        Geofence geofence = geofenceHelper.getGeofence(geofenceID, latlng, radius, Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT);
        GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofence);
        PendingIntent pendingIntent = geofenceHelper.getPendingIntent();

        geofencingClient.addGeofences(geofencingRequest, pendingIntent).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                System.out.println("Geofence added "+geofenceID);
            }
        }). addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String errormsg = geofenceHelper.getErrorString(e);
                System.out.println("Error in Geofence: "+errormsg);
            }
        });
    }
}

//References used here:
//https://stackoverflow.com/a/51024906
//https://stackoverflow.com/a/4602929
//ChatGPT to help with the thought process and understand programmatically creating ImageView and TextView
//https://stackoverflow.com/questions/12775743/programmatically-center-textview-text
//https://developers.google.com/android/reference/com/google/android/gms/location/GeofenceStatusCodes#GEOFENCE_REQUEST_TOO_FREQUENT
//https://stackoverflow.com/questions/44309857/request-code-for-permissions-in-android
//https://developer.android.com/reference/android/app/PendingIntent
//https://www.youtube.com/watch?v=nmAtMqljH9M
//https://www.youtube.com/watch?v=Ge4_4ZnAAX8
//https://stackoverflow.com/questions/36276840/how-to-set-checkbox-checked-and-disabled
//https://stackoverflow.com/questions/9411540/android-get-checked-checkbox-values
//https://dev.mysql.com/doc/refman/8.0/en/datetime.html
//https://stackoverflow.com/questions/5308200/clear-text-in-edittext-when-entered