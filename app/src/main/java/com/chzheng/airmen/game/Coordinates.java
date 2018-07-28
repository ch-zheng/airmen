package com.chzheng.airmen.game;

public class Coordinates {
    private double latitude, longitude;

    public Coordinates(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    //Simple getters
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }

    //Simple setters
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
}
