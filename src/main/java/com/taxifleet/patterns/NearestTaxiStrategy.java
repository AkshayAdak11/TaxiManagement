package com.taxifleet.patterns;

import com.taxifleet.model.Location;
import com.taxifleet.model.Taxi;

import java.util.List;
import java.util.stream.Collectors;

public class NearestTaxiStrategy implements DispatchStrategy {

    private final List<Taxi> allTaxis;

    public NearestTaxiStrategy(List<Taxi> allTaxis) {
        this.allTaxis = allTaxis;
    }

    @Override
    public List<Taxi> findAvailableTaxis(Location location) {
        return allTaxis.stream()
                .filter(Taxi::isAvailable)
                .sorted((taxi1, taxi2) -> Double.compare(taxi1.getLocation().distanceTo(location), taxi2.getLocation().distanceTo(location)))
                .collect(Collectors.toList());
    }
}