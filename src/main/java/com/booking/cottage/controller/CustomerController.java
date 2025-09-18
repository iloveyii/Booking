package com.booking.cottage.controller;

import com.booking.cottage.model.Customer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.booking.cottage.repository.CustomerRepository;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerRepository repo;

    public CustomerController(CustomerRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Customer> all() { return repo.findAll(); }

    @PostMapping
    public ResponseEntity<Customer> create(@RequestBody Customer customer) {
        Customer saved = repo.save(customer);
        return ResponseEntity.status(201).body(saved);
    }
}
