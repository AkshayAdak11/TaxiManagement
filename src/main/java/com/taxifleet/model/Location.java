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

    // Method to check if two locations are near each other
    public boolean isNear(Location otherLocation) {
        // Define a threshold for "nearness"
        final double NEAR_THRESHOLD = 0.01; // Example threshold in degrees

        double latDiff = Math.abs(this.latitude - otherLocation.latitude);
        double lonDiff = Math.abs(this.longitude - otherLocation.longitude);

        return latDiff < NEAR_THRESHOLD && lonDiff < NEAR_THRESHOLD;
    }

    public double distanceTo(double latitude, double longitude) {
        double latDiff = this.latitude - latitude;
        double lonDiff = this.longitude - longitude;
        return Math.sqrt(latDiff * latDiff + lonDiff * lonDiff);
    }
}