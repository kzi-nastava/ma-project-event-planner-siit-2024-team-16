package com.example.evenmate.models.user;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Report {
    private Long id;
    private String reason;
    private User reportedUser;
    private Boolean isApproved = false;
}
