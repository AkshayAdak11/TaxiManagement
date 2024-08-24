package com.taxifleet;

import com.google.inject.Inject;
import com.taxifleet.db.StoredBooking;
import com.taxifleet.db.StoredTaxi;
import com.taxifleet.enums.BookingStatus;
import com.taxifleet.enums.BookingStrategy;
import com.taxifleet.enums.TaxiStatus;
import com.taxifleet.resources.BookingResource;
import com.taxifleet.resources.DashboardResource;
import com.taxifleet.resources.TaxiResource;
import com.taxifleet.services.BookingService;
import com.taxifleet.services.DashboardService;
import com.taxifleet.services.TaxiService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@Slf4j
public class TaxiFleetIntegrationTest extends BaseIntegrationTest {
    private BookingResource bookingResource;

    @Inject
    private BookingService bookingService;

    @Inject
    private TaxiService taxiService;

    @Inject
    private DashboardService dashboardService;

    private TaxiResource taxiResource;

    private DashboardResource dashboardResource;

    @BeforeEach
    void setup() {
        baseSetup();
        bookingResource = new BookingResource(bookingService);
        taxiResource = new TaxiResource(taxiService, bookingService);
        dashboardResource = new DashboardResource(dashboardService);
    }


    @Test
    void testTaxiFleetIntegrationTest() throws InterruptedException {
        //1. Book a Taxi with latitude and longitude  0 0 and status Available

        StoredTaxi storedTaxi = new StoredTaxi();
        storedTaxi.setStatus(TaxiStatus.AVAILABLE);
        storedTaxi.setTaxiNumber("AU11A");
        storedTaxi.setAvailable(true);
        storedTaxi.setFromLatitude(0.00);
        storedTaxi.setFromLongitude(0.00);
        storedTaxi.setCurrentLatitude(0.00);
        storedTaxi.setCurrentLongitude(0.00);
        storedTaxi.setBookingStrategy(BookingStrategy.ALL_AREA);
        taxiResource.createTaxi(storedTaxi);
        System.out.println("Booked taxi 1 " + storedTaxi);


        StoredTaxi storedTaxi1 = new StoredTaxi();
        storedTaxi1.setStatus(TaxiStatus.AVAILABLE);
        storedTaxi1.setTaxiNumber("AU12N");
        storedTaxi1.setAvailable(true);
        storedTaxi1.setFromLatitude(12.00);
        storedTaxi1.setFromLongitude(12.00);
        storedTaxi1.setCurrentLatitude(12.00);
        storedTaxi1.setCurrentLongitude(12.00);
        storedTaxi1.setBookingStrategy(BookingStrategy.NEAR_BY);
        taxiResource.createTaxi(storedTaxi1);
        System.out.println("Booked taxi 2 " + storedTaxi1);


        StoredTaxi storedTaxi2 = new StoredTaxi();
        storedTaxi2.setStatus(TaxiStatus.OFFLINE);
        storedTaxi2.setTaxiNumber("AU13A");
        storedTaxi2.setAvailable(false);
        storedTaxi2.setFromLatitude(0.00);
        storedTaxi2.setFromLongitude(0.00);
        storedTaxi2.setCurrentLatitude(0.00);
        storedTaxi2.setCurrentLongitude(0.00);
        storedTaxi2.setBookingStrategy(BookingStrategy.ALL_AREA);
        taxiResource.createTaxi(storedTaxi2);
        System.out.println("Booked taxi 3 " + storedTaxi2);


        StoredTaxi storedTaxi3 = new StoredTaxi();
        storedTaxi3.setStatus(TaxiStatus.AVAILABLE);
        storedTaxi3.setTaxiNumber("AU14N");
        storedTaxi3.setAvailable(true);
        storedTaxi3.setFromLatitude(0.00);
        storedTaxi3.setFromLongitude(0.00);
        storedTaxi2.setCurrentLatitude(0.00);
        storedTaxi2.setCurrentLongitude(0.00);
        storedTaxi3.setBookingStrategy(BookingStrategy.NEAR_BY);
        taxiResource.createTaxi(storedTaxi3);
        System.out.println("Booked taxi 4 " + storedTaxi3);


        //Get all taxis
        List<StoredTaxi> storedTaxisList = taxiResource.getAllTaxis();
        System.out.println("\n All taxis are as below");
        System.out.println();
        storedTaxisList.forEach(System.out::println);
        Assertions.assertEquals(4, storedTaxisList.size());


        //Check how many taxis are available for booking
        Assertions.assertEquals(3, storedTaxisList.stream()
                .filter(StoredTaxi::isAvailable)
                .filter(taxi -> TaxiStatus.AVAILABLE.equals(taxi.getStatus()))
                .toList().size());


        //Check how many taxi are booked and not available
        Assertions.assertEquals(0, storedTaxisList.stream()
                .filter(taxi -> !taxi.isAvailable())
                .filter(taxi -> TaxiStatus.BOOKED.equals(taxi.getStatus()))
                .toList().size());


        //Check how many taxi are offline
        Assertions.assertEquals(1, storedTaxisList.stream()
                .filter(taxi -> !taxi.isAvailable())
                .filter(taxi -> TaxiStatus.OFFLINE.equals(taxi.getStatus()))
                .toList().size());


        //Now Set taxi preference whether taxi want to accept near by and all area bookings
        System.out.println("\n All subscribed taxis are as below");
        List<StoredTaxi> taxiObserversList = (List<StoredTaxi>) taxiResource.getAllSubscribedTaxis().getEntity();
        taxiObserversList.forEach(System.out::println);


        System.out.println("\n Add bookings");
        //Add bookings
        StoredBooking storedBooking = new StoredBooking();
        storedBooking.setId(1L);
        storedBooking.setStatus(BookingStatus.PENDING);
        storedBooking.setBookingId(123121L);
        storedBooking.setFromLatitude(0.00);
        storedBooking.setFromLongitude(0.00);
        storedBooking.setToLatitude(10.00);
        storedBooking.setToLongitude(10.00);
        Date startDate = Date.from(LocalDateTime.now().plusMinutes(22).atZone(ZoneId.systemDefault()).toInstant());
        storedBooking.setStartTime(startDate);
        Date endDate = Date.from(LocalDateTime.now().plusMinutes(30).atZone(ZoneId.systemDefault()).toInstant());
        storedBooking.setEndTime(endDate);
        storedBooking.setFare(10);
        bookingResource.createBooking(storedBooking);
        System.out.println("Booking booked 1 " + storedBooking);


        //Booking near to the taxi range
        StoredBooking storedBooking1 = new StoredBooking();
        storedBooking1.setId(2L);
        storedBooking1.setStatus(BookingStatus.PENDING);
        storedBooking1.setBookingId(123122L);
        storedBooking1.setFromLatitude(12.00);
        storedBooking1.setFromLongitude(12.00);
        storedBooking1.setToLatitude(0.00);
        storedBooking1.setToLongitude(0.00);
        storedBooking1.setStartTime(new Date());
        Date endDate1 = Date.from(LocalDateTime.now().plusMinutes(2).atZone(ZoneId.systemDefault()).toInstant());
        storedBooking1.setEndTime(endDate1);
        storedBooking1.setFare(10);
        bookingResource.createBooking(storedBooking1);
        System.out.println("Booking booked 2 " + storedBooking1);


        StoredBooking storedBooking2 = new StoredBooking();
        storedBooking2.setId(3L);
        storedBooking2.setStatus(BookingStatus.PENDING);
        storedBooking2.setBookingId(123123L);
        storedBooking2.setFromLatitude(0.00);
        storedBooking2.setFromLongitude(0.00);
        storedBooking2.setToLatitude(10.00);
        storedBooking2.setToLongitude(10.00);
        storedBooking2.setStartTime(new Date());
        Date endDate2 = Date.from(LocalDateTime.now().plusMinutes(8).atZone(ZoneId.systemDefault()).toInstant());
        storedBooking2.setEndTime(endDate2);
        storedBooking2.setFare(10);
        bookingResource.createBooking(storedBooking2);
        System.out.println("Booking booked 3 " + storedBooking2);

        StoredBooking storedBooking3 = new StoredBooking();
        storedBooking3.setId(4L);
        storedBooking3.setStatus(BookingStatus.PENDING);
        storedBooking3.setBookingId(123124L);
        storedBooking3.setFromLatitude(0.00);
        storedBooking3.setFromLongitude(0.00);
        storedBooking3.setToLatitude(10.00);
        storedBooking3.setToLongitude(10.00);
        storedBooking3.setStartTime(new Date());
        storedBooking3.setFare(10);
        Date endDate3 = Date.from(LocalDateTime.now().plusMinutes(10).atZone(ZoneId.systemDefault()).toInstant());
        storedBooking3.setEndTime(endDate3);
        bookingResource.createBooking(storedBooking3);
        System.out.println("Booking booked 4 " + storedBooking3);

//        Thread.sleep(2000);

        //Check all taxis ranges and bookings it has all area
        System.out.println("\n Check All taxis and their available bookings");
        List<StoredTaxi> allTaxis = taxiResource.getAllTaxis();
        allTaxis.forEach(taxi -> {
            System.out.println("Taxi is " + taxi.getTaxiNumber() + " & Booking are as below \n");
            List<StoredBooking> storedBookingList =
                    (List<StoredBooking>) taxiResource.getAssignedBookingAsPerChoice(taxi.getTaxiNumber())
                            .getEntity();
            storedBookingList.forEach(System.out::println);
        });

        //Test cases for bookings
        List<StoredBooking> storedBookingList =
                (List<StoredBooking>) taxiResource.getAssignedBookingAsPerChoice(storedTaxi.getTaxiNumber())
                        .getEntity();
        Assertions.assertEquals(4, storedBookingList.size());
        Assertions.assertEquals(storedBooking.getBookingId(),
                storedBookingList.stream()
                        .filter(booking -> booking.getBookingId().equals(storedBooking.getBookingId()))
                        .findFirst()
                        .get()
                        .getBookingId());
        Assertions.assertEquals(storedBooking2.getBookingId(),
                storedBookingList.stream()
                        .filter(booking -> booking.getBookingId().equals(storedBooking2.getBookingId()))
                        .findFirst()
                        .get()
                        .getBookingId());
        Assertions.assertEquals(storedBooking3.getBookingId(),
                storedBookingList.stream()
                        .filter(booking -> booking.getBookingId().equals(storedBooking3.getBookingId()))
                        .findFirst()
                        .get()
                        .getBookingId());
        Assertions.assertEquals(storedBooking1.getBookingId(),
                storedBookingList.stream()
                        .filter(booking -> booking.getBookingId().equals(storedBooking1.getBookingId()))
                        .findFirst()
                        .get()
                        .getBookingId());


        //Same check for other taxi for nearby area
        List<StoredBooking> storedBookingList1 =
                (List<StoredBooking>) taxiResource.getAssignedBookingAsPerChoice(storedTaxi1.getTaxiNumber()).getEntity();
        Assertions.assertEquals(1, storedBookingList1.size());
        Assertions.assertEquals(storedBooking1.getBookingId(),
                storedBookingList.stream()
                        .filter(booking -> booking.getBookingId().equals(storedBooking1.getBookingId()))
                        .findFirst()
                        .get()
                        .getBookingId());

        //Below Booking is not present as taxi is not accepting booking near by
        Assertions.assertFalse(storedBookingList.stream()
                .filter(booking -> booking.getBookingId().equals(storedBooking2.getBookingId()))
                .findFirst().isEmpty());


        //Failed Selected booking for AU12N
        Assertions.assertEquals(Response.status(Response.Status.BAD_REQUEST).build().getStatus(),
                taxiResource.selectBooking("AU12N", storedBooking.getBookingId()).getStatus());

        //Completed Selected booking for AU12N
        Assertions.assertEquals(Response.ok().build()
                .getStatus(), taxiResource.selectBooking("AU12N", storedBooking1.getBookingId())
                .getStatus());
        System.out.println("\n Bookings done for all taxi till now as below should be only1 and that is AU12N \n");
        taxiResource.getAllTaxis().forEach(System.out::println);

        Assertions.assertEquals(BookingStatus.PENDING, bookingResource.getBooking(storedBooking.getBookingId()).getStatus());
        Assertions.assertEquals(BookingStatus.COMPLETED, bookingResource.getBooking(storedBooking1.getBookingId()).getStatus());

        //Check Dashboard Stats at after 1 booking
        System.out.println("\n Dashboard Stats after first booking \n" + dashboardResource.getDashboardStats().getEntity());

        //Now lets subscribe two more taxi
        //Below Booking is not present as taxi is not accepting booking all area

        //Lets Test race condition
        System.out.println("\nTesting race condition for booking " + storedBooking.getBookingId());

        testRaceCondition(storedBooking);

        System.out.println("\nRace Condition Tested Succesfully\n");
        //
//        Assertions.assertEquals(Response.ok().build()
//                .getStatus(), taxiResource.selectBooking("AU11A", storedBooking.getBookingId())
//                .getStatus());

        Assertions.assertEquals(Response.status(Response.Status.BAD_REQUEST).build().getStatus(),
                taxiResource.selectBooking("AU13A", storedBooking.getBookingId()).getStatus());


//        Assertions.assertEquals(Response.ok().build()
//                .getStatus(), taxiResource.selectBooking("AU14N", storedBooking3.getBookingId())
//                .getStatus());

        Assertions.assertEquals(Response.status(Response.Status.NOT_FOUND).build()
                .getStatus(), taxiResource.selectBooking("AU14A", storedBooking3.getBookingId())
                .getStatus());


        System.out.println("\n Bookings done for all taxi till now as below \n");
        taxiResource.getAllTaxis().forEach(System.out::println);


        System.out.println("\n Dashboard Stats after all booking \n" + dashboardResource.getDashboardStats().getEntity());


        //Now set availability for one taxi to true to book again
        System.out.println("\n Setting one taxi availability to true and eligible booking \n" + storedTaxi2.getTaxiNumber());
        taxiResource.updateTaxiAvailability(storedTaxi2.getTaxiNumber(), true, TaxiStatus.AVAILABLE);
        //We have cron job to schedule pending bookings again so here we will push manually again
        bookingService.publishBooking(storedBooking2);
        StoredTaxi finalTaxi = taxiService.getTaxi(storedTaxi2.getTaxiNumber());
        Assertions.assertEquals(Response.ok().build()
                .getStatus(), taxiResource.selectBooking(finalTaxi.getTaxiNumber(), storedBooking2.getBookingId())
                .getStatus());

        StoredTaxi taxiLast = taxiResource.getTaxi(storedTaxi2.getTaxiNumber());
        StoredBooking bookinLast = bookingResource.getBooking(storedBooking2.getBookingId());
        verifyFinalTaxiStatus(taxiLast, TaxiStatus.BOOKED);
        verifyFinalBookingStatus(bookinLast, BookingStatus.COMPLETED);


        System.out.println("\n Bookings done for all taxi till now after changing status as below \n");
        taxiResource.getAllTaxis().forEach(System.out::println);


        System.out.println("\n Dashboard Stats after all booking \n" +
                dashboardResource.getDashboardStats().getEntity());

        System.out.println("\n Dashboard Stats after all booking for one taxi \n" + storedTaxi2.getTaxiNumber() + " " +
                dashboardResource.getAllBookingsByTaxiNumber(storedTaxi2.getTaxiNumber()).getEntity());


        Date queryStartDate = Date.from(LocalDateTime.now().minusMinutes(10).atZone(ZoneId.systemDefault()).toInstant());
        Date queryEndDate = Date.from(LocalDateTime.now().plusMinutes(20).atZone(ZoneId.systemDefault()).toInstant());

        System.out.println("\n Dashboard Stats after all booking in time range of curr date -10 end +20 min so 3 bookings \n" +
                dashboardResource.getAllBookingsForTimeRange(queryStartDate, queryEndDate).getEntity());


        queryStartDate = Date.from(LocalDateTime.now().plusMinutes(20).atZone(ZoneId.systemDefault()).toInstant());
        queryEndDate = Date.from(LocalDateTime.now().plusMinutes(40).atZone(ZoneId.systemDefault()).toInstant());

        System.out.println("\n Dashboard Stats after all booking in time range of curr > 20 min so 3 bookings \n" +
                dashboardResource.getAllBookingsForTimeRange(queryStartDate, queryEndDate).getEntity());

        System.out.println("\n Dashboard Stats after all booking done in location range 0 to 10 \n" +
                dashboardResource.getAllBookingsForLocation(0.00, 10.00, 0.00, 10.00).getEntity());


        System.out.println("\n Dashboard Stats after all booking done in location range 10 to 20 \n" +
                dashboardResource.getAllBookingsForLocation(10.00, 20.00, 10.00, 30.00).getEntity());

    }

    private void verifyFinalTaxiStatus(StoredTaxi taxi, TaxiStatus expectedStatus) {
        StoredTaxi updatedTaxi = taxiResource.getTaxi(taxi.getTaxiNumber());
        Assertions.assertEquals(expectedStatus, updatedTaxi.getStatus());
    }


    private void verifyFinalBookingStatus(StoredBooking booking, BookingStatus expectedStatus) {
        StoredBooking updatedBooking = bookingResource.getBooking(booking.getBookingId());
        Assertions.assertEquals(expectedStatus, updatedBooking.getStatus());
    }

    private void testRaceCondition(StoredBooking raceBooking) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        // Thread 1 tries to assign taxi1 to the booking
        Thread thread1 = new Thread(() -> {
            try {
                latch.await(); // Wait until both threads are ready
                Response response = taxiResource.selectBooking("AU11A", raceBooking.getBookingId());
                if (response.getStatus() == Response.ok().build().getStatus()) {
                    System.out.println("Thread 1 have booked taxi and taxi number is " + "AU11A");
                    Assertions.assertEquals(Response.ok().build()
                                    .getStatus(), response.getStatus(),
                            "Thread 1 should have successfully assigned taxi1 to the booking " + raceBooking.getBookingId());
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // Thread 2 tries to assign taxi2 to the same booking
        Thread thread2 = new Thread(() -> {
            try {
                latch.await(); // Wait until both threads are ready
                Response response = taxiResource.selectBooking("AU14N", raceBooking.getBookingId());
                if (response.getStatus() == Response.ok().build().getStatus()) {
                    System.out.println("Thread 2 have booked taxi and taxi number is " + "AU14N");
                    Assertions.assertEquals(Response.ok().build()
                                    .getStatus(), response.getStatus(),
                            "Thread 2 should have successfully assigned taxi2 to the booking." + raceBooking.getBookingId());
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });


        // Start both threads
        thread1.start();
        thread2.start();

        // Release the latch, allowing both threads to run simultaneously
        latch.countDown();

        // Wait for both threads to complete
        thread1.join();
        thread2.join();
    }
}
