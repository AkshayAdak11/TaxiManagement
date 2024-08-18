package com.taxifleet.patterns;

import com.taxifleet.model.Location;
import com.taxifleet.model.Taxi;

import java.util.List;

public interface DispatchStrategy {
    List<Taxi> findAvailableTaxis(Location location);
}