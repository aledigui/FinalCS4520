package com.example.finalcs4520;

import java.io.Serializable;

public class TransitSection implements Serializable {
    private String type;
    private String departureTime;
    private String departureName;
    private String arrivalTime;
    private String arrivalName;

    public TransitSection(String type, String departureTime, String departureName, String arrivalTime, String arrivalName) {
        this.type = type;
        this.departureTime = departureTime;
        this.departureName = departureName;
        this.arrivalTime = arrivalTime;
        this.arrivalName = arrivalName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public String getDepartureName() {
        return departureName;
    }

    public void setDepartureName(String departureName) {
        this.departureName = departureName;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getArrivalName() {
        return arrivalName;
    }

    public void setArrivalName(String arrivalName) {
        this.arrivalName = arrivalName;
    }

    @Override
    public String toString() {
        return "TransitSection{" +
                "type='" + type + '\'' +
                ", departureTime='" + departureTime + '\'' +
                ", departureName='" + departureName + '\'' +
                ", arrivalTime='" + arrivalTime + '\'' +
                ", arrivalName='" + arrivalName + '\'' +
                '}';
    }
}
