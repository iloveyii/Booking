package com.booking.cottage.controller;

import com.booking.cottage.dto.BookingRequest;
import com.booking.cottage.model.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.booking.cottage.service.BookingService;
import com.booking.cottage.repository.BookingRepository;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

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
        Pageable pageable = PageRequest.of(0, 5, Sort.by("startDate").ascending());
        Page<Booking> bookings = bookingRepo.findAll(pageable);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/recent")
    public ResponseEntity<?> top5() {
        LocalDate today = LocalDate.now();
        List<Booking> bookings = bookingRepo.findByStartDateGreaterThanEqualOrEndDateGreaterThanEqualOrderByStartDate(today, today);
        return ResponseEntity.ok(bookings);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody BookingRequest req) {
        Booking saved = bookingService.createBooking(req);
        return ResponseEntity.status(201).body(saved);
    }

    @GetMapping("/calendar/{year}/{month}")
    public Map<Integer, Map<String, Long>> getMonthlyOverview(
            @PathVariable int year,
            @PathVariable int month) {

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        return bookingService.getMonthlyOverview(startDate, endDate);
    }
}

