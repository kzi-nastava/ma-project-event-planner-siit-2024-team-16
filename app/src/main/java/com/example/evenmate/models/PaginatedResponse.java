package com.example.evenmate.models;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PaginatedResponse<T> {
    private List<T> content;
    private int totalPages;
    private int totalElements;

}
