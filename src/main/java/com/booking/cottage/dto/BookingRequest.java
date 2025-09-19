package com.booking.cottage.dto;

import java.time.LocalDate;
import java.time.LocalDate;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Email;


public class BookingRequest {
    @NotNull
    public Long cottageId;

    public Long customerId; // optional field

    @NotNull
    public LocalDate startDate;

    @NotNull
    public LocalDate endDate;

    public short guests;

    @Email
    @NotNull
    public String email;

    public String specialRequest;

    public BookingRequest() {}
}
