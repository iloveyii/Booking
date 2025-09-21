package com.booking.cottage.controller;

import com.booking.cottage.service.BookingService;
import com.booking.cottage.util.Helper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.booking.cottage.repository.*;
import com.booking.cottage.model.*;
import com.booking.cottage.ApiException;

@RestController
@RequestMapping("/api/availabilities")
public class AvailabilityController {

    private final AvailabilityRepository repo;
    private final CottageRepository cottageRepo;
    private final BookingRepository bookingRepository;

    public AvailabilityController(AvailabilityRepository repo, CottageRepository cottageRepo, BookingRepository bookingRepository) {
        this.repo = repo;
        this.cottageRepo = cottageRepo;
        this.bookingRepository = bookingRepository;
    }

    @GetMapping
    public List<Availability> all() { return repo.findAll(); }

    @GetMapping("/{id}")
    public List<String> getAvailabilities(@PathVariable Long id) {
        List<Availability> availabilities = repo.findByCottageId(id);
        List<String> stringList = new ArrayList<>();
        for(Availability availability: availabilities) {
            stringList.addAll(Helper.getAvailableDates(availability.getAvailableStart(), availability.getAvailableEnd()));
        }
        return stringList;
    }

    @GetMapping("/{id}/{bookingId}")
    public List<String> getAvailabilitiesAndSelfBooked(@PathVariable Long id, @PathVariable Long bookingId) {
        List<Availability> availabilities = repo.findByCottageId(id);
        List<String> stringList = new ArrayList<>();
        for(Availability availability: availabilities) {
            stringList.addAll(Helper.getAvailableDates(availability.getAvailableStart(), availability.getAvailableEnd()));
        }
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        booking.ifPresent(b -> stringList.addAll(Helper.getAvailableDates(b.getStartDate(), b.getEndDate())));

        return stringList;
    }

    @PostMapping
    public ResponseEntity<Availability> create(@RequestBody Availability availability) {
        // make sure cottage exists (incoming JSON must include cottage.id)
        if (availability.getCottage() == null || availability.getCottage().getId() == null) {
            throw new ApiException("Cottage id is required in availability");
        }
        Cottage c = cottageRepo.findById(availability.getCottage().getId())
                .orElseThrow(() -> new ApiException("Cottage not found"));
        availability.setCottage(c);
        Availability saved = repo.save(availability);
        return ResponseEntity.status(201).body(saved);
    }
}
