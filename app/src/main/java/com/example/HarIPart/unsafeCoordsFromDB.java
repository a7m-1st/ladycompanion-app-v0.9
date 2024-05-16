package com.example.HarIPart;

public class unsafeCoordsFromDB {

    private String dbid;
    private double xAxis;
    private double yAxis;
    private String reasonForMarked;

    public unsafeCoordsFromDB(String id, double x, double y, String reason) {
        this.dbid = id;
        this.xAxis = x;
        this.yAxis = y;
        this.reasonForMarked = reason;
    }

    public String getID() { return this.dbid; }

    public double getX() {
        return this.xAxis;
    }

    public double getY() {
        return this.yAxis;
    }

    public String getReasonForMarked() {
        return this.reasonForMarked;
    }

}
