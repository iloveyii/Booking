package com.booking.cottage.repository;

import com.booking.cottage.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
import com.booking.cottage.model.Availability;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT b FROM Booking b WHERE b.cottage.id = :cottageId AND b.startDate <= :endDate AND b.endDate >= :startDate")
    List<Booking> findOverlappingBookings(@Param("cottageId") Long cottageId,
                                          @Param("startDate") LocalDate startDate,
                                          @Param("endDate") LocalDate endDate);
    List<Booking> findByStartDateBetween(LocalDate startDate, LocalDate endDate);

    List<Booking> findByStartDateLessThanEqualAndEndDateGreaterThanEqual(LocalDate endOfMonth, LocalDate startOfMonth);
    List<Booking> findByStartDateGreaterThanEqualOrEndDateGreaterThanEqualOrderByStartDate(
            LocalDate startDate,
            LocalDate endDate
    );
}
