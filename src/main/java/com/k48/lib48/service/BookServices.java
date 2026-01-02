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
import com.k48.lib48.repository.BookRepository;
import com.k48.lib48.repository.CategoryRepository;
import com.k48.lib48.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.*;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class BookServices {
	
	private final BookRepository bookRepository;
	private final CategoryRepository categoryRepo;
	private final HistoryService historyService;
	private final UserRepository userRepository;
	private  final FileServiceInt fileService;

	@Value("${base.url:http://localhost:8080}")
	private String baseUrl;

	@Value("${project.poster:uploads/}")
	private String path;
	
	public BookServices(BookRepository bookRepository, CategoryRepository categoryRepo, HistoryService historyService, UserRepository userRepository, FileServiceInt fileService) {
		this.bookRepository = bookRepository;
		this.categoryRepo = categoryRepo;
		this.historyService = historyService;
		this.userRepository = userRepository;
        this.fileService = fileService;
    }


//------------------------------------------------------------------------------------------------------------------------------------------
	@Transactional
	public Book createBook(long idCategory, BookRequestDTO bookRequestDTO, MultipartFile coverImage) throws IOException {
		
		if (bookRepository.existsByTitre(bookRequestDTO.titre())){
			User gerant = getGerant();
			logHistory(gerant.getName(), bookRequestDTO.titre(), TypeOpperation.AJOUTER_LIVRE, EtatOpperation.ECHEC, "Nouvel ajout rate.");
			
			throw new IllegalArgumentException("Book with title: "+bookRequestDTO.titre()+" already exist.");
		}
	
	// Validation du titre: il ne peut être ni null ni vide
		if (bookRequestDTO == null || bookRequestDTO.titre() == null || bookRequestDTO.titre().isBlank()) {
			User gerant = getGerant();
			logHistory(gerant.getName(), bookRequestDTO.titre(), TypeOpperation.AJOUTER_LIVRE, EtatOpperation.ECHEC, "Nouvel ajout rate.");
			
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

		// Gestion de l'image avec FileServiceInt
		if (coverImage != null && !coverImage.isEmpty()) {
			// Cloudinary renvoie déjà l'URL complète !
			String cloudinaryUrl = fileService.uploadFile(path, coverImage);
			book.setUrlCoverImage(cloudinaryUrl);
		}
		
		User gerant = getGerant();
		logHistory(gerant.getName(), bookRequestDTO.titre(), TypeOpperation.AJOUTER_LIVRE, EtatOpperation.SUCCES, "Nouvel ajout valide.");
		
		return bookRepository.save(book);
		
	}

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
	public Book updateBook(long id, EtatLivre livreEtat, long idCategory, BookUpDateDTO bookUpDateDTO, MultipartFile coverImage) throws IOException {
		// 1. Validation de base
		if (bookUpDateDTO == null || bookUpDateDTO.titre() == null || bookUpDateDTO.titre().isBlank()) {
			throw new IllegalArgumentException("Le titre ne peut pas être vide");
		}
		
		Book existingBook = bookRepository.findById(id)
			                    .orElseThrow(() -> new NoSuchElementException("Livre non trouvé"));
		
		Category category = categoryRepo.findById(idCategory)
			                    .orElseThrow(() -> new NoSuchElementException("Catégorie non trouvée"));
		
		// 2. Mise à jour des champs
		existingBook.setTitre(bookUpDateDTO.titre());
		existingBook.setAuteur(bookUpDateDTO.auteur());
		existingBook.setEditeur(bookUpDateDTO.editeur());
		existingBook.setEstDisponible(bookUpDateDTO.estDisponible());
		existingBook.setEtatLivre(livreEtat);
		existingBook.setCategory(category);
		
		// 3. Image
		if (coverImage != null && !coverImage.isEmpty()) {
			// Utilise toujours ton service Cloudinary
			String cloudinaryUrl = fileService.uploadFile(path, coverImage);
			existingBook.setUrlCoverImage(cloudinaryUrl);
		}
		
		// 4. Log
		User gerant = getGerant();
		logHistory(gerant.getName(), existingBook.getTitre(), TypeOpperation.MODIFIER_LIVRE, EtatOpperation.SUCCES, "Mise à jour réussie");
		
		return bookRepository.save(existingBook);
	}
	
	public void deleteBook(long id) {
		if (!bookRepository.existsById(id)) {
			User gerant = getGerant();
			logHistory(gerant.getName(), "Book to Delete", TypeOpperation.SUPPRIMMER_LIVRE, EtatOpperation.ECHEC, "Supression ratee.");
			
			throw new NoSuchElementException("Book not found");
		}
		
		if (!bookRepository.findById(id).get().isEstDisponible()){
			User gerant = getGerant();
			logHistory(gerant.getName(), "Book to Delete", TypeOpperation.SUPPRIMMER_LIVRE, EtatOpperation.ECHEC, "Supression ratee.");
			
			throw new IllegalArgumentException("Book is not available.");
		}
		
		User gerant = getGerant();
		logHistory(gerant.getName(), "Book to Delete", TypeOpperation.SUPPRIMMER_LIVRE, EtatOpperation.SUCCES, "Supression reussie.");
		
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
			User gerant = userRepository.findAllByRoleName(Role.GERANT).getFirst();
			HistoryRequestDTO historyRequestDTO = new HistoryRequestDTO(gerant.getName(), "Any", TypeOpperation.AJOUTER_LIVRE, EtatOpperation.ECHEC, "Nouvel ajout rate.");
			historyService.addToHistory(historyRequestDTO);
			
			throw new RuntimeException("Could not save the file.", e);
		}
	}
	
	private void logHistory(String userName, String bookTitre, TypeOpperation type, EtatOpperation etat, String message) {
		HistoryRequestDTO dto = new HistoryRequestDTO(userName, bookTitre, type, etat, message);
		historyService.addToHistory(dto);
	}
	
	private User getGerant() {
		return userRepository.findAllByRoleName(Role.GERANT).stream()
			       .findFirst()
			       .orElseThrow(() -> new IllegalStateException("Aucun gérant disponible"));
	}


	// === MÉTHODES SPÉCIFIQUES POUR LES IMAGES ===
	public byte[] getBookCoverImageData(long bookId) throws IOException {
		Book book = bookRepository.findById(bookId)
			            .orElseThrow(() -> new NoSuchElementException("Livre non trouvé"));
		
		String imagePathOrUrl = book.getUrlCoverImage();
		if (imagePathOrUrl == null || imagePathOrUrl.isEmpty()) {
			throw new IllegalArgumentException("Ce livre n'a pas d'image");
		}
		
		// CAS 1 : C'est une URL Cloudinary (commence par http)
		if (imagePathOrUrl.startsWith("http")) {
			try (InputStream in = new java.net.URL(imagePathOrUrl).openStream()) {
				return in.readAllBytes();
			}
		}
		
		// CAS 2 : C'est un fichier local (nom de fichier simple)
		else {
			String filename = extractFilenameFromUrl(imagePathOrUrl);
			try (InputStream inputStream = fileService.getResourceFile(path, filename)) {
				return inputStream.readAllBytes();
			}
		}
	}
	
	public String getBookCoverImageUrl(long bookId) {
		Book book = bookRepository.getBooksById(bookId);
		
		if (book.getUrlCoverImage() == null || book.getUrlCoverImage().isBlank()) {
			throw new IllegalArgumentException("Ce livre n'a pas d'image de couverture");
		}
		
		// Nettoyer l'URL pour éviter les espaces ou caractères parasites
		String cleanUrl = book.getUrlCoverImage().replaceAll("\\s+", "");
		
		return cleanUrl;
	}

	public boolean hasCoverImage(long bookId) {
		Book book = bookRepository.getBooksById(bookId);
		return book.getUrlCoverImage() != null && !book.getUrlCoverImage().isEmpty();
	}

	public String getCoverImageMimeType(long bookId) {
		Book book = bookRepository.getBooksById(bookId);
		if (book.getUrlCoverImage() == null) {
			return "application/octet-stream";
		}

		String filename = extractFilenameFromUrl(book.getUrlCoverImage());
		return determineMimeType(filename);
	}
	
	
	private String buildPublicUrl(String fileName) {
		// Utilise la baseUrl injectée (http://localhost:8080) + le context-path + le handler
		return baseUrl + "/api/uploads/" + fileName;
	}
	
	private String extractFilenameFromUrl(String url) {
		if (url == null || url.isEmpty()) return null;
		String[] parts = url.split("/");
		return parts[parts.length - 1];
	}

	private String determineMimeType(String filename) {
		if (filename == null) return "application/octet-stream";

		String lowerFilename = filename.toLowerCase();
		if (lowerFilename.endsWith(".jpg") || lowerFilename.endsWith(".jpeg")) {
			return "image/jpeg";
		} else if (lowerFilename.endsWith(".png")) {
			return "image/png";
		} else if (lowerFilename.endsWith(".gif")) {
			return "image/gif";
		} else if (lowerFilename.endsWith(".webp")) {
			return "image/webp";
		} else {
			return "application/octet-stream";
		}
	}

}
