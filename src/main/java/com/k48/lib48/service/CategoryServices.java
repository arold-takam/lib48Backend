package com.k48.lib48.service;

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
        Optional<Category> category = categoryRepo.findById(categoryId);
        if (category.isEmpty()) {
            throw new NoSuchElementException("Category not found");
        }else {
            return category.get();
        }
    }

    public Category createCategory(Category category) {
        if (category.getNom() == null || category.getNom().trim().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be null");
        }
        return categoryRepo.save(category);
    }

    public Category updateCategory(Category category) {
        Category updatedCategory = categoryRepo.findById(category.getId()).orElseThrow(() -> new NoSuchElementException("Category not found"));
        updatedCategory.setNom(category.getNom());
        updatedCategory.setDescription(category.getDescription());
        return categoryRepo.save(updatedCategory);
    }

    public void deleteCategory(long categoryId) {
        Category deletedCategory = categoryRepo.findById(categoryId).orElseThrow(() -> new NoSuchElementException("Category not found"));
        categoryRepo.delete(deletedCategory);
    }

}
