package com.example.evenmate.models.commentreview;

import com.example.evenmate.models.user.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Review {
    private Long id;
    private int stars;
    private User user;
}

