package com.booking.cottage.service;

import com.booking.cottage.dto.BookingRequest;
import com.booking.cottage.model.Customer;
import com.booking.cottage.repository.CustomerRepository;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer findOrCreateCustomer(BookingRequest request) {
        if (request.customerId != null) {
            // Try to find by customerId first
            return customerRepository.findById(request.customerId)
                    .orElseGet(() -> findOrCreateByEmail(request));
        } else {
            // No customerId provided, try to find by email or create new
            return findOrCreateByEmail(request);
        }
    }

    private Customer findOrCreateByEmail(BookingRequest request) {
        return customerRepository.findByEmail(request.email)
                .orElseGet(() -> createNewCustomer(request));
    }

    private Customer createNewCustomer(BookingRequest request) {
        Customer newCustomer = new Customer();
        newCustomer.setName("");
        newCustomer.setEmail(request.email);
        newCustomer.setPhone("");

        return customerRepository.save(newCustomer);
    }
}
