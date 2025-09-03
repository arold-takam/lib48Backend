package com.k48.lib48.service;

import com.k48.lib48.dto.BookRequestDTO;
import com.k48.lib48.dto.BookUpDateDTO;
import com.k48.lib48.dto.HistoryDTO;
import com.k48.lib48.models.Book;
import com.k48.lib48.models.Category;
import com.k48.lib48.myEnum.EtatLivre;
import com.k48.lib48.repository.BookRespositories;
import com.k48.lib48.repository.CategoryRepositories;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class BookServices {

    private final BookRespositories bookRespo;
    private final CategoryRepositories categoryRepo;
    private final FileStorageService fileStorageService;
    private final HistoryService historyService;

    public BookServices(BookRespositories bookRespo, CategoryRepositories categoryRepo ,FileStorageService fileStorageService, HistoryService historyService) {
        this.bookRespo = bookRespo;
        this.categoryRepo = categoryRepo;
        this.fileStorageService= fileStorageService;
        this.historyService = historyService;
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


    @Transactional

    public Book createBook(long idCategory, BookRequestDTO   bookRequestDTO, MultipartFile coverImage) {
        
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


        HistoryDTO historyGerantTrue = new HistoryDTO("Gerant",book.getTitre(),"CREATE","SUCCESS", LocalDateTime.now(),"Echec de la création du livre couverture non validé");
        HistoryDTO historyGerantFalse = new HistoryDTO("Gerant",book.getTitre(),"CREATE","FAILED", LocalDateTime.now(),"Création du livre réussi");

        //valider l'image de couverture
        if (coverImage != null && !coverImage.isEmpty()) {
            String fileName = fileStorageService.storeFile(coverImage, book.getId() );
            book.setCoverImage(fileName);
        }
        book.setTitre(bookRequestDTO.titre());
        book.setAuteur(bookRequestDTO.auteur());
        book.setEstDisponible(true);
        book.setEditeur(bookRequestDTO.editeur());
        book.setEtatLivre(EtatLivre.NEUF);
        book.setCategory(category);

        historyService.createHistory(historyGerantTrue);
        
        categoryRepo.save(category);
       return bookRespo.save(book);
    }

    public Book updateBook(long id, EtatLivre livreEtat , long idCategory ,BookUpDateDTO bookUpDateDTO, MultipartFile coverImage) {
        Book existingBook = bookRespo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Book not found"));

        HistoryDTO historyGerantTrue = new HistoryDTO("Gerant",existingBook.getTitre(),"UPDATE","SUCCESS", LocalDateTime.now(),"Echec de la mise à jour du livre ");
        HistoryDTO historyGerantFalse = new HistoryDTO("Gerant",existingBook.getTitre(),"UPDATE","FAILED", LocalDateTime.now(),"Mise à jour du livre réussi");
        
        Optional<Category> category =  categoryRepo.findById(idCategory);
        if (category.isEmpty()){
            historyService.createHistory(historyGerantFalse);
            throw new NoSuchElementException("Category not found ");
        }

        if (coverImage != null && !coverImage.isEmpty()) {
            fileStorageService.deleteFile(existingBook.getCoverImage());
        }
        String fileName = fileStorageService.storeFile(coverImage, existingBook.getId() );
        existingBook.setCoverImage(fileName);
        existingBook.setTitre(bookUpDateDTO.titre());
        existingBook.setAuteur(bookUpDateDTO.auteur());
        existingBook.setEstDisponible(bookUpDateDTO.estDisponible());
        existingBook.setEditeur(bookUpDateDTO.editeur());
        existingBook.setEtatLivre(livreEtat);
        existingBook.setCategory(category.get());

        historyService.createHistory(historyGerantTrue);
        
        return bookRespo.save(existingBook);
    }

    public void deleteBook(long id) {

        HistoryDTO historyGerantTrue = new HistoryDTO("Gerant",bookRespo.findById(id).get().getTitre(),"DELETE","SUCCESS", LocalDateTime.now(),"Echec de la supression du livre ");
        HistoryDTO historyGerantFalse = new HistoryDTO("Gerant",bookRespo.findById(id).get().getTitre(),"DELETE","FAILED", LocalDateTime.now(),"Supression du livre réussi");

        if (!bookRespo.existsById(id)) {
            historyService.createHistory(historyGerantFalse);
            throw new NoSuchElementException("Book not found");
        }

        historyService.createHistory(historyGerantTrue);
        bookRespo.deleteById(id);
    }
}
