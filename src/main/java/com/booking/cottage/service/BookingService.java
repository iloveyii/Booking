package com.booking.cottage.service;

import com.booking.cottage.ApiException;
import com.booking.cottage.dto.BookingRequest;
import com.booking.cottage.model.Availability;
import com.booking.cottage.model.Booking;
import com.booking.cottage.model.Cottage;
import com.booking.cottage.model.Customer;
import com.booking.cottage.repository.AvailabilityRepository;
import com.booking.cottage.repository.CottageRepository;
import com.booking.cottage.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.booking.cottage.repository.BookingRepository;

import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepo;
    private final CottageRepository cottageRepo;
    private final CustomerRepository customerRepo;
    private final AvailabilityRepository availabilityRepo;

    public BookingService(BookingRepository bookingRepo,
                          CottageRepository cottageRepo,
                          CustomerRepository customerRepo,
                          AvailabilityRepository availabilityRepo) {
        this.bookingRepo = bookingRepo;
        this.cottageRepo = cottageRepo;
        this.customerRepo = customerRepo;
        this.availabilityRepo = availabilityRepo;
    }

    @Transactional
    public Booking createBooking(BookingRequest req) {
        if (req.startDate == null || req.endDate == null) {
            throw new ApiException("Start and end dates are required");
        }
        if (req.startDate.isAfter(req.endDate)) {
            throw new ApiException("startDate must be before or equal to endDate");
        }

        Cottage cottage = cottageRepo.findById(req.cottageId)
                .orElseThrow(() -> new ApiException("Cottage not found"));
        Customer customer = customerRepo.findById(req.customerId)
                .orElseThrow(() -> new ApiException("Customer not found"));

        // 1) check availability table: a record must fully cover the requested range
        List<Availability> covering = availabilityRepo.findCoveringAvailability(cottage.getId(), req.startDate, req.endDate);
        if (covering.isEmpty()) {
            throw new ApiException("Cottage not available for the selected dates (no availability entry covers the range)");
        }

        // 2) check overlapping bookings
        List<Booking> overlapping = bookingRepo.findOverlappingBookings(cottage.getId(), req.startDate, req.endDate);
        if (!overlapping.isEmpty()) {
            throw new ApiException("Cottage already booked for the selected dates");
        }

        Booking booking = new Booking(cottage, customer, req.startDate, req.endDate);
        return bookingRepo.save(booking);
    }
}

