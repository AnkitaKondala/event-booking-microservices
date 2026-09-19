package com.stacy.bookingservice.client;

import com.stacy.bookingservice.dto.EventResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

@Component
public class EventServiceClient {

    private final RestClient restClient;

    public EventServiceClient(@Value("${event-service.url}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
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

    public EventResponse reserveSeats(Long eventId,int numberOfSeats) {

        try {
            return restClient
                    .post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/events/{id}/reserve")
                            .queryParam("numberOfSeats",numberOfSeats)
                            .build(eventId))
                    .retrieve()
                    .body(EventResponse.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Event not found");
        }
    }
}
