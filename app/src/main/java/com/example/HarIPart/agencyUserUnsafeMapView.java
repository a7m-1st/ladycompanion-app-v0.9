package com.example.HarIPart;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.DataBase.sqlConnector;
import com.example.ladyapp.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.SphericalUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;


public class agencyUserUnsafeMapView extends Fragment {

    private ImageView existingImageView;
    private TextView existingTextView;
    FusedLocationProviderClient fusedLocationProviderClient;
    private int locationUpdateCount;
    private GoogleMap googleMapObjectForWork;
    ArrayList<LatLngDistanceKeeper> LatLngsAgency = new ArrayList<>();
    private float GEOFENCE_RADIUS = 75;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_agency_user_unsafe_map_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Initializing the Map here
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.AgencyMap);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

        //Getting existing imageview and textview:
        this.existingImageView = view.findViewById(R.id.unsafeZoneDescription3);
        this.existingTextView = view.findViewById(R.id.unsafeZoneDescriptionText2);

        //Making them invisible:
        existingImageView.setVisibility(View.GONE);
        existingTextView.setVisibility(View.GONE);

        //Getting map location ready:
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(view.getContext());

        //Handling mark unsafe zone button
        Button markUnsafeButton = view.findViewById(R.id.markUnsafeButton2);
        markUnsafeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Make first menu visible first
                ImageView reasonbanner = view.findViewById(R.id.unsafeMarkSuccessPopupReason);
                TextView reasontext = view.findViewById(R.id.textView11);
                EditText reasonformarkagency = view.findViewById(R.id.reasonForMarkAgency);
                Button continueButton = view.findViewById(R.id.continueButtonAgencyUnsafeZoneReason);
                Button cancelButton = view.findViewById(R.id.cancelButtonAgencyUnsafeZoneReason);
                reasonbanner.setVisibility(View.VISIBLE);
                reasonbanner.bringToFront();
                reasontext.setVisibility(View.VISIBLE);
                reasontext.bringToFront();
                reasonformarkagency.setVisibility(View.VISIBLE);
                reasonformarkagency.bringToFront();
                continueButton.setVisibility(View.VISIBLE);
                continueButton.bringToFront();
                cancelButton.setVisibility(View.VISIBLE);
                cancelButton.bringToFront();

                //Handle continue button
                continueButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String reason = reasonformarkagency.getText().toString();
                        LatLng point = googleMapObjectForWork.getCameraPosition().target;
                        //Make everything disappear first
                        reasonbanner.setVisibility(View.GONE);
                        reasontext.setVisibility(View.GONE);
                        reasonformarkagency.setVisibility(View.GONE);
                        reasonformarkagency.getText().clear();
                        continueButton.setVisibility(View.GONE);
                        cancelButton.setVisibility(View.GONE);

                        //Send to database
                        try {
                            sqlConnector connect = new sqlConnector();
                            connect.insertNewUnsafeZone(point.latitude, point.longitude, reason);
                            connect.closeConnection();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        //add marked region to current view:
                        HashMap<ImageView, Integer> a = returnNewImageView();
                        HashMap<TextView, Integer> b = returnNewTextView();
                        HashMap.Entry<ImageView, Integer> entry = a.entrySet().iterator().next();
                        ImageView IVforthisLatLong = entry.getKey();
                        int IDofIV = entry.getValue();
                        HashMap.Entry<TextView, Integer> entry2 = b.entrySet().iterator().next();
                        TextView TVforthisLatLong = entry2.getKey();
                        int IDofTV = entry2.getValue();
                        //Add to LatLngs arraylist
                        LatLngsAgency.add(new LatLngDistanceKeeper(point, reason , IVforthisLatLong, IDofIV, TVforthisLatLong, IDofTV));
                        //draws circle there
                        googleMapObjectForWork.addCircle(new CircleOptions().center(point).radius(GEOFENCE_RADIUS).fillColor(Color.argb(62, 255, 0, 0)).strokeWidth(0));

                        //Make the final banner visible
                        ImageView successpopup = view.findViewById(R.id.unsafeMarkSuccessPopup);
                        TextView successtext = view.findViewById(R.id.unsafeMarkSuccessText);
                        Button oksuccess = view.findViewById(R.id.okUnsafeMarkedAgency);
                        successpopup.setVisibility(View.VISIBLE);
                        successpopup.bringToFront();
                        successtext.setVisibility(View.VISIBLE);
                        successtext.bringToFront();
                        oksuccess.setVisibility(View.VISIBLE);
                        oksuccess.bringToFront();

                        oksuccess.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                successpopup.setVisibility(View.GONE);
                                successtext.setVisibility(View.GONE);
                                oksuccess.setVisibility(View.GONE);
                            }
                        });
                    }
                });

                //Handle cancel button
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        reasonbanner.setVisibility(View.GONE);
                        reasontext.setVisibility(View.GONE);
                        reasonformarkagency.setVisibility(View.GONE);
                        reasonformarkagency.getText().clear();
                        continueButton.setVisibility(View.GONE);
                        cancelButton.setVisibility(View.GONE);
                    }
                });
            }
        });

        //Handling Unsafe Zone addition requests button
        Button unsafeRequestReview = view.findViewById(R.id.UnsafeRequestReview);
        unsafeRequestReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_agencyUserUnsafeMapView_to_agencyReviewUnsafeZoneRequests);
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
                    LatLngsAgency.add(new LatLngDistanceKeeper(toadd, i.getReasonForMarked() , IVforthisLatLong, IDofIV, TVforthisLatLong, IDofTV));

                    //draws circle there
                    googleMap.addCircle(new CircleOptions().center(toadd).radius(GEOFENCE_RADIUS).fillColor(Color.argb(62, 255, 0, 0)).strokeWidth(0));
                }
            }

            googleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
                @Override
                public void onCameraMove() {

                    LatLng location = googleMap.getCameraPosition().target;
                    if (LatLngsAgency.size()>0) {
                        //Keep updating all distance values between all spheres for everything

                        for (LatLngDistanceKeeper i: LatLngsAgency) {
                            //First sets distance
                            double dist = SphericalUtil.computeDistanceBetween(location, i.getLatLng());
                            i.setDistance(dist);

                            //Then checks if anything is in range
                            if (i.getDistance() < GEOFENCE_RADIUS) {

                                ConstraintLayout constraintLayout = requireView().findViewById(R.id.AgencyMapViewConstraintLayout);
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
        ConstraintLayout constraintLayout = requireView().findViewById(R.id.AgencyMapViewConstraintLayout);
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
        ConstraintLayout constraintLayout = requireView().findViewById(R.id.AgencyMapViewConstraintLayout);
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
        ConstraintLayout constraintLayout = requireView().findViewById(R.id.AgencyMapViewConstraintLayout);
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
        ConstraintLayout constraintLayout = requireView().findViewById(R.id.AgencyMapViewConstraintLayout);
        for (int i=0; i<constraintLayout.getChildCount(); i++) {
            View childView = constraintLayout.getChildAt(i);
            if (childView.getId() == id && childView instanceof TextView) {
                returnval = (TextView) childView;
                break;
            }
        }
        return returnval;
    }
}