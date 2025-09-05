package com.k48.lib48.dto;

import org.springframework.web.multipart.MultipartFile;

public record BookUpDateDTO(String titre, String auteur, boolean estDisponible, String editeur) {
}
