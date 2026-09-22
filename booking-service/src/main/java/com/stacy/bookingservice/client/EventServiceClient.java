package com.stacy.bookingservice.client;

import com.stacy.bookingservice.dto.EventResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;

@Component
public class EventServiceClient {

    private final RestClient restClient;

    public EventServiceClient(@Value("${event-service.url}") String baseUrl) {

        var httpClient = java.net.http.HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(2)).build();

        var requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(Duration.ofSeconds(3));

        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
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

    @Retry(name="eventService")
    @CircuitBreaker(name = "eventService",fallbackMethod = "reserveSeatsFallback")
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

    private EventResponse reserveSeatsFallback(
            Long eventId,
            int numberOfSeats,
            Throwable throwable) {

        throw new ResponseStatusException(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Event Service is currently unavailable. Please try again later."
        );
    }
}
