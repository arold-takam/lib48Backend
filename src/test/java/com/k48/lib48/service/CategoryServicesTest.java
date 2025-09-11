package com.k48.lib48.service;

import com.k48.lib48.dto.CategoryRequestDTO;
import com.k48.lib48.models.Category;
import com.k48.lib48.models.User;
import com.k48.lib48.myEnum.Role;
import com.k48.lib48.repository.CategoryRepositories;
import com.k48.lib48.repository.UserRepositories;
import com.k48.lib48.service.CategoryServices;
import com.k48.lib48.service.HistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryServicesTest {

    @Mock
    private CategoryRepositories categoryRepo;

    @Mock
    private UserRepositories userRepo;

    @Mock
    private HistoryService historyService;

    @InjectMocks
    private CategoryServices categoryServices;

    private User gerant;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        gerant = new User();
        gerant.setName("Admin");
        when(userRepo.findAllByRoleName(Role.GERANT)).thenReturn(List.of(gerant));
    }

    @Test
    void getCategoryByName_found() {
        Category cat = new Category();
        cat.setNom("Test");
        when(categoryRepo.findByNomIgnoreCase("Test")).thenReturn(cat);

        Category result = categoryServices.getCategoryByName("Test");
        assertEquals("Test", result.getNom());
    }

    @Test
    void getCategoryByName_notFound() {
        when(categoryRepo.findByNomIgnoreCase("X")).thenReturn(null);
        assertThrows(NoSuchElementException.class, () -> categoryServices.getCategoryByName("X"));
    }

    @Test
    void createCategory_success() {
        CategoryRequestDTO dto = new CategoryRequestDTO("Romans", "Livres de fiction");
        Category saved = new Category();
        saved.setNom("Romans");

        when(categoryRepo.save(any(Category.class))).thenReturn(saved);

        Category result = categoryServices.createCategory(dto);
        assertEquals("Romans", result.getNom());
        verify(historyService, times(1)).addToHistory(any());
    }

    @Test
    void createCategory_nullDto() {
        assertThrows(IllegalArgumentException.class, () -> categoryServices.createCategory(null));
        verify(historyService, times(1)).addToHistory(any());
    }

    @Test
    void updateCategory_success() {
        Category existing = new Category();
        existing.setNom("Old");
        when(categoryRepo.findById(1L)).thenReturn(Optional.of(existing));
        when(categoryRepo.save(any(Category.class))).thenReturn(existing);

        CategoryRequestDTO dto = new CategoryRequestDTO("New", "desc");
        Category result = categoryServices.updateCategory(1L, dto);

        assertEquals("New", result.getNom());
    }

    @Test
    void updateCategory_notFound() {
        when(categoryRepo.findById(99L)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> categoryServices.updateCategory(99L, new CategoryRequestDTO("X","Y")));
    }

    @Test
    void deleteCategory_success() {
        Category cat = new Category();
        when(categoryRepo.findById(1L)).thenReturn(Optional.of(cat));
        doNothing().when(categoryRepo).delete(cat);

        categoryServices.deleteCategory(1L);

        verify(categoryRepo, times(1)).delete(cat);
        verify(historyService, times(1)).addToHistory(any());
    }

    @Test
    void deleteCategory_notFound() {
        when(categoryRepo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> categoryServices.deleteCategory(1L));
    }
}

