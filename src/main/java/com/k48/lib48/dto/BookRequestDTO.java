package com.k48.lib48.dto;

import org.springframework.web.multipart.MultipartFile;

public record BookRequestDTO(String titre, String auteur, String editeur ) { }
