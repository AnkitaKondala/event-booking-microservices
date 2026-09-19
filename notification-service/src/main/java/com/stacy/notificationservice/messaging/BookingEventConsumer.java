package com.stacy.notificationservice.messaging;

import com.stacy.notificationservice.event.BookingCreatedEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class BookingEventConsumer {

    @RabbitListener(queues = "booking.queue")
    public void handleBookingCreated(BookingCreatedEvent event){

        System.out.println("===== BOOKING NOTIFICATION =====");
        System.out.println("Booking ID: " + event.getBookingId());
        System.out.println("Customer: " + event.getCustomerName());
        System.out.println("Event ID: " + event.getEventId());
        System.out.println("Seats: " + event.getNumberOfSeats());
        System.out.println("================================");

    }
}
