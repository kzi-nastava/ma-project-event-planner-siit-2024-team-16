package com.example.evenmate.models.user;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Notification {
    private Long id;
    private String text;
    private LocalDateTime dateTime;
    private boolean isRead;

    //constructor is like this for now until i connect to be
    public Notification(Long id,String text,LocalDateTime datetime,Boolean isRead){
        this.id=id;
        this.dateTime=datetime;
        this.text=text;
        this.isRead=isRead;
    }
    public String getDateTime() {
        LocalDateTime now = LocalDateTime.now();
        if (dateTime.toLocalDate().isEqual(now.toLocalDate())) {
            long minutesAgo = ChronoUnit.MINUTES.between(dateTime, now);
            if (minutesAgo < 1) {
                return "Just now";
            } else if (minutesAgo < 60) {
                return minutesAgo + " min ago";
            } else {
                long hoursAgo = ChronoUnit.HOURS.between(dateTime, now);
                return hoursAgo + " hour" + (hoursAgo > 1 ? "s" : "") + " ago";
            }        } else if (dateTime.toLocalDate().isEqual(now.minusDays(1).toLocalDate())) {
            DateTimeFormatter hour = DateTimeFormatter.ofPattern("HH:mm");
            return String.format("Yesterday at %s", dateTime.format(hour));
        } else {
            DateTimeFormatter date = DateTimeFormatter.ofPattern("dd. MMM yyyy.");
            DateTimeFormatter hour = DateTimeFormatter.ofPattern("HH:mm");
            return String.format("%s at %s", dateTime.format(date), dateTime.format(hour));
        }
    }
}
