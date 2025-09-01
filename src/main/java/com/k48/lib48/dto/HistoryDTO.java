package com.k48.lib48.dto;

import com.k48.lib48.models.Book;

import java.time.LocalDateTime;

public record HistoryDTO(String users,
                         String bookTitle ,
                         String operation ,
                         String etatOperation ,
                         LocalDateTime dateTime,
                         String details) {
}
