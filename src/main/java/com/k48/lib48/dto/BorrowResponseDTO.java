package com.k48.lib48.dto;

import com.k48.lib48.myEnum.BorrowStatus;

import java.time.LocalDate;

public record BorrowResponseDTO(int idBorrow, String gerantName, String abonneName, String BookTitle, LocalDate dateEmprunt, int delaiEmprunt, BorrowStatus status) {

}
