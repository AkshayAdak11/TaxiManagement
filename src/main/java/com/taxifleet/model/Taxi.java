package com.taxifleet.model;


import lombok.Data;

@Data
public class Taxi {
    private String id;
    private boolean available;
    private Location location;

    public Taxi(String id, Location location) {
        this.id = id;
        this.available = true;
        this.location = location;
    }

    public void receiveBooking(Location pickupLocation) {
        if (available && location.isNear(pickupLocation)) {
            System.out.println("Taxi " + id + " accepted the booking.");
            setAvailable(false);
        } else {
            System.out.println("Taxi " + id + " ignored the booking.");
        }
    }
}
