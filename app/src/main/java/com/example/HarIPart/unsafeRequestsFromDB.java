package com.example.HarIPart;

public class unsafeRequestsFromDB {

    private int reqID;
    private double x;
    private double y;
    private String reason;

    public unsafeRequestsFromDB(int id, double xaxis, double yaxis, String reasons) {
        this.reqID = id;
        this.x = xaxis;
        this.y = yaxis;
        this.reason = reasons;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getReqID() {
        return reqID;
    }

    public String getReason() {
        return reason;
    }
}
