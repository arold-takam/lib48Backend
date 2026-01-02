package com.k48.lib48.controllers;

import com.k48.lib48.dto.BookRequestDTO;
import com.k48.lib48.dto.BookUpDateDTO;
import com.k48.lib48.models.Book;
import com.k48.lib48.myEnum.EtatLivre;
import com.k48.lib48.service.BookServices;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.NoSuchElementException;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "/books")
@CrossOrigin(origins = {"http://127.0.0.1:5500", "http://localhost:5500"})
public class BookController {
	private final BookServices bookServices;
	private static final Logger log = LoggerFactory.getLogger(BookController.class);
	
	public BookController(BookServices bookServices) {
		this.bookServices = bookServices;
	}
	
//	----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
//POST BOOK----------------------------------
	@PreAuthorize("hasRole('GERANT')")
	@PostMapping(path = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(summary = "Créer un nouveau livre avec image")
	public ResponseEntity<?> createBook(
		@RequestParam long idCategory,
		@ModelAttribute BookRequestDTO bookRequestDTO,
		@RequestParam MultipartFile coverImage) {
		
		try {
			Book createdBook = bookServices.createBook(idCategory, bookRequestDTO, coverImage);
			return new ResponseEntity<>(createdBook, HttpStatus.CREATED);
		} catch (NoSuchElementException e) {
			log.error(e.getMessage());
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			log.error(e.getMessage());
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	// GET BOOK ------------------------------------------
	@GetMapping(path = "/get/All", produces = APPLICATION_JSON_VALUE)
	public List<Book> getAllBooks() {
		return bookServices.getAllBooks();
	}
	
	@GetMapping(path = "/get/byID/{id}", produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<Book> getBookById(@PathVariable long id) {
		try {
			Book book = bookServices.getBookId(id);
			return ResponseEntity.ok(book);
		} catch (NoSuchElementException e) {
			log.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			log.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping(path = "/get/byTitle", produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<Book> getBookByTitle(@RequestParam String title) {
		try {
			Book book = bookServices.getBooksByTitle(title);
			return ResponseEntity.ok(book);
		} catch (NoSuchElementException e) {
			log.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			log.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping(path = "/get/byCategory", produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Book>> getBookByCategorie(@RequestParam String categorie) {
		try {
			List<Book> books = bookServices.getBooksByCategorieNom(categorie);
			return ResponseEntity.ok(books);
		} catch (NoSuchElementException e) {
			log.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			log.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	//PUT BOOK-------------------------------------
	@PreAuthorize("hasRole('GERANT')")
	@PutMapping(path = ("/update/{id}"), consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Book> updateBook(
		@PathVariable long id,
		@RequestParam EtatLivre livreEtat,
		@RequestParam long idCategory,
		@ModelAttribute BookUpDateDTO bookUpDateDTO,
		@RequestParam(required = false) MultipartFile coverImage) {
		try {
			Book book = bookServices.updateBook(id, livreEtat, idCategory, bookUpDateDTO, coverImage);
			return ResponseEntity.ok(book);
		} catch (NoSuchElementException e) {
			log.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			log.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	//DELETE BOOK--------------------------------
	@PreAuthorize("hasRole('GERANT')")
	@DeleteMapping(path = "delete/{id}")
	public ResponseEntity<Book> deleteBook(@PathVariable long id) {
		try {
			bookServices.deleteBook(id);
			return ResponseEntity.noContent().build();
		} catch (NoSuchElementException e) {
			log.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			log.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// === ENDPOINTS SPÉCIFIQUES POUR LES IMAGES ===

	@GetMapping("/{id}/cover-image")
	public ResponseEntity<?> getBookCoverImage(@PathVariable long id) {
		try {
			byte[] imageData = bookServices.getBookCoverImageData(id);
			String mimeType = bookServices.getCoverImageMimeType(id);

			return ResponseEntity.ok()
					.contentType(MediaType.parseMediaType(mimeType))
					.body(imageData);

		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Image non trouvée sur le serveur");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Erreur lors de la récupération de l'image");
		}
	}

	@GetMapping("/{id}/cover-image-url")
	public ResponseEntity<?> getBookCoverImageUrl(@PathVariable long id) {
		try {
			String imageUrl = bookServices.getBookCoverImageUrl(id);
			return ResponseEntity.ok(imageUrl);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body("Livre non trouvé ou sans image");
		}
	}

	@GetMapping("/{id}/has-cover-image")
	public ResponseEntity<Boolean> hasCoverImage(@PathVariable long id) {
		try {
			boolean hasImage = bookServices.hasCoverImage(id);
			return ResponseEntity.ok(hasImage);
		} catch (Exception e) {
			return ResponseEntity.ok(false);
		}
	}

	
}
