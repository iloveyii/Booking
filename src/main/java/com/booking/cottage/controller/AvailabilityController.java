package com.booking.cottage.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.booking.cottage.repository.*;
import com.booking.cottage.model.*;
import com.booking.cottage.ApiException;

@RestController
@RequestMapping("/api/availabilities")
public class AvailabilityController {

    private final AvailabilityRepository repo;
    private final CottageRepository cottageRepo;

    public AvailabilityController(AvailabilityRepository repo, CottageRepository cottageRepo) {
        this.repo = repo;
        this.cottageRepo = cottageRepo;
    }

    @GetMapping
    public List<Availability> all() { return repo.findAll(); }

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
