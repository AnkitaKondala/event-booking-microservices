package com.stacy.bookingservice.client;

import com.stacy.bookingservice.dto.EventResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

@Component
public class EventServiceClient {

    private final RestClient restClient;

    public EventServiceClient(){
        this.restClient = RestClient.builder().baseUrl("http://localhost:8080").build();
    }

    public EventResponse getEventById(Long eventId) {

        try {
            return restClient
                    .get()
                    .uri("/events/{id}", eventId)
                    .retrieve()
                    .body(EventResponse.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Event not found");
        }
    }
}
