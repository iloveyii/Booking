package com.booking.cottage.controller;

import com.booking.cottage.dto.BookingRequest;
import com.booking.cottage.model.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
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

    @GetMapping("/{page}/{size}")
    public ResponseEntity<?> all(@PathVariable int page,
                                 @PathVariable int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("startDate").descending());
        Page<Booking> bookings = bookingRepo.findAll(pageable);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/recent")
    public ResponseEntity<?> top5() {
        LocalDate today = LocalDate.now();
        List<Booking> bookings = bookingRepo.findByStartDateGreaterThanEqualOrEndDateGreaterThanEqualOrderByStartDate(today, today);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable Long id) {
        return bookingRepo.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Booking with id " + id + " not found")));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody BookingRequest req) {
        Booking saved = bookingService.createBooking(req);
        return ResponseEntity.status(201).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody BookingRequest req) {
        Booking updated = bookingService.updateBooking(id, req);
        if (updated == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Booking with id " + id + " not found"));
        }
        return ResponseEntity.ok(updated); // 200 OK
    }

    // Get bookings & avail month wise
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

