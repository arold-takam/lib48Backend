package com.k48.lib48.controller;

import com.k48.lib48.dto.BookRequestDTO;
import com.k48.lib48.dto.BookUpDateDTO;
import com.k48.lib48.models.Book;
import com.k48.lib48.myEnum.EtatLivre;
import com.k48.lib48.service.BookServices;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "/books")
public class BookController {
    private BookServices bookServices;

    public BookController(BookServices bookServices) {
        this.bookServices = bookServices;
    }

    // GET BOOK ------------------------------------------
    @GetMapping(path = "/get/All" , produces = APPLICATION_JSON_VALUE)
    public List<Book> getAllBooks() {
        return bookServices.getAllBooks();
    }

    @GetMapping(path = "/get/ById/{id}",produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Book> getBookById(@PathVariable long id) {
       try {
           Book book = bookServices.getBookId(id);
           return ResponseEntity.ok(book);
       }catch (NoSuchElementException e){
           throw  new NoSuchElementException(e.getMessage());
       }
    }

    @GetMapping(path = "/get/ByTitle", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Book> getBookByTitle(@RequestParam String title) {
       try {
           Book book = bookServices.getBooksByTitle(title);
           return ResponseEntity.ok(book);
       }catch (NoSuchElementException e){
           throw  new NoSuchElementException(e.getMessage());
       }
    }
    

    @GetMapping(path = "/get/ByCategory", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Book>> getBookByCategorie(@RequestParam String categorie) {
       try {
           List <Book> books = bookServices.getBooksByCategorieNom(categorie);
           return ResponseEntity.ok(books);
       }catch (NoSuchElementException e){
           throw new NoSuchElementException(e.getMessage());
       }
    }

    //POST BOOK----------------------------------
    @PostMapping(path = ("/create") , consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createBook(@RequestParam EtatLivre etatLivre , @RequestParam long idCategory ,@RequestBody BookRequestDTO bookRequestDTO) {
       try {
           bookServices.createBook(etatLivre, idCategory, bookRequestDTO);
           
           return new ResponseEntity<>(HttpStatus.CREATED);
       }catch (NoSuchElementException e){
           throw new NoSuchElementException(e.getMessage());
       } catch (Exception e) {
               return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
       }
    }

    //PUT BOOK-------------------------------------
    @PutMapping(path = ("/update/{id}"), consumes = APPLICATION_JSON_VALUE , produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Book> updateBook(@PathVariable long id , @RequestParam EtatLivre livreEtat, @RequestParam long idCategory, @RequestBody BookUpDateDTO bookUpDateDTO) {
      try {
          Book book1 = bookServices.updateBook(id,livreEtat,idCategory,bookUpDateDTO);
          return ResponseEntity.ok(book1);
      }catch (NoSuchElementException e){
          throw new NoSuchElementException(e.getMessage());
      }
    }

    //DELETE BOOK--------------------------------
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Book> deleteBook(@RequestParam long id) {
      try {
          bookServices.deleteBook(id);
          return ResponseEntity.noContent().build();
      }catch (NoSuchElementException e){
          throw new NoSuchElementException(e.getMessage());
      }
    }

}
