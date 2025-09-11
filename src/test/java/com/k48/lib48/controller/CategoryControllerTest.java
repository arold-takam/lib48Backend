package com.k48.lib48.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.k48.lib48.controllers.CategoryController;
import com.k48.lib48.dto.CategoryRequestDTO;
import com.k48.lib48.models.Category;
import com.k48.lib48.service.CategoryServices;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // On injecte le mock depuis TestConfig
    @Autowired
    private CategoryServices categoryServices;

    @TestConfiguration
    static class TestConfig {
        @Bean
        CategoryServices categoryServices() {
            return Mockito.mock(CategoryServices.class);
        }
    }

    @Test
    void getAllCategories_success() throws Exception {
        Mockito.when(categoryServices.getAllCategories()).thenReturn(List.of(new Category()));

        mockMvc.perform(get("/categories/get/All"))
                .andExpect(status().isOk());
    }

    @Test
    void getCategoryById_found() throws Exception {
        Category cat = new Category();
        cat.setNom("Test");
        Mockito.when(categoryServices.getCategoryById(1L)).thenReturn(cat);

        mockMvc.perform(get("/categories/get/ById/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Test"));
    }

    @Test
    void getCategoryById_notFound() throws Exception {
        Mockito.when(categoryServices.getCategoryById(99L)).thenThrow(new NoSuchElementException("Not found"));

        mockMvc.perform(get("/categories/get/ById/99"))
                .andExpect(status().isNotFound());
        // si tu ajoutes un @ExceptionHandler -> .andExpect(status().isNotFound())
    }

    @Test
    void createCategory_success() throws Exception {
        CategoryRequestDTO dto = new CategoryRequestDTO("Romans", "desc");
        Category saved = new Category();
        saved.setNom("Romans");

        Mockito.when(categoryServices.createCategory(any(CategoryRequestDTO.class))).thenReturn(saved);

        mockMvc.perform(post("/categories/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nom").value("Romans"));
    }

    @Test
    void updateCategory_success() throws Exception {
        CategoryRequestDTO dto = new CategoryRequestDTO("Updated", "desc");
        Category updated = new Category();
        updated.setNom("Updated");

        Mockito.when(categoryServices.updateCategory(eq(1L), any(CategoryRequestDTO.class))).thenReturn(updated);

        mockMvc.perform(put("/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Updated"));
    }

    @Test
    void deleteCategory_success() throws Exception {
        mockMvc.perform(delete("/categories/1"))
                .andExpect(status().isNoContent());
    }
}
