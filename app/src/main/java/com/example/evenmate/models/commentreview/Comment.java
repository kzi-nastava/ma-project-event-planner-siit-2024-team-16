package com.example.evenmate.models.commentreview;

import androidx.annotation.Nullable;

import com.example.evenmate.models.asset.Asset;
import com.example.evenmate.models.event.Event;
import com.example.evenmate.models.user.User;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Comment {
    private Long id;
    private User user;
    private String text;
    private Date date;
    private boolean isApproved;

    @Nullable
    private Event event;

    @Nullable
    private Asset asset;
}

