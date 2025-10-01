package com.k48.lib48.controller;

import com.k48.lib48.dto.CategoryRequestDTO;
import com.k48.lib48.models.Category;
import com.k48.lib48.service.CategoryServices;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/categories")
public class CategoryController {
    private CategoryServices categoryServ;

    public CategoryController(CategoryServices categoryServ) {
        this.categoryServ = categoryServ;
    }
    
//    -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    
    @PreAuthorize("hasRole('GERANT')")
    @PostMapping(path = "/create", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Category> createCategory(@RequestBody CategoryRequestDTO categoryRequestDTO) {
        try {
            
            Category created = categoryServ.createCategory(categoryRequestDTO);
            
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        }catch (IllegalArgumentException e){
            System.out.println(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('GERANT')")
    @GetMapping(path = "/get/All", produces= APPLICATION_JSON_VALUE)
    public List<Category> getAllCategories() {
        return categoryServ.getAllCategories();
    }

    @PreAuthorize("hasRole('GERANT')")
    @GetMapping(path = "/get/byID/{id}", produces= APPLICATION_JSON_VALUE)
    public ResponseEntity<Category> getCategoryId(@PathVariable long id) {
        try {
            Category category = categoryServ.getCategoryById(id);
            return ResponseEntity.ok(category);
        }catch (NoSuchElementException e){
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasRole('GERANT')")
    @GetMapping(path = "/get/byName", produces= APPLICATION_JSON_VALUE)
    public ResponseEntity<Category> getCategoryByName(@RequestParam String name) {
        try {
            Category category = categoryServ.getCategoryByName(name);
            return ResponseEntity.ok(category);
        }catch (NoSuchElementException e){
            throw new NoSuchElementException(e.getMessage());
        }
    }

    @PreAuthorize("hasRole('GERANT')")
    @PutMapping(path = "/update/{id}" , consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Category> updateCategory(@PathVariable long id,@RequestBody CategoryRequestDTO  categoryRequestDTO) {
        try {
            Category updated = categoryServ.updateCategory(id , categoryRequestDTO);
            return ResponseEntity.ok(updated);
        }catch (NoSuchElementException e){
            throw new NoSuchElementException(e.getMessage());
        }
    }

    @PreAuthorize("hasRole('GERANT')")
    @DeleteMapping(path = "/delete/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable long id) {
        try {
            categoryServ.deleteCategory(id);
            return ResponseEntity.noContent().build();
        }catch (NoSuchElementException e){
            throw new NoSuchElementException(e.getMessage());
        }
     
    }

}
