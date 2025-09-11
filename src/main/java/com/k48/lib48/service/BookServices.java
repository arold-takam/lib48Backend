package com.k48.lib48.service;

import com.k48.lib48.dto.BookRequestDTO;
import com.k48.lib48.dto.BookUpDateDTO;
import com.k48.lib48.dto.HistoryRequestDTO;
import com.k48.lib48.models.Book;
import com.k48.lib48.models.Category;
import com.k48.lib48.models.User;
import com.k48.lib48.myEnum.EtatLivre;
import com.k48.lib48.myEnum.EtatOpperation;
import com.k48.lib48.myEnum.Role;
import com.k48.lib48.myEnum.TypeOpperation;
import com.k48.lib48.repository.BookRespositories;
import com.k48.lib48.repository.CategoryRepositories;
import com.k48.lib48.repository.UserRepositories;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class BookServices {
	
	private final BookRespositories bookRepository;
	private final CategoryRepositories categoryRepo;
	private final HistoryService historyService;
	private final UserRepositories userRepositories;
	
	public BookServices(BookRespositories bookRepository, CategoryRepositories categoryRepo, HistoryService historyService, UserRepositories userRepositories) {
		this.bookRepository = bookRepository;
		this.categoryRepo = categoryRepo;
		this.historyService = historyService;
		this.userRepositories = userRepositories;
	}
	
//------------------------------------------------------------------------------------------------------------------------------------------
	public List<Book> getAllBooks() {
		return bookRepository.findAll();
	}
	
	public Book getBookId(long id) {
		return bookRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Book not found"));
	}
	
	public Book getBooksByTitle(String title) {
		Book book = bookRepository.findByTitreIgnoreCase(title);
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
		return bookRepository.findAllByCategory(category);
	}
	
	@Transactional
	public Book createBook(long idCategory, BookRequestDTO bookRequestDTO, MultipartFile coverImage) {
	
		if (bookRepository.existsByTitre(bookRequestDTO.titre())){
			User gerant = userRepositories.findAllByRoleName(Role.GERANT).getFirst();
			HistoryRequestDTO historyRequestDTO = new HistoryRequestDTO(gerant.getName(), bookRequestDTO.titre(), TypeOpperation.AJOUTER_LIVRE, EtatOpperation.ECHEC, "Nouvel ajout rate.");
			historyService.addToHistory(historyRequestDTO);
			
			throw new IllegalArgumentException("Book with title: "+bookRequestDTO.titre()+" already exist.");
		}

// Validation du titre: il ne peut être ni null ni vide
		if (bookRequestDTO == null || bookRequestDTO.titre() == null || bookRequestDTO.titre().isBlank()) {
			User gerant = userRepositories.findAllByRoleName(Role.GERANT).getFirst();
			HistoryRequestDTO historyRequestDTO = new HistoryRequestDTO(gerant.getName(), bookRequestDTO.titre(), TypeOpperation.AJOUTER_LIVRE, EtatOpperation.ECHEC, "Nouvel ajout rate.");
			historyService.addToHistory(historyRequestDTO);
			
			
			throw new IllegalArgumentException("The Title cannot be null or empty");
		}

//Trouver la categorie
		Category category = categoryRepo.findById(idCategory).orElseThrow(() -> new IllegalArgumentException("Category not found"));
		
		Book book = new Book();
		
		book.setTitre(bookRequestDTO.titre());
		book.setAuteur(bookRequestDTO.auteur());
		book.setEditeur(bookRequestDTO.editeur());
		book.setEstDisponible(true);
		book.setEtatLivre(EtatLivre.NEUF);
		book.setCategory(category);
		
		if (coverImage != null && !coverImage.isEmpty()) {
			String coverImageURL = saveFile(coverImage);
			
			book.setUrlCoverImage(coverImageURL);
		}
		
		User gerant = userRepositories.findAllByRoleName(Role.GERANT).getFirst();
		HistoryRequestDTO historyRequestDTO = new HistoryRequestDTO(gerant.getName(), bookRequestDTO.titre(), TypeOpperation.AJOUTER_LIVRE, EtatOpperation.SUCCES, "Nouvel ajout valide.");
		historyService.addToHistory( historyRequestDTO);
		
		return bookRepository.save(book);
		
	}
	
	public Book updateBook(long id, EtatLivre livreEtat, long idCategory, BookUpDateDTO bookUpDateDTO, MultipartFile coverImage) {
		Book existingBook = bookRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Book not found"));
		
		Category category = categoryRepo.findById(idCategory).orElseThrow(() -> new NoSuchElementException("Category not found "));
		
		existingBook.setTitre(bookUpDateDTO.titre());
		existingBook.setAuteur(bookUpDateDTO.auteur());
		existingBook.setEditeur(bookUpDateDTO.editeur());
		existingBook.setEstDisponible(bookUpDateDTO.estDisponible());
		existingBook.setEtatLivre(livreEtat);
		existingBook.setCategory(category);
		
		if (coverImage != null && !coverImage.isEmpty()) {
			String coverImageURL = saveFile(coverImage);
			
			existingBook.setUrlCoverImage(coverImageURL);
		}
		
		User gerant = userRepositories.findAllByRoleName(Role.GERANT).getFirst();
		HistoryRequestDTO historyRequestDTO = new HistoryRequestDTO(gerant.getName(), bookUpDateDTO.titre(), TypeOpperation.MODIFIER_LIVRE, EtatOpperation.SUCCES, "Mise a jour du livre reussit.");
		historyService.addToHistory(historyRequestDTO);
		
		return bookRepository.save(existingBook);
	}
	
	public void deleteBook(long id) {
		if (!bookRepository.existsById(id)) {
			User gerant = userRepositories.findAllByRoleName(Role.GERANT).getFirst();
			HistoryRequestDTO historyRequestDTO = new HistoryRequestDTO(gerant.getName(), "Book to Delete", TypeOpperation.SUPPRIMMER_LIVRE, EtatOpperation.ECHEC, "Supression ratee.");
			historyService.addToHistory(historyRequestDTO);
			
			throw new NoSuchElementException("Book not found");
		}
		
		User gerant = userRepositories.findAllByRoleName(Role.GERANT).getFirst();
		HistoryRequestDTO historyRequestDTO = new HistoryRequestDTO(gerant.getName(), "Book to Delete", TypeOpperation.SUPPRIMMER_LIVRE, EtatOpperation.SUCCES, "Supression reussie.");
		historyService.addToHistory(historyRequestDTO);
		
		bookRepository.deleteById(id);
	}
	
	//-------------------UTILITIES METHODS-----------------------------------------------
    public String saveFile(MultipartFile file) {
		try {
			String originalFileName = file.getOriginalFilename();
			String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
			String fileName = UUID.randomUUID().toString() + fileExtension;
			
			Path uploadDir = Paths.get("uploads");
			if (!Files.exists(uploadDir)) {
				Files.createDirectories(uploadDir);
			}
			
			Path filePath = uploadDir.resolve(fileName);
			Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
			
			return "/uploads/" + fileName;
		} catch (IOException e) {
			User gerant = userRepositories.findAllByRoleName(Role.GERANT).getFirst();
			HistoryRequestDTO historyRequestDTO = new HistoryRequestDTO(gerant.getName(), "Any", TypeOpperation.AJOUTER_LIVRE, EtatOpperation.ECHEC, "Nouvel ajout rate.");
			historyService.addToHistory(historyRequestDTO);
			
			throw new RuntimeException("Could not save the file.", e);
		}
	}
}
