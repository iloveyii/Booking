package com.booking.cottage.data;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import com.booking.cottage.repository.*;
import com.booking.cottage.model.*;

@Component
public class DataLoader implements CommandLineRunner {

    private final CottageRepository cottageRepository;
    private final AvailabilityRepository availabilityRepository;
    private final CustomerRepository customerRepository;
    private final BookingRepository bookingRepository;

    public DataLoader(CottageRepository cottageRepository,
                      AvailabilityRepository availabilityRepository,
                      CustomerRepository customerRepository,
                      BookingRepository bookingRepository) {
        this.cottageRepository = cottageRepository;
        this.availabilityRepository = availabilityRepository;
        this.customerRepository = customerRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public void run(String... args) {
        Cottage c1 = cottageRepository.save(new Cottage(null, "Lakeside Cottage", "Lake Tahoe", 150.0));
        Cottage c2 = cottageRepository.save(new Cottage(null, "Forest Cabin", "Yosemite", 120.0));
        Cottage c3 = cottageRepository.save(new Cottage(null, "Mountain Hut", "Rockies", 200.0));

        // availability ranges (inclusive)
        availabilityRepository.save(new Availability(null, LocalDate.of(2025,9,1), LocalDate.of(2025,9,30), c1));
        availabilityRepository.save(new Availability(null, LocalDate.of(2025,9,5), LocalDate.of(2025,9,25), c2));
        availabilityRepository.save(new Availability(null, LocalDate.of(2025,9,10), LocalDate.of(2025,9,20), c3));

        Customer customer1 = new Customer(null,"Alice", "alice@example.com", "123456789");
        Customer customer2 = new Customer(null, "Bob", "bob@example.com", "987654321");
        customerRepository.save(customer1);
        customerRepository.save(customer2);

        // bookingRepository.save(new Booking(c1, customer1, LocalDate.of(2025,9,1), LocalDate.of(2025,9,30) ));

        // Add one booking for Alice
        bookingRepository.save(
                Booking.builder()
                        .cottage(c1)
                        .customer(customer1)
                        .startDate(LocalDate.of(2025, 9, 5))
                        .endDate(LocalDate.of(2025, 9, 8))
                        .build()
        );
    }
}

