package com.stacy.eventservice.controller;

import com.stacy.eventservice.dto.EventRequest;
import com.stacy.eventservice.dto.EventResponse;
import com.stacy.eventservice.model.Event;
import com.stacy.eventservice.service.EventService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class EventController {

    @Autowired
    EventService eventService;

    @GetMapping("/events")
    public List<Event> getEvents() {
        return eventService.getEvents();
    }

    @PostMapping("/events")
    public ResponseEntity<EventResponse> createEvent(@Valid @RequestBody EventRequest request) {


        Event event = new Event(null,request.getName(), request.getLocation(), request.getAvailableSeats());

        Event createdEvent = eventService.createEvent(event);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(eventService.toResponse(createdEvent));
    }

    @GetMapping("/events/{id}")
    public EventResponse getEventById(@PathVariable Long id){
        return eventService.getEventById(id);
    }

    @DeleteMapping("/events/{id}")
    public void deleteEvent(@PathVariable Long id){
        eventService.deleteEvent(id);
    }

    @PutMapping("/events/{id}")
    public EventResponse updateEvent(
            @PathVariable Long id,
            @Valid @RequestBody EventRequest request
    ) {
        Event event = new Event(null, request.getName(), request.getLocation(), request.getAvailableSeats());

        return eventService.toResponse(eventService.updateEvent(id,event));
    }

    @PostMapping("/events/{id}/reserve")
    public EventResponse reserveEvent(@PathVariable Long id, @RequestParam int numberOfSeats) {
        return eventService.reserveSeats(id,numberOfSeats);
    }
}
