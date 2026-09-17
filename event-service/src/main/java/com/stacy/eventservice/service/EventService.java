package com.stacy.eventservice.service;

import com.stacy.eventservice.dto.EventResponse;
import com.stacy.eventservice.model.Event;
import com.stacy.eventservice.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class EventService {

    @Autowired
    EventRepository eventRepository;

    public List<Event> getEvents() {
        return eventRepository.findAll();
    }

    public Event createEvent(Event event) {
        return eventRepository.save(event);
    }

    public EventResponse getEventById(Long id){
        Event event = eventRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Event Not Fount"));
        return toResponse(event);
    }

    public void deleteEvent(Long id){
        if(!eventRepository.existsById(id)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Event not found");
        }

        eventRepository.deleteById(id);
    }

    public Event updateEvent(Long id, Event updatedEvent) {
        Event existingEvent = eventRepository.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Event not found"));

        existingEvent.setName(updatedEvent.getName());
        existingEvent.setLocation(updatedEvent.getLocation());
        existingEvent.setAvailableSeats(updatedEvent.getAvailableSeats());

        return eventRepository.save(existingEvent);
    }

    public EventResponse toResponse(Event event) {
        return new EventResponse(event.getId(), event.getName(), event.getLocation(), event.getAvailableSeats());
    }
}
