package com.stacy.bookingservice.event;

public class BookingCreatedEvent {

    private Long bookingId;
    private Long eventId;
    private String customerName;
    private int numberOfSeats;

    public BookingCreatedEvent(){

    }

    public BookingCreatedEvent(Long bookingId, Long eventId, String customerName, int numberOfSeats) {
        this.bookingId = bookingId;
        this.eventId = eventId;
        this.customerName = customerName;
        this.numberOfSeats = numberOfSeats;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public Long getEventId() {
        return eventId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public int getNumberOfSeats() {
        return numberOfSeats;
    }
}
