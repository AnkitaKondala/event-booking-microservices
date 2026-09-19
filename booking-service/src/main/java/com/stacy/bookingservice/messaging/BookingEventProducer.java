package com.stacy.bookingservice.messaging;

import com.stacy.bookingservice.event.BookingCreatedEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class BookingEventProducer {

    private final RabbitTemplate rabbitTemplate;

    public BookingEventProducer(RabbitTemplate rabbitTemplate){
        this.rabbitTemplate=rabbitTemplate;
    }

    public void publishBookingCreated(BookingCreatedEvent event){
        rabbitTemplate.convertAndSend("booking.queue",event);
    }
}
