package com.example.HarIPart;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    private static final String REASONSFILE = "reasons_file";
    private static final String NOTIFDISABLEDFILE = "notif_is_disabled";
    NotificationsAreHere notificationHelper;

    @Override
    public void onReceive(Context context, Intent intent) {

//        Toast.makeText(context, "Geofence Works! ", Toast.LENGTH_SHORT).show();

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if (geofencingEvent.hasError()) {
            System.out.println("Error receiving Geofence Event");
            return;
        }

        List<Geofence> geofenceList = geofencingEvent.getTriggeringGeofences();
        String geofenceID = null;
        for (Geofence geofence: geofenceList) {
            geofenceID = geofence.getRequestId();
            System.out.println(geofenceID);
        }

        //Get appropriate reason to add in notification
        String reasons = readReasonsFromFile(context);
        String[] reasonsArray = reasons.split("%");
        String theReasonToUse = reasonsArray[Integer.parseInt(geofenceID)-1];

        //Add notification information
        notificationHelper = new NotificationsAreHere(context);

        int transitionType = geofencingEvent.getGeofenceTransition();

        if (!checkIfNotifsFileExists(context)) {
            switch (transitionType) {
                case Geofence.GEOFENCE_TRANSITION_ENTER:
                    sendNotifications(geofenceID, "WARNING: Unsafe Zone Entered", "Reason: "+theReasonToUse, "Unsafe Zone Entered");
                    break;
                case Geofence.GEOFENCE_TRANSITION_DWELL:
                    sendNotifications(geofenceID, "WARNING: Currently Within Unsafe Zone", "Reason: "+theReasonToUse, "Inside Unsafe Zone");
                    break;
                case Geofence.GEOFENCE_TRANSITION_EXIT:
                    sendNotifications(geofenceID, "Alert: Unsafe Zone Exited", "Reason: "+theReasonToUse, "Unsafe Zone Exited");
                    break;
            }
        }
    }

    private String readReasonsFromFile(Context context) {
        String FileContent = "";
        FileInputStream FIS = null;
        try {
            FIS = context.openFileInput(REASONSFILE);
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

    private boolean checkIfNotifsFileExists(Context context) {
        File notifFile = new File(context.getFilesDir(), NOTIFDISABLEDFILE);
        if (notifFile.exists()) {
            return true;
        }
        return false;
    }

    private void sendNotifications(String GeofenceID, String title, String body, String summary) {
        notificationHelper.sendEmergencyNotifications(Integer.parseInt(GeofenceID), title, body, summary);
    }
}