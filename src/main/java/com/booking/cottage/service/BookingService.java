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

import java.time.LocalDate;
import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepo;
    private final CottageRepository cottageRepo;
    private final AvailabilityRepository availabilityRepo;
    private final CustomerService customerService;

    public BookingService(BookingRepository bookingRepo,
                          CottageRepository cottageRepo,
                          CustomerRepository customerRepo,
                          AvailabilityRepository availabilityRepo,
                          CustomerService customerService) {
        this.bookingRepo = bookingRepo;
        this.cottageRepo = cottageRepo;
        this.availabilityRepo = availabilityRepo;
        this.customerService = customerService;
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
        Customer customer = customerService.findOrCreateCustomer(req);

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

        Booking booking = new Booking(cottage, customer, req.startDate, req.endDate, req.guests);
        Booking savedBooking = bookingRepo.save(booking);

        // 3) Update availability records
        updateAvailabilityForBooking(cottage.getId(), req.startDate, req.endDate, covering);

        return  savedBooking;
    }

    private void updateAvailabilityForBooking(Long cottageId, LocalDate startDate, LocalDate endDate,
                                              List<Availability> coveringAvailabilities) {
        for (Availability availability : coveringAvailabilities) {
            // Case 1: Booking exactly matches availability period
            if (availability.getAvailableStart().equals(startDate) && availability.getAvailableEnd().equals(endDate)) {
                availabilityRepo.delete(availability); // Remove the entire availability
            }
            // Case 2: Booking starts at availability start but ends earlier
            else if (availability.getAvailableStart().equals(startDate) && endDate.isBefore(availability.getAvailableEnd())) {
                // Create new availability for the remaining period
                Availability newAvailability = new Availability();
                newAvailability.getCottage().setId(cottageId);
                newAvailability.setAvailableStart(endDate.plusDays(1));
                newAvailability.setAvailableEnd(availability.getAvailableEnd());
                availabilityRepo.save(newAvailability);

                availabilityRepo.delete(availability); // Remove the original availability
            }
            // Case 3: Booking ends at availability end but starts later
            else if (availability.getAvailableEnd().equals(endDate) && startDate.isAfter(availability.getAvailableStart())) {
                // Update current availability to end before booking starts
                availability.setAvailableEnd(startDate.minusDays(1));
                availabilityRepo.save(availability);
            }
            // Case 4: Booking is in the middle of availability period
            else if (startDate.isAfter(availability.getAvailableStart()) && endDate.isBefore(availability.getAvailableEnd())) {
                // Split into two availability periods

                // First part: before booking
                Availability firstPart = new Availability();
                firstPart.getCottage().setId(cottageId);
                firstPart.setAvailableStart(availability.getAvailableStart());
                firstPart.setAvailableEnd(startDate.minusDays(1));
                availabilityRepo.save(firstPart);

                // Second part: after booking
                Availability secondPart = new Availability();
                secondPart.getCottage().setId(cottageId);
                secondPart.setAvailableStart(endDate.plusDays(1));
                secondPart.setAvailableEnd(availability.getAvailableEnd());
                availabilityRepo.save(secondPart);

                availabilityRepo.delete(availability); // Remove the original availability
            }
        }
    }
}

