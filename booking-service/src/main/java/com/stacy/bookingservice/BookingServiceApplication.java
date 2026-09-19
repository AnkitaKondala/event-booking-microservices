package com.stacy.bookingservice;

import org.springframework.amqp.core.Queue;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BookingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookingServiceApplication.class, args);
	}

    @Bean
    public ApplicationRunner rabbitTest(Queue bookingQueue) {
        return args -> {
            System.out.println(">>> RabbitMQ Queue Bean: " + bookingQueue.getName());
        };
    }

}
