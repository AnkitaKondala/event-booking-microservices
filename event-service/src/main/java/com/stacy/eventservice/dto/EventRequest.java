package com.stacy.eventservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class EventRequest {

    @NotBlank(message = "Event name is required")
    private String name;

    @NotBlank(message = "Event location is required")
    private String location;

    @Min(value = 1, message = "Available seats must be atleast 1")
    private int availableSeats;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }
}
