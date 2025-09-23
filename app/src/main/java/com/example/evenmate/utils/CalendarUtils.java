package com.example.evenmate.utils;

import android.util.Log;
import com.example.evenmate.models.user.CalendarDay;
import com.example.evenmate.models.user.CalendarItem;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class CalendarUtils {

    private static final String TAG = "CalendarUtils";

    public static List<CalendarDay> getDaysForMonth(LocalDateTime month) {
        List<CalendarDay> days = new ArrayList<>();

        // Get first day of the month
        LocalDateTime firstDayOfMonth = month.withDayOfMonth(1);

        // Get first Sunday of the calendar view (might be in previous month)
        LocalDateTime startDate = firstDayOfMonth.minusDays(firstDayOfMonth.getDayOfWeek().getValue() % 7);

        // Generate 42 days (6 weeks * 7 days) to fill the calendar grid
        for (int i = 0; i < 42; i++) {
            LocalDateTime currentDate = startDate.plusDays(i);
            boolean isCurrentMonth = currentDate.getMonth() == month.getMonth() &&
                    currentDate.getYear() == month.getYear();

            days.add(new CalendarDay(currentDate, isCurrentMonth));
        }

        return days;
    }

    public static List<CalendarDay> mapEventsToDays(List<CalendarDay> days, List<CalendarItem> events) {
        Log.d(TAG, "Mapping " + events.size() + " events to " + days.size() + " days");

        // Create a copy to avoid modifying the original list
        List<CalendarDay> daysWithEvents = new ArrayList<>();

        for (CalendarDay day : days) {
            CalendarDay newDay = new CalendarDay(day.getDateTime(), day.isCurrentMonth());
            newDay.setToday(day.isToday());

            // Find events for this day
            if (day.getDateTime() != null) {
                LocalDate dayDate = day.getDateTime().toLocalDate(); // Convert to LocalDate for comparison

                for (CalendarItem event : events) {
                    LocalDate eventDate = parseEventDate(event);

                    if (eventDate != null) {
                        Log.d(TAG, "Comparing day " + dayDate + " with event date " + eventDate +
                                " for event '" + event.getName() + "'");

                        if (eventDate.equals(dayDate)) {
                            newDay.addEvent(event);
                            Log.d(TAG, "✓ Added event '" + event.getName() + "' to day " + dayDate +
                                    " (Special: " + event.getIsSpecial() + ")");
                        }
                    } else {
                        Log.w(TAG, "Could not parse date for event: " + event.getName());
                    }
                }
            }

            daysWithEvents.add(newDay);
        }

        // Log summary
        int daysWithEventsCount = 0;
        for (CalendarDay day : daysWithEvents) {
            if (!day.getEvents().isEmpty()) {
                daysWithEventsCount++;
            }
        }
        Log.d(TAG, "Result: " + daysWithEventsCount + " days have events");

        return daysWithEvents;
    }

    /**
     * Parse event date from various possible formats
     */
    private static LocalDate parseEventDate(CalendarItem event) {
        if (event.getDateTime() == null) {
            Log.w(TAG, "Event " + event.getName() + " has null dateTime");
            return null;
        }

        try {
            // Get the date string from the event
            String dateTimeStr = event.getDateTime();
            Log.d(TAG, "Parsing date string: '" + dateTimeStr + "' for event: " + event.getName());

            // Your API returns format: "2025-09-28T00:00:00"
            // This is ISO LocalDateTime format
            try {
                LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr);
                LocalDate result = dateTime.toLocalDate();
                Log.d(TAG, "Successfully parsed date: " + result);
                return result;
            } catch (DateTimeParseException e1) {
                Log.w(TAG, "Failed to parse as LocalDateTime, trying LocalDate");

                // Try just the date part (2025-09-28)
                try {
                    if (dateTimeStr.contains("T")) {
                        String datePart = dateTimeStr.split("T")[0];
                        LocalDate result = LocalDate.parse(datePart);
                        Log.d(TAG, "Successfully parsed date part: " + result);
                        return result;
                    } else {
                        LocalDate result = LocalDate.parse(dateTimeStr);
                        Log.d(TAG, "Successfully parsed as LocalDate: " + result);
                        return result;
                    }
                } catch (DateTimeParseException e2) {
                    Log.e(TAG, "Could not parse date in any format: " + dateTimeStr);
                    return null;
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "Error parsing event date: " + e.getMessage(), e);
            return null;
        }
    }

    public static String formatMonthYear(LocalDateTime date) {
        return date.getMonth().name() + " " + date.getYear();
    }

    public static boolean isSameMonth(LocalDateTime date1, LocalDateTime date2) {
        return date1.getMonth() == date2.getMonth() && date1.getYear() == date2.getYear();
    }

    public static long getDaysBetween(LocalDateTime start, LocalDateTime end) {
        return ChronoUnit.DAYS.between(start, end);
    }
}