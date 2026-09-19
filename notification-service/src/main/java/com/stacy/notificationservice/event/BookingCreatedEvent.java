package com.stacy.notificationservice.event;

public class BookingCreatedEvent
{
    private Long bookingId;
    private Long eventId;
    private String customerName;
    private int numberOfSeats;

    public BookingCreatedEvent() {
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
