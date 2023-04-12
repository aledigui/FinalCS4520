package com.example.finalcs4520;

import android.net.Uri;

import java.util.ArrayList;

public class TripProfile {
    private String fromLocation;
    private String toLocation;
    private String dateTrip;
    // This is an arraylist of string for now, later we can make Transportation its own class if needed.
    private String transportations;

    private Boolean completed;

    private Uri tripImgPath;

    // TODO: arraylist for transportation
    public TripProfile(String fromLocation, String toLocation, String dateTrip,
                       String transportations, Boolean completed, Uri tripImgPath) {
        this.fromLocation = fromLocation;
        this.toLocation = toLocation;
        this.dateTrip = dateTrip;
        this.transportations = transportations;
        this.completed = completed;
        this.tripImgPath = tripImgPath;

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

    public String getTransportations() {
        return this.transportations;
    }

    public Boolean getCompleted() {return this.completed;}

    public Uri getTripPicture() {
        return this.tripImgPath;
    }

    public void setTripImgPath(Uri newUri) {
        this.tripImgPath = newUri;
    }

    public void setCompleted(Boolean newBool) {
        this.completed = newBool;
    }


}
