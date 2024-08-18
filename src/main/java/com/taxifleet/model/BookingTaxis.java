package com.taxifleet.model;

import com.taxifleet.db.StoredBooking;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingTaxis {
    public StoredBooking getStoredBooking() {
        return storedBooking;
    }

    public void setStoredBooking(StoredBooking storedBooking) {
        this.storedBooking = storedBooking;
    }

    public boolean isPublishNearByTaxis() {
        return publishNearByTaxis;
    }

    public void setPublishNearByTaxis(boolean publishNearByTaxis) {
        this.publishNearByTaxis = publishNearByTaxis;
    }

    public boolean isPublishToAllTaxis() {
        return publishToAllTaxis;
    }

    public void setPublishToAllTaxis(boolean publishToAllTaxis) {
        this.publishToAllTaxis = publishToAllTaxis;
    }

    private StoredBooking storedBooking;

    private boolean publishNearByTaxis;

    private boolean publishToAllTaxis;
}
