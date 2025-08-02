package com.k48.lib48.dto;

import com.k48.lib48.models.Category;
import com.k48.lib48.myEnum.EtatLivre;

import java.time.LocalDate;

public record BookRequestDTO(String titre, String auteur, String editeur ) {
}
