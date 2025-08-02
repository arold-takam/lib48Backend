package com.k48.lib48.service;

import com.k48.lib48.dto.BookRequestDTO;
import com.k48.lib48.dto.BookUpDateDTO;
import com.k48.lib48.models.Book;
import com.k48.lib48.models.Category;
import com.k48.lib48.myEnum.EtatLivre;
import com.k48.lib48.repository.BookRespositories;
import com.k48.lib48.repository.CategoryRepositories;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class BookServices {

    private final BookRespositories bookRespo;
    private final CategoryRepositories categoryRepo;

    public BookServices(BookRespositories bookRespo, CategoryRepositories categoryRepo) {
        this.bookRespo = bookRespo;
        this.categoryRepo = categoryRepo;
    }

    public List<Book> getAllBooks() {
        return bookRespo.findAll();
    }

    public Book getBookId(long id) {
        return bookRespo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Book not found"));
    }

    public Book getBooksByTitle(String title) {
        Book book = bookRespo.findByTitreIgnoreCase(title);
        if (book == null) {
            throw new NoSuchElementException("Book not found");
        }
        return book;
    }

    public List<Book> getBooksByCategorieNom(String nomCategorie) {
        Category category = categoryRepo.findByNomIgnoreCase(nomCategorie);
        if (category == null) {
            throw new NoSuchElementException("Category not found");
        }
        return bookRespo.findAllByCategory(category);
    }

    public void createBook(EtatLivre livreEtat, long idCategory, BookRequestDTO   bookRequestDTO) {
        
        // Validation du titre: il ne peut être ni null ni vide
        if (bookRequestDTO == null || bookRequestDTO.titre() == null || bookRequestDTO.titre().isBlank()) {
            throw new IllegalArgumentException("The Title cannot be null or empty");
        }
        
        //Trouver la categorie
        Optional<Category> categoryOpt = categoryRepo.findById(idCategory);
        if (categoryOpt.isEmpty()) {
            throw new NoSuchElementException("Category not found");
        }
        
        Category category = categoryOpt.get();
        
        Book book = new Book();
        
        book.setTitre(bookRequestDTO.titre());
        book.setAuteur(bookRequestDTO.auteur());
        book.setEstDisponible(true);
        book.setEditeur(bookRequestDTO.editeur());
        book.setEtatLivre(livreEtat);
        book.setCategory(category);
        
        categoryRepo.save(category);
        
        bookRespo.save(book);
    }

    public Book updateBook(long id, EtatLivre livreEtat , long idCategory ,BookUpDateDTO bookUpDateDTO) {
        Book existingBook = bookRespo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Book not found"));
        
        Optional<Category> category =  categoryRepo.findById(idCategory);
        if (category.isEmpty()){
            throw new NoSuchElementException("Category not found ");
        }

        existingBook.setTitre(bookUpDateDTO.titre());
        existingBook.setAuteur(bookUpDateDTO.auteur());
        existingBook.setEstDisponible(bookUpDateDTO.estDisponible());
        existingBook.setEditeur(bookUpDateDTO.editeur());
        existingBook.setEtatLivre(livreEtat);
        existingBook.setCategory(category.get());
     
        
        return bookRespo.save(existingBook);
    }

    public void deleteBook(long id) {
        if (!bookRespo.existsById(id)) {
            throw new NoSuchElementException("Book not found");
        }
        bookRespo.deleteById(id);
    }
}
