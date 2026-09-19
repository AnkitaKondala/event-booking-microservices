package com.stacy.bookingservice.controller;

import com.stacy.bookingservice.dto.BookingRequest;
import com.stacy.bookingservice.dto.BookingResponse;
import com.stacy.bookingservice.model.Booking;
import com.stacy.bookingservice.service.BookingService;
import com.stacy.bookingservice.service.RedisLockService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BookingController {

    @Autowired
    BookingService bookingService;

    @Autowired
    RedisLockService redisLockService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @GetMapping("/bookings")
    public List<Booking> getBookings() {
        return bookingService.getBookings();
    }

    @GetMapping("/bookings/{id}")
    public BookingResponse getBookingById(@PathVariable Long id){
        return bookingService.getBookingById(id);
    }

    @PostMapping("/bookings")
    public BookingResponse createBooking(@Valid @RequestBody BookingRequest bookingRequest){

        Booking booking = new Booking(null, bookingRequest.getEventId(), bookingRequest.getCustomerName(), bookingRequest.getNumberOfSeats());
        return bookingService.createBooking(booking);
    }

    @PutMapping("/bookings/{id}")
    public BookingResponse updateBooking(@PathVariable Long id,@Valid @RequestBody BookingRequest request) {
        Booking booking = new Booking(null, request.getEventId(), request.getCustomerName(), request.getNumberOfSeats());
        return bookingService.updateBooking(id,booking);
    }

    @DeleteMapping("/bookings/{id}")
    public void deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
    }

    @GetMapping("/bookings/redis-test")
    public String redisTest(){
        redisTemplate.opsForValue().set("test-key","Redis is  working!");

        return redisTemplate.opsForValue().get("test-key");
    }

    @GetMapping("/bookings/lock-test/{eventId}")
    public String testLock(@PathVariable Long eventId){

        String lockKey = "lock:event" + eventId;

        String lockValue = redisLockService.acquireLock(lockKey);

        if(lockValue == null) {
            return "Could not acquire lock";
        }

        try {
            Thread.sleep(5000);
            return "Lock acquired successfully";
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Interrupted";
        }finally {
            redisLockService.releaseLock(lockKey, lockValue);
        }
    }

}
