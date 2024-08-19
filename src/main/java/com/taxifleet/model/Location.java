package com.taxifleet.model;

import lombok.Data;

import javax.inject.Inject;
import javax.persistence.Embeddable;

@Data
public class Location {
    private double latitude;
    private double longitude;

    // Default constructor
    public Location() {
    }

    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double distanceTo(double latitude, double longitude) {
        double latDiff = this.latitude - latitude;
        double lonDiff = this.longitude - longitude;
        return Math.sqrt(latDiff * latDiff + lonDiff * lonDiff);
    }
}