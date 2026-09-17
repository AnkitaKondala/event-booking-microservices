package com.stacy.bookingservice.dto;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class BookingRequest {

    @NotNull(message = "Event ID is required")
    @Min(value = 1,message = "Event ID must be greater than 0")
    private Long eventId;

    @NotBlank(message = "Customer Name is required")
    private String customerName;

    @Min(value = 1, message = "Number of seats must be atleast 1")
    private int numberOfSeats;

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
