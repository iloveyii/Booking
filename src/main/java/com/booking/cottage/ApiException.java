package com.booking.cottage;

public class ApiException extends RuntimeException{
    public ApiException(String message) {
        super(message);
    }
}
