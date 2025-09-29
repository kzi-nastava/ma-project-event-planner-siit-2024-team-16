package com.example.evenmate.models.chat;

import com.example.evenmate.models.user.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Chat {
    private Long id;
    private User user1;
    private User user2;
}
