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
import com.booking.cottage.util.Helper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.booking.cottage.repository.BookingRepository;

import java.time.LocalDate;
import java.util.*;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final CottageRepository cottageRepository;
    private final AvailabilityRepository availabilityRepository;
    private final CustomerService customerService;

    public BookingService(BookingRepository bookingRepository,
                          CottageRepository cottageRepository,
                          CustomerRepository customerRepo,
                          AvailabilityRepository availabilityRepository,
                          CustomerService customerService) {
        this.bookingRepository = bookingRepository;
        this.cottageRepository = cottageRepository;
        this.availabilityRepository = availabilityRepository;
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

        Cottage cottage = cottageRepository.findById(req.cottageId)
                .orElseThrow(() -> new ApiException("Cottage not found"));
        // before new booking
        mergeAvailabilities(cottage.getId());
        Customer customer = customerService.findOrCreateCustomer(req);

        // 1) check availability table: a record must fully cover the requested range
        List<Availability> covering = availabilityRepository.findCoveringAvailability(cottage.getId(), req.startDate, req.endDate);
        if (covering.isEmpty()) {
            throw new ApiException("Cottage not available for the selected dates (no availability entry covers the range)");
        }

        // 2) check overlapping bookings
        List<Booking> overlapping = bookingRepository.findOverlappingBookings(cottage.getId(), req.startDate, req.endDate);
        if (!overlapping.isEmpty()) {
            throw new ApiException("Cottage already booked for the selected dates");
        }

        Booking booking = new Booking(cottage, customer, req.startDate, req.endDate, req.guests);
        Booking savedBooking = bookingRepository.save(booking);

        // 3) Update availability records
        updateAvailabilityForBooking(cottage, req.startDate, req.endDate, covering);
        // after new booking
        mergeAvailabilities(cottage.getId());
        return savedBooking;
    }

    private void updateAvailabilityForBooking(Cottage cottage, LocalDate startDate, LocalDate endDate,
                                              List<Availability> coveringAvailabilities) {
        for (Availability availability : coveringAvailabilities) {
            LocalDate availStart = availability.getAvailableStart();
            LocalDate availEnd = availability.getAvailableEnd();

            // Delete the original availability
            availabilityRepository.delete(availability);

            // Create availability before booking (if needed)
            if (availStart.isBefore(startDate)) {
                Availability beforeBooking = new Availability();
                beforeBooking.setCottage(cottage);
                beforeBooking.setAvailableStart(availStart);
                beforeBooking.setAvailableEnd(startDate.minusDays(1));
                availabilityRepository.save(beforeBooking);
            }

            // Create availability after booking (if needed)
            if (availEnd.isAfter(endDate)) {
                Availability afterBooking = new Availability();
                afterBooking.setCottage(cottage);
                afterBooking.setAvailableStart(endDate.plusDays(1));
                afterBooking.setAvailableEnd(availEnd);
                availabilityRepository.save(afterBooking);
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
        List<Booking> bookings = bookingRepository
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
        List<Availability> availabilities = availabilityRepository
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

    @Transactional
    private void mergeAvailabilities(Long cottageId) {
        List<Availability> availabilities = availabilityRepository.findByCottageId(cottageId);
        List<Availability> merged = Helper.mergeAvailabilities(availabilities);
        List<Availability> copiedMerged = new ArrayList<>();
        for(Availability av : merged) {
            Availability copyAv = new Availability(av.getAvailableStart(), av.getAvailableEnd(), av.getCottage());
            copiedMerged.add(copyAv);
        }
        availabilityRepository.deleteAll(availabilities);
        availabilityRepository.saveAll(copiedMerged);
    }

    @Transactional
    public Booking updateBooking(Long id, BookingRequest req) {
        Optional<Booking> booking = bookingRepository.findById(id);
        if(booking.isPresent()) {
            Availability availability = new Availability(booking.get().getStartDate(), booking.get().getEndDate(), booking.get().getCottage());
            availabilityRepository.save(availability);
            availabilityRepository.flush();
            req.cottageId = booking.get().getCottage().getId();
            bookingRepository.delete(booking.get());
            bookingRepository.flush();
        }

        Booking newBooking = createBooking(req);
        newBooking.setPricePerNight(req.getPricePerNight());
        newBooking.getCustomer().setName(req.getName());
        bookingRepository.save(newBooking);
        return newBooking;
    }

    @Transactional
    public void deleteBooking(Long id) {
        Optional<Booking> booking = bookingRepository.findById(id);
        if(booking.isPresent()) {
            Availability availability = new Availability(booking.get().getStartDate(), booking.get().getEndDate(), booking.get().getCottage());
            availabilityRepository.save(availability);
            availabilityRepository.flush();
            bookingRepository.delete(booking.get());
        }
    }
}

