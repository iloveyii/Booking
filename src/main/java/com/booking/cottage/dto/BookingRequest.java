package com.booking.cottage.dto;

import java.time.LocalDate;

public class BookingRequest {
    public Long cottageId;
    public Long customerId;
    public LocalDate startDate;
    public LocalDate endDate;

    public BookingRequest() {}
}
