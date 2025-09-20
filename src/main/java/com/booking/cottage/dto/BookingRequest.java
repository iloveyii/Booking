package com.booking.cottage.dto;

import java.time.LocalDate;
import java.time.LocalDate;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Email;
import lombok.Getter;


@Getter
public class BookingRequest {
    @NotNull
    public Long cottageId;

    public Long customerId; // optional field

    @NotNull
    public LocalDate startDate;

    @NotNull
    public LocalDate endDate;

    public short guests;

    public String name;

    public Double pricePerNight;

    @Email
    @NotNull
    public String email;

    public String specialRequest;

    public BookingRequest() {}
}
