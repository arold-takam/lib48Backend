package com.k48.lib48.controller;

import com.k48.lib48.dto.BookRequestDTO;
import com.k48.lib48.dto.BookUpDateDTO;
import com.k48.lib48.models.Book;
import com.k48.lib48.myEnum.EtatLivre;
import com.k48.lib48.service.BookServices;
import com.k48.lib48.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "/books")
@CrossOrigin(origins = "*")
public class BookController {
    private BookServices bookServices;
    private FileStorageService fileStorageService;

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
    @PostMapping(path = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Créer un nouveau livre avec image")
    public ResponseEntity<?> createBook(
            @RequestParam long idCategory,
            @ModelAttribute BookRequestDTO bookRequestDTO, // ← Garder @ModelAttribute
            @RequestParam(value = "coverImage", required = false) MultipartFile coverImage) { // ← Changer @RequestPart en @RequestParam

        try {
            Book createdBook = bookServices.createBook(idCategory, bookRequestDTO, coverImage);
            return new ResponseEntity<>(createdBook, HttpStatus.CREATED);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    //PUT BOOK-------------------------------------
    @PutMapping(path = ("/update/{id}"), consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Book> updateBook(@PathVariable long id , @RequestParam EtatLivre livreEtat, @RequestParam long idCategory, @ModelAttribute BookUpDateDTO bookUpDateDTO,@RequestPart(value = "coverImage",required = false)MultipartFile coverImage) {
      try {
          Book book1 = bookServices.updateBook(id,livreEtat,idCategory,bookUpDateDTO,coverImage);
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

    @GetMapping("/getcover/{id}")
    public ResponseEntity<Resource> getCoverImage(@PathVariable Long id) {
        Book book = bookServices.getBookId(id);
       if (book != null && book.getCoverImage() != null) {
           try {
               Path filePath = fileStorageService.loadFile(book.getCoverImage());
               Resource resource = new UrlResource(filePath.toUri());

               if(resource.exists() || resource.isReadable()) {
                   return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG)
                           .header(HttpHeaders.CONTENT_DISPOSITION,"inline;filename=\""+ resource.getFilename() + "\"")
                           .body(resource);
               }else {
                   return ResponseEntity.notFound().build();
               }

           } catch (Exception e) {
               throw new RuntimeException(e);
           }
       }else {
           return ResponseEntity.notFound().build();
       }
    }

}
