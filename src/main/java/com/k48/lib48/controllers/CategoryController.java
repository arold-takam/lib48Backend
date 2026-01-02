package com.k48.lib48.controllers;

import com.k48.lib48.dto.CategoryRequestDTO;
import com.k48.lib48.models.Category;
import com.k48.lib48.service.CategoryServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger log = LoggerFactory.getLogger(CategoryController.class);
    
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
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }catch (Exception e){
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(path = "/get/All", produces= APPLICATION_JSON_VALUE)
    public List<Category> getAllCategories() {
        return categoryServ.getAllCategories();
    }

    @GetMapping(path = "/get/byID/{id}", produces= APPLICATION_JSON_VALUE)
    public ResponseEntity<Category> getCategoryId(@PathVariable long id) {
        try {
            Category category = categoryServ.getCategoryById(id);
            return ResponseEntity.ok(category);
        }catch (NoSuchElementException e){
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }catch (Exception e){
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "/get/byName", produces= APPLICATION_JSON_VALUE)
    public ResponseEntity<Category> getCategoryByName(@RequestParam String name) {
        try {
            Category category = categoryServ.getCategoryByName(name);
            return ResponseEntity.ok(category);
        }catch (NoSuchElementException e){
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }catch (Exception e){
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('GERANT')")
    @PutMapping(path = "/update/{id}" , consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Category> updateCategory(@PathVariable long id,@RequestBody CategoryRequestDTO  categoryRequestDTO) {
        try {
            Category updated = categoryServ.updateCategory(id , categoryRequestDTO);
            return ResponseEntity.ok(updated);
        }catch (NoSuchElementException e){
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }catch (Exception e){
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('GERANT')")
    @DeleteMapping(path = "/delete/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable long id) {
        try {
            categoryServ.deleteCategory(id);
            return ResponseEntity.noContent().build();
        }catch (NoSuchElementException e){
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }catch (Exception e){
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
     
    }

}
