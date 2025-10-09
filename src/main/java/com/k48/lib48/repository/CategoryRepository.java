package com.k48.lib48.repository;

import com.k48.lib48.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CategoryRepository extends JpaRepository<Category, Long> {

    Category findByNomIgnoreCase(String name);

}
