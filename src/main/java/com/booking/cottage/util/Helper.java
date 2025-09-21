package com.booking.cottage.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import com.booking.cottage.model.Availability;

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

    public static List<Availability> mergeAvailabilities(List<Availability> availabilities) {
        // 1. Sort by cottage + start date
        availabilities.sort(Comparator
                .comparing((Availability a) -> a.getCottage().getId())
                .thenComparing(Availability::getAvailableStart));

        List<Availability> merged = new ArrayList<>();

        for (Availability current : availabilities) {
            if (merged.isEmpty()) {
                merged.add(current);
            } else {
                Availability last = merged.get(merged.size() - 1);

                // Same cottage and continuous or overlapping
                if (last.getCottage().getId().equals(current.getCottage().getId())
                        && !current.getAvailableStart().isAfter(last.getAvailableEnd().plusDays(1))) {
                    // Extend the last range
                    if (current.getAvailableEnd().isAfter(last.getAvailableEnd())) {
                        last.setAvailableEnd(current.getAvailableEnd());
                    }
                } else {
                    merged.add(current);
                }
            }
        }
        return merged;
    }

}

