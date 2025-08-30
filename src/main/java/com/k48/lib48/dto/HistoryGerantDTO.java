package com.k48.lib48.dto;

import com.k48.lib48.models.Book;
import com.k48.lib48.models.User;

import java.time.LocalDate;

public record HitoryGerantDTO(User gerant, Book book , String operation , String etatOperation , LocalDate date, User abonne) {
}
