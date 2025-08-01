package com.k48.lib48.controller;

import com.k48.lib48.models.Book;
import com.k48.lib48.myEnum.EtatLivre;
import com.k48.lib48.service.BookServices;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {
    private BookServices bookServices;

    public BookController(BookServices bookServices) {
        this.bookServices = bookServices;
    }

    // GET BOOK ------------------------------------------
    @GetMapping("/get/All")
    public List<Book> getAllBooks() {
        return bookServices.getAllBooks();
    }

    @GetMapping("/get/ById/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable long id) {
        Book book = bookServices.getBookId(id);
        return ResponseEntity.ok(book);
    }

    @GetMapping("/get/ByTitle")
    public ResponseEntity<Book> getBookByTitle(@RequestParam String title) {
        Book book = bookServices.getBooksByTitle(title);
        return ResponseEntity.ok(book);
    }

    @GetMapping("/get/ByAuthor")
    public ResponseEntity<List<Book>> getBookByAuteur(@RequestParam("auteur") String auteur) {
        List <Book> books = bookServices.getBooksByAuthor(auteur);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/get/ByEditor")
    public ResponseEntity<List<Book>> getBookByEditeur(@RequestParam String editeur) {
        List <Book> books = bookServices.getBooksByEditeur(editeur);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/get/ByState")
    public ResponseEntity<List<Book>> getBookByEtat(@RequestParam EtatLivre etatLivre) {
        List<Book> books = bookServices.getBooksByEtatLivre(etatLivre);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/get/ByCategory")
    public ResponseEntity<List<Book>> getBookByCategorie(@RequestParam String categorie) {
        List <Book> books = bookServices.getBooksByCategorieNom(categorie);
        return ResponseEntity.ok(books);
    }

    //POST BOOK----------------------------------
    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody Book book) {
        Book book1 = bookServices.createBook(book);
        return new ResponseEntity<>(book1, HttpStatus.CREATED);
    }

    //PUT BOOK-------------------------------------
    @PutMapping
    public ResponseEntity<Book> updateBook(@RequestBody Book book) {
        Book book1 = bookServices.updateBook(book);
        return ResponseEntity.ok(book1);
    }

    //DELETE BOOK--------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<Book> deleteBook(@RequestParam long id) {
        bookServices.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

}
