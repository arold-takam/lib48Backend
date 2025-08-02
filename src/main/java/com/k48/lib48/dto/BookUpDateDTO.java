package com.k48.lib48.dto;

import com.k48.lib48.models.Category;
import com.k48.lib48.myEnum.EtatLivre;

import java.time.LocalDate;

public record BookUpDateDTO(String titre, String auteur, boolean estDisponible, String editeur) {
}
