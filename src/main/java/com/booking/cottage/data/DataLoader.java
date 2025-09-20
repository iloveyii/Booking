package com.booking.cottage.data;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.List;

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
        Cottage c1 = cottageRepository.save(
            Cottage.builder()
                .name("Lakeside Cottage")
                .location("Lake Tahoe")
                .pricePerNight(150.0)
                .bedrooms((short) 3)
                .baths((short) 1)
                .stars(5.0f)
                .reviews(230)
                .guests((short) 5)
                .images(List.of(
                        "cot1.avif",
                        "cot2.avif"
                ))
                .build()
        );

        Cottage c2 = cottageRepository.save(new Cottage("Forest Cabin", "Yosemite", 120.0, (short) 2, (short) 1, List.of("cot2.avif"), 4.5f, 150, (short) 2));
        Cottage c3 = cottageRepository.save(new Cottage("Mountain Hut", "Rockies", 200.0, (short) 3, (short) 2, List.of("cot3.avif"), 4.0f, 250, (short) 4));

        Cottage c4 = cottageRepository.save(new Cottage("Seaside Escape", "Vancouver Island, BC", 199.0, (short) 2, (short) 1, List.of("cot4.avif"), 4.5f, 130, (short) 3));
        Cottage c5 = cottageRepository.save(new Cottage("Wilderness Cabin", "Algonquin Park, Ontario", 169.0, (short) 1, (short) 1, List.of("cot5.avif"), 4.7f, 120, (short) 1));
        Cottage c6 = cottageRepository.save(new Cottage("Luxury Lake House", "Lake of Bays, Ontario", 349.0, (short) 4, (short) 3, List.of("cot6.avif"), 4.2f, 450, (short) 7));

        Cottage c7 = cottageRepository.save(new Cottage("Riverside Retreat", "Gatineau, Quebec", 159.0, (short) 2, (short) 1, List.of("cot7.avif"), 4.9f, 105, (short) 2));
        Cottage c8 = cottageRepository.save(new Cottage("Mountain A-Frame", "Canmore, Alberta", 229.0, (short) 3, (short) 2, List.of("cot8.avif", "cot7.avif"), 4.3f, 115, (short) 3));
        Cottage c9 = cottageRepository.save(new Cottage("Countryside Cottage", "Prince Edward County, Ontario", 139.0, (short) 2, (short) 1, List.of("cot9.avif"), 4.5f, 109, (short) 1));

        // availability ranges (inclusive)
        availabilityRepository.save(new Availability(null, LocalDate.of(2025,10,9), LocalDate.of(2025,10,30), c1));
        availabilityRepository.save(new Availability(null, LocalDate.of(2025,10,5), LocalDate.of(2025,10,25), c2));
        availabilityRepository.save(new Availability(null, LocalDate.of(2025,10,10), LocalDate.of(2025,10,30), c3));

        Customer customer1 = new Customer(null,"Alice", "alice@example.com", "123456789");
        Customer customer2 = new Customer(null, "Bob", "bob@example.com", "987654321");
        customerRepository.save(customer1);
        customerRepository.save(customer2);

        // Add one booking for Alice
//        bookingRepository.save(
//            Booking.builder()
//                .cottage(c1)
//                .customer(customer1)
//                .startDate(LocalDate.of(2025, 10, 5))
//                .endDate(LocalDate.of(2025, 10, 8))
//                .build()
//        );
    }
}

