package com.booking.cottage.controller;

import com.booking.cottage.repository.CottageRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.booking.cottage.model.*;
import java.util.List;

@RestController
@RequestMapping("/api/cottages")
public class CottageController {

    private final CottageRepository repo;

    public CottageController(CottageRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Cottage> all() {
        return repo.findAll();
    }

    @PostMapping
    public ResponseEntity<Cottage> create(@RequestBody Cottage cottage) {
        Cottage saved = repo.save(cottage);
        return ResponseEntity.status(201).body(saved);
    }
}

