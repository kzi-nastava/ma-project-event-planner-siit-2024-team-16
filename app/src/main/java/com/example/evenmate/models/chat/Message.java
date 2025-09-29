package com.example.evenmate.models.chat;

import com.example.evenmate.models.user.User;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Message {
    private Long id;
    private String text;
    private LocalDateTime timestamp;
    private User sender;
}
