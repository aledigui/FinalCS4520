package com.example.finalcs4520;

import java.util.ArrayList;

public class TripProfileList {

    private ArrayList<TripProfile> tripProfileList;

    public TripProfileList() {

    }

    public ArrayList<TripProfile> getTripProfileList() {
        return this.tripProfileList;
    }

    public void setTripProfileList(ArrayList<TripProfile> newTripList) {
        this.tripProfileList = newTripList;
    }
}
