package com.k48.lib48.controller;

import com.k48.lib48.models.Category;
import com.k48.lib48.service.CategoryServices;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {
    private CategoryServices categoryServ;

    public CategoryController(CategoryServices categoryServ) {
        this.categoryServ = categoryServ;
    }

    @GetMapping("/get/All")
    public List<Category> getAllCategories() {
        return categoryServ.getAllCategories();
    }

    @GetMapping("/get/ById/{id}")
    public ResponseEntity<Category> getCategoryId(@PathVariable long id) {
        Category category = categoryServ.getCategoryById(id);
        return ResponseEntity.ok(category);
    }

    @GetMapping("/get/ByName")
    public ResponseEntity<Category> getCategoryByName(@RequestParam String name) {
        Category category = categoryServ.getCategoryByName(name);
        return ResponseEntity.ok(category);
    }

    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        Category created = categoryServ.createCategory(category);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Category> updateCategory(@RequestBody Category category) {
        Category updated = categoryServ.updateCategory(category);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable long id) {
       categoryServ.deleteCategory(id);
       return ResponseEntity.noContent().build();
    }

}
