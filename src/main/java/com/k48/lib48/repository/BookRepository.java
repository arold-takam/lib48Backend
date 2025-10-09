package com.k48.lib48.repository;

import com.k48.lib48.models.Book;
import com.k48.lib48.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    Book findByTitreIgnoreCase(String title);
    
    List<Book> findAllByCategory (Category category);
    
    boolean existsByTitre(String titre);

}
