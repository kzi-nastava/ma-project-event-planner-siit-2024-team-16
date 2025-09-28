package com.example.evenmate.models.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Block {
    private Long id;
    private User blocked;
    private User blocker;
}
