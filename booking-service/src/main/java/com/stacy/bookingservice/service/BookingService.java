package com.stacy.bookingservice.service;

import com.stacy.bookingservice.client.EventServiceClient;
import com.stacy.bookingservice.dto.BookingRequest;
import com.stacy.bookingservice.dto.BookingResponse;
import com.stacy.bookingservice.dto.EventResponse;
import com.stacy.bookingservice.model.Booking;
import com.stacy.bookingservice.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class BookingService {

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    EventServiceClient eventServiceClient;

    @Autowired
    RedisLockService redisLockService;

    public List<Booking> getBookings(){
        return bookingRepository.findAll();
    }

    public BookingResponse getBookingById(Long id){
        Booking booking = bookingRepository.findById(id).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Booking not found"));

        return toResponse(booking);
    }

    public BookingResponse createBooking(Booking booking) {

        String lockKey = "lock:event:" + booking.getEventId();

        String lockValue = redisLockService.acquireLock(lockKey);

        if(lockValue == null){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Another booking is currently in progress for this event.");
        }


        try {
            eventServiceClient.reserveSeats(booking.getEventId(), booking.getNumberOfSeats());

            Booking savedBooking = bookingRepository.save(booking);

            return toResponse(savedBooking);
        }
        finally {
            redisLockService.releaseLock(lockKey,lockValue);
        }

    }

    public BookingResponse updateBooking(Long id, Booking booking){

        Booking existingBooking = bookingRepository.findById(id).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Booking not found"));

        existingBooking.setEventId(booking.getEventId());
        existingBooking.setCustomerName(booking.getCustomerName());
        existingBooking.setNumberOfSeats(booking.getNumberOfSeats());

        return toResponse(bookingRepository.save(existingBooking));
    }

    public void deleteBooking(Long id){
        if(!bookingRepository.existsById(id)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Booking not found");
        }

        bookingRepository.deleteById(id);
    }

    public BookingResponse toResponse(Booking booking) {
        return new BookingResponse(booking.getId(), booking.getEventId(), booking.getCustomerName(), booking.getNumberOfSeats());
    }
}
