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
import java.util.*;

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
        updateAvailabilityForBooking(cottage, req.startDate, req.endDate, covering);

        return savedBooking;
    }

    private void updateAvailabilityForBooking(Cottage cottage, LocalDate startDate, LocalDate endDate,
                                              List<Availability> coveringAvailabilities) {
        for (Availability availability : coveringAvailabilities) {
            LocalDate availStart = availability.getAvailableStart();
            LocalDate availEnd = availability.getAvailableEnd();

            // Delete the original availability
            availabilityRepo.delete(availability);

            // Create availability before booking (if needed)
            if (availStart.isBefore(startDate)) {
                Availability beforeBooking = new Availability();
                beforeBooking.setCottage(cottage);
                beforeBooking.setAvailableStart(availStart);
                beforeBooking.setAvailableEnd(startDate.minusDays(1));
                availabilityRepo.save(beforeBooking);
            }

            // Create availability after booking (if needed)
            if (availEnd.isAfter(endDate)) {
                Availability afterBooking = new Availability();
                afterBooking.setCottage(cottage);
                afterBooking.setAvailableStart(endDate.plusDays(1));
                afterBooking.setAvailableEnd(availEnd);
                availabilityRepo.save(afterBooking);
            }
        }
    }

    public Map<Integer, Map<String, Long>> getMonthlyOverview(LocalDate startDate, LocalDate endDate) {

        // Pre-fill map with all days of the month
        Map<Integer, Map<String, Long>> overview = new LinkedHashMap<>();
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            Map<String, Long> counts = new HashMap<>();
            counts.put("booked", 0L);
            counts.put("available", 0L);
            overview.put(current.getDayOfMonth(), counts);
            current = current.plusDays(1);
        }

        // Bookings: expand each range into days
        List<Booking> bookings = bookingRepo
                .findByStartDateLessThanEqualAndEndDateGreaterThanEqual(endDate, startDate);

        for (Booking booking : bookings) {
            LocalDate bookingStart = booking.getStartDate().isBefore(startDate) ? startDate : booking.getStartDate();
            LocalDate bookingEnd = booking.getEndDate().isAfter(endDate) ? endDate : booking.getEndDate();

            LocalDate d = bookingStart;
            while (!d.isAfter(bookingEnd)) {
                overview.get(d.getDayOfMonth()).merge("booked", 1L, Long::sum);
                d = d.plusDays(1);
            }
        }

        // Availabilities: expand each range into days
        List<Availability> availabilities = availabilityRepo
                .findByAvailableStartLessThanEqualAndAvailableEndGreaterThanEqual(endDate, startDate);

        for (Availability av : availabilities) {
            LocalDate availStart = av.getAvailableStart().isBefore(startDate) ? startDate : av.getAvailableStart();
            LocalDate availEnd = av.getAvailableEnd().isAfter(endDate) ? endDate : av.getAvailableEnd();

            LocalDate d = availStart;
            while (!d.isAfter(availEnd)) {
                overview.get(d.getDayOfMonth()).merge("available", 1L, Long::sum);
                d = d.plusDays(1);
            }
        }

        return overview;
    }

    public Booking updateBooking(Long id, BookingRequest req) {
        Optional<Booking> booking = bookingRepo.findById(id);
        if(booking.isPresent()) {
            Availability availability = new Availability(booking.get().getStartDate(), booking.get().getEndDate(), booking.get().getCottage());
            availabilityRepo.save(availability);
            bookingRepo.delete(booking.get());
        }
        return createBooking(req);
    }
}

