package com.example.NicoPart;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentActivity;

import com.example.ladyapp.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SosDetailsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int REQUEST_CODE = 2;
    private static final int YOUR_REQUEST_CODE = 3;
    private ImageView locatingIcon;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private TextView getLocationText;
    private TextView editEmergencyConsText;
    private String contactNumber1, contactNumber2, contactNumber3;
    CardView cardView1, cardView2, cardView3;
    private EditText sosMessageInput;
    private ImageButton sosButton;
    private static final int PERMISSION_REQUEST_CODE = 5;
    private ImageView microphone;
    private MediaRecorder recorder = null;
    private String audioFileName = null;
    private static final int MAX_RECORDING_DURATION = 5000; // 5 seconds in milliseconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos_details);

        locatingIcon = findViewById(R.id.locating_icon);
        getLocationText = findViewById(R.id.Get_Location_Text);
        editEmergencyConsText = findViewById(R.id.editEmergencyCons);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.MapFragmentSos);
        mapFragment.getMapAsync(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        getLocationText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentLocationAndPopulateEditText();
                requestLocationPermission();
            }
        });

        createLocationCallback();

        editEmergencyConsText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SosDetailsActivity.this, EditEmergencyContactActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        cardView1 = findViewById(R.id.cardView1);
        cardView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makePhoneCall1(view);
            }
        });

        cardView2 = findViewById(R.id.cardView2);
        cardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makePhoneCall2(view);
            }
        });

        cardView3 = findViewById(R.id.cardView3);
        cardView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makePhoneCall3(view);
            }
        });

        loadContactNumbers();

        sosMessageInput = findViewById(R.id.sosMessageInput);
        sosButton = findViewById(R.id.sosButton);

        sosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendSosMessage();
            }
        });

        checkPermissions();

        microphone = findViewById(R.id.ic_microphone);
        microphone.setOnClickListener(v -> {
            if (recorder == null) {
                checkPermissionsAndStartRecording();
            } else {
                stopRecording();
            }
        });

    }

    private void getCurrentLocationAndPopulateEditText() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                updateLocationText(location);
            } else {
                Toast.makeText(SosDetailsActivity.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                requestLocationUpdates(); // Request for a fresh location if the last known location is null
            }
        });
    }

    private void updateLocationText(Location location) {
        String locationString = "https://www.google.com/maps/search/?api=1&query=" + location.getLatitude() + "," + location.getLongitude() + "\n";
        sosMessageInput.setText(locationString);
        sosMessageInput.requestFocus();
        sosMessageInput.setSelection(sosMessageInput.getText().length());
    }


    ///////////////For sending SMS//////////////////////
    private void sendSosMessage() {
        // Check for SMS permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            String message = sosMessageInput.getText().toString();

            if (!message.isEmpty()) {
                List<Contact> selectedContacts = loadSelectedContacts();
                SmsManager smsManager = SmsManager.getDefault();

                for (Contact contact : selectedContacts) {
                    String phoneNumber = contact.getPhoneNumber();

                    // Send SMS message
                    smsManager.sendTextMessage(phoneNumber, null, message, null, null);
                }

                // Clear the input field and show a success message
                sosMessageInput.setText("");
                Toast.makeText(this, "The SOS message is sent", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Please enter a message to send", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "SMS permission is required to send SOS messages", Toast.LENGTH_LONG).show();
        }
    }

    private List<Contact> loadSelectedContacts() {
        SharedPreferences prefs = getSharedPreferences("SelectedContacts", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("Contacts", null);
        if (json == null) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<ArrayList<Contact>>() {}.getType();
        return gson.fromJson(json, type);
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_CONTACTS},
                    PERMISSION_REQUEST_CODE);
        }
    }

    //// For voice message /////////
    private void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        audioFileName = getExternalCacheDir().getAbsolutePath();
        audioFileName += "/audiorecordtest.m4a";

        recorder.setOutputFile(audioFileName);

        try {
            recorder.prepare();
            recorder.start();
            Toast.makeText(this, "Recording started", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.e("AudioRecordTest", "prepare() failed");
        }

        new Handler().postDelayed(this::stopRecording, MAX_RECORDING_DURATION);
    }

    private void stopRecording() {
        Toast.makeText(this, "Recording finished", Toast.LENGTH_SHORT).show();
        recorder.stop();
        recorder.release();
        recorder = null;
        shareRecordedAudio();
    }

    private void shareRecordedAudio() {
        Uri audioUri = FileProvider.getUriForFile(SosDetailsActivity.this, "com.example.NicoPart.fileprovider", new File(audioFileName));
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, audioUri);
        shareIntent.setType("audio/mp4");
        startActivity(Intent.createChooser(shareIntent, "Share Voice Message"));
    }

    private void checkPermissionsAndStartRecording() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);
        } else {
            startRecording();
        }
    }


    ////// For calling /////
    private void loadContactNumbers() {
        SharedPreferences sharedPref = getSharedPreferences("EmergencyContacts", MODE_PRIVATE);
        contactNumber1 = sharedPref.getString("CONTACT_NUMBER_1", "");
        contactNumber2 = sharedPref.getString("CONTACT_NUMBER_2", "");
        contactNumber3 = sharedPref.getString("CONTACT_NUMBER_3", "");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            String name1 = data.getStringExtra("CONTACT_NAME_1");
            String name2 = data.getStringExtra("CONTACT_NAME_2");
            String name3 = data.getStringExtra("CONTACT_NAME_3");

            String number1 = data.getStringExtra("CONTACT_NUMBER_1");
            String number2 = data.getStringExtra("CONTACT_NUMBER_2");
            String number3 = data.getStringExtra("CONTACT_NUMBER_3");

            if (name1.isEmpty()) {
                name1 = "Name";
            }
            if (name2.isEmpty()) {
                name2 = "Name";
            }
            if (name3.isEmpty()) {
                name3 = "Name";
            }


            TextView contactName1 = findViewById(R.id.contactname1);
            contactName1.setText(name1);

            TextView contactName2 = findViewById(R.id.contactname2);
            contactName2.setText(name2);

            TextView contactName3 = findViewById(R.id.contactname3);
            contactName3.setText(name3);

        }
    }

    public void makePhoneCall1(View view) {
        if (!contactNumber1.isEmpty()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, YOUR_REQUEST_CODE);
            } else {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + contactNumber1));
                startActivity(callIntent);
            }
        } else {
            Toast.makeText(this, "Contact number not available", Toast.LENGTH_SHORT).show();
        }
    }

    public void makePhoneCall2(View view) {
        if (!contactNumber2.isEmpty()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, YOUR_REQUEST_CODE);
            } else {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + contactNumber2));
                startActivity(callIntent);
            }
        } else {
            Toast.makeText(this, "Contact number not available", Toast.LENGTH_SHORT).show();
        }
    }

    public void makePhoneCall3(View view) {
        if (!contactNumber3.isEmpty()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, YOUR_REQUEST_CODE);
            } else {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + contactNumber3));
                startActivity(callIntent);
            }
        } else {
            Toast.makeText(this, "Contact number not available", Toast.LENGTH_SHORT).show();
        }
    }


    ////////// map related code //////////////
    private void createLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult.getLastLocation() != null) {
                    LatLng userLocation = new LatLng(locationResult.getLastLocation().getLatitude(),
                            locationResult.getLastLocation().getLongitude());

                    // Move the camera to the user's location
                    moveCameraToLocation(userLocation);

                    // Remove the location updates to stop continuous location updates
                    stopLocationUpdates();
                }
            }
        };
    }

    private void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            // Permission already granted, request location updates
            requestLocationUpdates();
        } else {
            // Request location permission
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            // Permission granted, request location updates
            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(1000); // Update every 1 second

            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        } else {
            // Permission not granted, handle accordingly (e.g., request permission again)
            requestLocationPermission();
        }
    }

    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    private void moveCameraToLocation(LatLng location) {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.MapFragmentSos);
        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    // Move the camera to the user's location
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f));

                    // Hide the locating icon
                    locatingIcon.setVisibility(View.GONE);
                }
            });
        }
    }

    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Check for location permission
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        // Enable the My Location layer
        googleMap.setMyLocationEnabled(true);

        // Enable zoom controls
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        // Enable compass button
        googleMap.getUiSettings().setCompassEnabled(true);

        // Enable My Location button
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);

        // Set the default map type
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // Enable traffic layer
        googleMap.setTrafficEnabled(true);

        // Enable indoor maps
        googleMap.setIndoorEnabled(true);

        // Enable gesture controls
        googleMap.getUiSettings().setRotateGesturesEnabled(true);
        googleMap.getUiSettings().setTiltGesturesEnabled(true);
        googleMap.getUiSettings().setScrollGesturesEnabled(true);

        // Enable/disable the toolbar
        googleMap.getUiSettings().setMapToolbarEnabled(true);

        // Enable/disable 3D buildings
        googleMap.setBuildingsEnabled(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Handle Location Permission Result
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestLocationUpdates();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
                openAppSettings();
            }
        }

        // Handle Call Permission Result
        if (requestCode == YOUR_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission for call granted. You can call makePhoneCall method here if needed.
            } else {
                Toast.makeText(this, "Permission denied to make calls", Toast.LENGTH_SHORT).show();
            }
        }

        // Handle SMS and Read Contacts Permission Result
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permissions for SMS and reading contacts granted.
            } else {
                // Permissions for SMS and reading contacts denied.
            }
        }

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startRecording();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
