package com.example.finalcs4520;

import java.util.ArrayList;

public class TripProfile {
    private String fromLocation;
    private String toLocation;
    private String dateTrip;
    // This is an arraylist of string for now, later we can make Transportation its own class if needed.
    private ArrayList<String> transportations;

    public TripProfile(String fromLocation, String toLocation, String dateTrip, ArrayList<String> transportations) {
        this.fromLocation = fromLocation;
        this.toLocation = toLocation;
        this.dateTrip = dateTrip;
        this.transportations = transportations;

    }

    public String getFromLocation() {
        return this.fromLocation;
    }

    public String getToLocation() {
        return this.toLocation;
    }

    public String getDateTrip() {
        return this.dateTrip;
    }

    public ArrayList<String> getTransportations() {
        return this.transportations;
    }


}
