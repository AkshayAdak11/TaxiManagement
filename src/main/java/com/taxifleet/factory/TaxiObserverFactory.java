package com.taxifleet.factory;

import com.taxifleet.enums.BookingStrategy;
import com.taxifleet.services.BookingService;
import com.taxifleet.services.CentralizedBookingService;
import com.taxifleet.services.DashboardService;
import com.taxifleet.services.TaxiService;
import com.taxifleet.strategy.AllDistanceBasedAssignmentStrategy;
import com.taxifleet.strategy.BookingAssignmentStrategy;
import com.taxifleet.observer.TaxiObserver;
import com.taxifleet.db.StoredTaxi;
import com.taxifleet.strategy.DistanceBasedAssignmentStrategy;

import javax.inject.Inject;

public class TaxiObserverFactory {

    public TaxiObserver createObserver(StoredTaxi taxi, BookingAssignmentStrategy strategy,
                                       CentralizedBookingService centralizedBookingService) {
        return new TaxiObserver(taxi, strategy, centralizedBookingService);
    }

    //Visitor Pattern
    public BookingAssignmentStrategy createStrategy(BookingStrategy bookingStrategy,
                                                    TaxiService taxiService,
                                                    BookingService bookingService,
                                                    DashboardService dashboardService) {
        return bookingStrategy.accept(new BookingStrategy.BookingStrategyVisitor<BookingAssignmentStrategy>() {
            @Override
            public BookingAssignmentStrategy visitNearBy() {
                return new DistanceBasedAssignmentStrategy(10, taxiService, bookingService, dashboardService);
            }

            @Override
            public BookingAssignmentStrategy visitAllArea() {
                return new AllDistanceBasedAssignmentStrategy(taxiService, bookingService, dashboardService);
            }
        });
    }

    public static void updateTaxiObserver(TaxiObserver observer, StoredTaxi updatedTaxi) {
        if (observer == null || updatedTaxi == null) {
            throw new IllegalArgumentException("Observer and updated taxi cannot be null");
        }

        StoredTaxi taxi = observer.getTaxi();
        if (taxi != null) {
            taxi.setAvailable(updatedTaxi.isAvailable());
            taxi.setStatus(updatedTaxi.getStatus());
            taxi.setVersion(updatedTaxi.getVersion());
        }
    }
}
