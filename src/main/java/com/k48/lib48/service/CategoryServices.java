package com.k48.lib48.service;

import com.k48.lib48.dto.CategoryRequestDTO;
import com.k48.lib48.models.Category;
import com.k48.lib48.repository.BookRespositories;
import com.k48.lib48.repository.CategoryRepositories;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class CategoryServices {
    private final CategoryRepositories categoryRepo;

    public CategoryServices(CategoryRepositories categoryRepo) {
        this.categoryRepo = categoryRepo;
    }

    public Category getCategoryByName(String categoryName) {
        Category category = categoryRepo.findByNomIgnoreCase(categoryName) ;
        if (category == null) {
            throw new NoSuchElementException("Category not found");
        }
        return category;
    }

    public List<Category> getAllCategories() {
        return categoryRepo.findAll();
    }

    public Category getCategoryById(long categoryId) {
        Optional<Category> categoryOptional = categoryRepo.findById(categoryId);
        if (categoryOptional.isEmpty()) {
            throw new NoSuchElementException("Category not found");
        }
            return categoryOptional.get();
    }

    public Category createCategory(CategoryRequestDTO categoryRequestDTO) {
        if (categoryRequestDTO == null) {
            throw new IllegalArgumentException("This category is invalid , try again");
        }
        
        Category existingCategory = new Category();
        
        existingCategory.setNom(categoryRequestDTO.nom());
        existingCategory.setDescription(categoryRequestDTO.description());
        
        return categoryRepo.save(existingCategory);
    }

    public Category updateCategory(long id,CategoryRequestDTO categoryRequestDTO) {
        
        Category updatedCategory = categoryRepo.findById(id).orElseThrow(() -> new NoSuchElementException("Category not found"));
        
        updatedCategory.setNom(categoryRequestDTO.nom());
        updatedCategory.setDescription(categoryRequestDTO.description());
        
        return categoryRepo.save(updatedCategory);
    }

    public void deleteCategory(long categoryId) {
        Category deletedCategory = categoryRepo.findById(categoryId).orElseThrow(() -> new NoSuchElementException("Category not found"));
        categoryRepo.delete(deletedCategory);
    }

}
