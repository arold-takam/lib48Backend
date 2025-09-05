package com.k48.lib48.dto;

import com.k48.lib48.myEnum.EtatLivre;

import java.time.LocalDate;

public record ReturnResponseDTO(int idReturn, String gerantRetutnName, String abonneName, String livreName, LocalDate dateRetour, EtatLivre nouvelEtatLivre) {

}
