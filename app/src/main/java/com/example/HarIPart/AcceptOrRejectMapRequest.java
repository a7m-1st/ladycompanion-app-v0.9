package com.example.HarIPart;

import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.DataBase.sqlConnector;
import com.example.ladyapp.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;

public class AcceptOrRejectMapRequest extends Fragment {

    private static final String LATLNGFILE = "latlng_info";
    private int DBID;
    private unsafeRequestsFromDB request;
    private TextView locationtext, reasontext;
    private float GEOFENCE_RADIUS = 75;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_accept_or_reject_map_request, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Initializing the Map here
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.AgencyConfirmMap);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

        //Initalizing elements here
        locationtext = view.findViewById(R.id.TVLocationView);
        reasontext = view.findViewById(R.id.reasonView);

        //Reading the database ID from the files
        DBID = Integer.parseInt(readDBIDFromFile(requireContext()));

        //Deleting said file after that
        File latlngFile = new File(requireContext().getFilesDir(), LATLNGFILE);
        latlngFile.delete();

        //Handling all buttons
        Button backbutton = view.findViewById(R.id.goBackToRecyclertButton);
        Button accept = view.findViewById(R.id.acceptRequestButton);
        Button reject = view.findViewById(R.id.RejectRequestButton);

        //When back button pressed
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_acceptOrRejectMapRequest_to_agencyReviewUnsafeZoneRequests);
            }
        });

        //When accept button pressed
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Deleting DBID from requests table, and adding to unsafe zones table
                try {
                    sqlConnector connect = new sqlConnector();
                    connect.deleteUnsafeZoneRequest(DBID);
                    connect.insertNewUnsafeZone(request.getX(), request.getY(), request.getReason());
                    connect.closeConnection();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                //Bringing up success window
                ImageView successpage = view.findViewById(R.id.unsafeMarkedPopupAcceptOrRejectPage);
                TextView successtext = view.findViewById(R.id.UnsageMarkedPopupTextAcceptOrReject);
                Button okbutton = view.findViewById(R.id.okAddUnsafeZone);
                successpage.setVisibility(View.VISIBLE);
                successpage.bringToFront();
                successtext.setVisibility(View.VISIBLE);
                successtext.bringToFront();
                okbutton.setVisibility(View.VISIBLE);
                okbutton.bringToFront();

                //setting onclick for okbutton
                okbutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //make everything disappear
                        successpage.setVisibility(View.GONE);
                        successtext.setVisibility(View.GONE);
                        okbutton.setVisibility(View.GONE);
                        Navigation.findNavController(view).navigate(R.id.action_acceptOrRejectMapRequest_to_agencyReviewUnsafeZoneRequests);
                    }
                });
            }
        });

        //When reject button pressed
        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Deleting DBID from requests table
                try {
                    sqlConnector connect = new sqlConnector();
                    connect.deleteUnsafeZoneRequest(DBID);
                    connect.closeConnection();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                //Making toast
                Toast.makeText(requireContext(), "Request Deleted", Toast.LENGTH_SHORT).show();

                //Moving to previous page
                Navigation.findNavController(view).navigate(R.id.action_acceptOrRejectMapRequest_to_agencyReviewUnsafeZoneRequests);
            }
        });
    }

    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(@NonNull GoogleMap googleMap) {

            //Getting all information about the request
            try {
                sqlConnector connect = new sqlConnector();
                request = connect.getUnsafeRequestDetails(DBID);
                connect.closeConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            //Setting location and reason texts
            //Obtaining address for location:
            Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
            List<Address> address;
            try {
                address = geocoder.getFromLocation(request.getX(), request.getY(), 1);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String locationinString = address.get(0).getAddressLine(address.get(0).getMaxAddressLineIndex());
            String locationvalue = "Location: "+locationinString;
            locationtext.setText(locationvalue);

            //Setting reason:
            String reasonvalue = "Reason: "+request.getReason();
            reasontext.setText(reasonvalue);

            //Centering map to LatLng mentioned by location
            LatLng locToCenterTo = new LatLng(request.getX(), request.getY());
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locToCenterTo, 17));

            //Drawing a circle on that point
            googleMap.addCircle(new CircleOptions().center(locToCenterTo).radius(GEOFENCE_RADIUS).fillColor(Color.argb(62, 255, 0, 0)).strokeWidth(0));
        }
    };

    private String readDBIDFromFile(Context context) {
        String FileContent = "";
        FileInputStream FIS = null;
        try {
            FIS = context.openFileInput(LATLNGFILE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        InputStreamReader inputStreamReader = new InputStreamReader(FIS, StandardCharsets.UTF_8);
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
            String line = reader.readLine();
            while (line != null) {
                stringBuilder.append(line);
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            FileContent = stringBuilder.toString();
        }
        return FileContent;
    }
}