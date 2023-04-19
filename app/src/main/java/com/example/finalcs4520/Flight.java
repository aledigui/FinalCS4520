package com.example.finalcs4520;

public class Flight {
    private String price, airline, departure, departureDate, departureTime, arrival, arrivalDate, arrivalTime, date;
    private Integer layovers;

    public Flight(String price, String airline, Integer layovers, String departure, String departureDate, String departureTime, String arrival, String arrivalDate, String arrivalTime, String date) {
        this.price = price;
        this.airline = airline;
        this.layovers = layovers;
        this.departure = departure;
        this.departureDate = departureDate;
        this.departureTime = departureTime;
        this.arrival = arrival;
        this.arrivalDate = arrivalDate;
        this.arrivalTime = arrivalTime;
        this.date = date;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getAirline() {
        return airline;
    }

    public void setAirline(String airline) {
        this.airline = airline;
    }

    public Integer getLayovers() {
        return layovers;
    }

    public void setLayovers(Integer layovers) {
        this.layovers = layovers;
    }

    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public String getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(String departureDate) {
        this.departureDate = departureDate;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public String getArrival() {
        return arrival;
    }

    public void setArrival(String arrival) {
        this.arrival = arrival;
    }

    public String getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(String arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.arrivalTime = date;
    }
}
