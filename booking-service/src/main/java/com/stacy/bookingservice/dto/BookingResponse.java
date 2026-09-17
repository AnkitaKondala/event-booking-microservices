package com.stacy.bookingservice.dto;

public class BookingResponse {

    private Long id;
    private Long eventId;
    private String customerName;
    private int numberOfSeats;


    public BookingResponse(Long id, Long eventId, String customerName, int numberOfSeats) {
        this.id = id;
        this.eventId = eventId;
        this.customerName = customerName;
        this.numberOfSeats = numberOfSeats;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public int getNumberOfSeats() {
        return numberOfSeats;
    }

    public void setNumberOfSeats(int numberOfSeats) {
        this.numberOfSeats = numberOfSeats;
    }
}
