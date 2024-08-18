package com.taxifleet.health;

import com.codahale.metrics.health.HealthCheck;

public class TaxiFleetHealthCheck extends HealthCheck {

    @Override
    protected Result check() throws Exception {
        return Result.healthy();
    }
}
