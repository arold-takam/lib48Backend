package com.k48.lib48.dto;

import com.k48.lib48.models.Book;
import com.k48.lib48.models.User;

import java.time.LocalDate;

public record BorrowRequestDTO(int abonneID, int bookID, int delaiEmprunt) {

}
