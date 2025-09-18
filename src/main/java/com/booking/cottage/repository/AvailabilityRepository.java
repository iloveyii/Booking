package com.booking.cottage.repository;

import com.booking.cottage.model.Availability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface AvailabilityRepository extends JpaRepository<Availability, Long> {
    @Query("SELECT a FROM Availability a WHERE a.cottage.id = :cottageId AND a.availableStart <= :startDate AND a.availableEnd >= :endDate")
    List<Availability> findCoveringAvailability(@Param("cottageId") Long cottageId,
                                                @Param("startDate") LocalDate startDate,
                                                @Param("endDate") LocalDate endDate);
}
