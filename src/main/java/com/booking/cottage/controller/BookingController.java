package com.booking.cottage.controller;

import com.booking.cottage.dto.BookingRequest;
import com.booking.cottage.model.Booking;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.booking.cottage.service.BookingService;
import com.booking.cottage.repository.BookingRepository;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final BookingRepository bookingRepo;

    public BookingController(BookingService bookingService, BookingRepository bookingRepo) {
        this.bookingService = bookingService;
        this.bookingRepo = bookingRepo;
    }

    @GetMapping
    public ResponseEntity<?> all() {
        return ResponseEntity.ok(bookingRepo.findAll());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody BookingRequest req) {
        Booking saved = bookingService.createBooking(req);
        return ResponseEntity.status(201).body(saved);
    }
}

