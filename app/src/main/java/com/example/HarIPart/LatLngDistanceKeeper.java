package com.example.HarIPart;

import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

public class LatLngDistanceKeeper {

    private LatLng valueinObject;
    private double distanceFromPointer;
    private String reasonForMarked;
    private static int counter = 0;
    private int anotherCounter = 0;

    //ImageView and TextView objects
    private ImageView IVAssociatedWithThisLatLng;
    private int TheIDofIVAssociatedWithThisLatLng;
    private TextView TVAssociatedWithThisLatLng;
    private int TheIDofTVAssociatedWithThisLatLng;


    public LatLngDistanceKeeper(LatLng value, String reason, ImageView IV, int IVID, TextView TV, int TVID) {
        this.valueinObject = value;
        this.distanceFromPointer = 0;
        this.counter++;
        this.anotherCounter = counter;

        this.reasonForMarked = reason;

        this.IVAssociatedWithThisLatLng = IV;
        this.TheIDofIVAssociatedWithThisLatLng = IVID;
        this.TVAssociatedWithThisLatLng = TV;
        this.TheIDofTVAssociatedWithThisLatLng = TVID;
    }

    public ImageView getIV() { return this.IVAssociatedWithThisLatLng; }

    public TextView getTV() { return this.TVAssociatedWithThisLatLng; }

    public int getIVID() { return this.TheIDofIVAssociatedWithThisLatLng; }

    public int getTVID() { return this.TheIDofTVAssociatedWithThisLatLng; }

    public LatLng getLatLng() {
        return this.valueinObject;
    }

    public int returnCounter() {
        return this.anotherCounter;
    }

    public double getDistance() {
        return this.distanceFromPointer;
    }

    public void setDistance(double newDistance) {
        this.distanceFromPointer = newDistance;
    }

    public String getStoredText() {
        return this.reasonForMarked;
    }
}
