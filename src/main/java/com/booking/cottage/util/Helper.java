package com.booking.cottage.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Helper {

    public static List<String> getAvailableDates(String startDate, String endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate _startDate = LocalDate.parse(startDate, formatter);
        LocalDate _endDate = LocalDate.parse(endDate, formatter);
        return getAvailableDates(_startDate, _endDate);
    }
    public static List<String> getAvailableDates(LocalDate startDate, LocalDate endDate) {
        List<String> availableDates = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Iterate through each day in the range
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            availableDates.add(currentDate.format(formatter));
            currentDate = currentDate.plusDays(1);
        }

        return availableDates;
    }
    // Alternative version with more customization
    public static List<String> getAvailableDates(LocalDate startDate, LocalDate endDate,
                                                 int chunkSize, int gapSize, boolean excludeWeekends) {
        List<String> availableDates = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate current = startDate;

        while (!current.isAfter(endDate)) {
            // Create a chunk of available dates
            for (int i = 0; i < chunkSize && !current.isAfter(endDate); i++) {
                if (!excludeWeekends || current.getDayOfWeek().getValue() < 6) {
                    availableDates.add(current.format(formatter));
                }
                current = current.plusDays(1);
            }

            // Skip gap days
            current = current.plusDays(gapSize);
        }

        return availableDates;
    }
}

