package com.stacy.eventservice;

import com.stacy.eventservice.dto.EventResponse;
import com.stacy.eventservice.model.Event;
import com.stacy.eventservice.repository.EventRepository;
import com.stacy.eventservice.service.EventService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import javax.swing.text.html.Option;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventService eventService;

    @Test
    void getEventById_shouldReturnEvent_whenEventExists() {

        Event event = new Event(1L,"Java Conference","Kolkata",175);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        EventResponse response = eventService.getEventById(1L);

        assertEquals(1L,response.getId());
        assertEquals("Java Conference",response.getName());
        assertEquals("Kolkata",response.getLocation());
        assertEquals(175,response.getAvailableSeats());
    }

    @Test
    void getEventById_shouldThrowException_whenEventDoesNotExists() {

        when(eventRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,()-> eventService.getEventById(999L));
    }

    @Test
    void createEvent_shouldSaveEvent(){

        Event event = new Event(null,"Spring Boot Conference","Kolkata",250);
        Event createdEvent = new Event(1L,"Spring Boot Conference","Kolkata",250);

        when(eventRepository.save(event)).thenReturn(createdEvent);

        Event result = eventService.createEvent(event);

        assertEquals(1L,result.getId());
        assertEquals("Spring Boot Conference",result.getName());
    }

    @Test
    void updateEvent_shouldUpdatedEventExists(){

        Event existingEvent = new Event(1L,"Old Event","Kolkata",190);
        Event updatedEvent = new Event(null,"New Event","Delhi",200);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(existingEvent));
        when(eventRepository.save(existingEvent)).thenReturn(existingEvent);

        Event result = eventService.updateEvent(1L,updatedEvent);

        assertEquals("Delhi",result.getLocation());
        assertEquals("New Event",result.getName());
        assertEquals(200,result.getAvailableSeats());
    }

    @Test
    void deleteEvent_shouldDeleteEvent_whenEventExists() {
        when(eventRepository.existsById(1L)).thenReturn(true);

        eventService.deleteEvent(1L);

        verify(eventRepository).deleteById(1L);
    }

    @Test
    void deleteEvent_shouldThrowException_whenEventDoesNotExists() {
        when(eventRepository.existsById(999L)).thenReturn(false);

        assertThrows(ResponseStatusException.class,()->eventService.deleteEvent(999L));

        verify(eventRepository, never()).deleteById(999L);
    }

    @Test
    void updateEvent_shouldThrowException_whenEventDoesNotExists() {
        Event updatedEvent = new Event(null,"New Event","Delhi",200);

        when(eventRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,()->eventService.updateEvent(999L,updatedEvent));

        verify(eventRepository,never()).save(updatedEvent);

    }


}
