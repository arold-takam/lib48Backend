package com.k48.lib48.service;

import com.k48.lib48.dto.*;
import com.k48.lib48.models.Book;
import com.k48.lib48.models.BorrowBook;
import com.k48.lib48.models.CarteAbonnement;
import com.k48.lib48.models.User;
import com.k48.lib48.myEnum.EtatOpperation;
import com.k48.lib48.myEnum.Role;
import com.k48.lib48.myEnum.TypeOpperation;
import com.k48.lib48.repository.BookRepository;
import com.k48.lib48.repository.BorrowBookRepository;
import com.k48.lib48.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BorrowBookService {
	private final UserRepository userRepository;
	private final BorrowBookRepository borrowBookRepository;
	private final BookRepository bookRepository;
	private final BookServices bookServices;
	private final HistoryService historyService;
	
	public BorrowBookService(UserRepository userRepository, BorrowBookRepository borrowBookRepository, BookRepository bookRepository, BookServices bookServices, HistoryService historyService) {
		this.userRepository = userRepository;
		this.borrowBookRepository = borrowBookRepository;
		this.bookRepository = bookRepository;
		this.bookServices = bookServices;
		this.historyService = historyService;
	}
	
	
//	BORROWING MANAGEMENT-----------------------------------------------------------------------------------------------------------------
	public void makeBorrow(int gerantID, BorrowRequestDTO borrowRequestDTO)   {
		Optional<User>abonneOptional = userRepository.findById(borrowRequestDTO.abonneID());
		
		if (abonneOptional.isEmpty()) {
			throw new IllegalArgumentException("Abonne not found with the ID: "+borrowRequestDTO.abonneID());
		}
		User abonne = abonneOptional.get();
		
		if (!abonne.getRoleName().equals(Role.ABONNE)){
			HistoryRequestDTO historyRequestDTO = new HistoryRequestDTO(
				abonne.getName(),
				"Book ID: "+borrowRequestDTO.bookID(),
				TypeOpperation.EMPRUNT_LIVRE,
				EtatOpperation.ECHEC,
				"Emprunt rate."
			);
			historyService.addToHistory(historyRequestDTO);
			
			throw new IllegalArgumentException("User with ID: " + abonne.getId() + " is not an Abonne.");
		}
		
		Optional<User>optionalGerant = userRepository.findById(gerantID);
		if (optionalGerant.isEmpty() || !optionalGerant.get().getRoleName().equals(Role.GERANT)){
			HistoryRequestDTO historyRequestDTO = new HistoryRequestDTO(
				abonne.getName(),
				"Book ID: "+borrowRequestDTO.bookID(),
				TypeOpperation.EMPRUNT_LIVRE,
				EtatOpperation.ECHEC,
				"Emprunt rate."
			);
			historyService.addToHistory(historyRequestDTO);
			
			throw new IllegalArgumentException("Gerant not found with the ID: "+gerantID);
		}
		
		User gerant = optionalGerant.get();
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User authenticatedUser = (User) authentication.getPrincipal();
		if (authenticatedUser.getId() != abonne.getId()) {
			HistoryRequestDTO historyRequestDTO = new HistoryRequestDTO(
				abonne.getName(),
				"Book ID: "+borrowRequestDTO.bookID(),
				TypeOpperation.EMPRUNT_LIVRE,
				EtatOpperation.ECHEC,
				"Emprunt rate."
			);
			historyService.addToHistory(historyRequestDTO);
			
			throw new IllegalArgumentException("You are not authorized to borrow this book.");
		}
		
		Optional<Book> livreAEmprunterOpt = bookRepository.findById(borrowRequestDTO.bookID());
		if (livreAEmprunterOpt.isEmpty()) {
			HistoryRequestDTO historyRequestDTO = new HistoryRequestDTO(
				abonne.getName(),
				"Book ID: "+borrowRequestDTO.bookID(),
				TypeOpperation.EMPRUNT_LIVRE,
				EtatOpperation.ECHEC,
				"Emprunt rate."
			);
			historyService.addToHistory(historyRequestDTO);
			
			throw new IllegalArgumentException("Book not found with the ID: " + borrowRequestDTO.bookID());
		}
		
		Book livreAEmprunter = livreAEmprunterOpt.get();
		if (!livreAEmprunter.isEstDisponible()) {
			HistoryRequestDTO historyRequestDTO = new HistoryRequestDTO(
				abonne.getName(),
				"Book ID: "+borrowRequestDTO.bookID(),
				TypeOpperation.EMPRUNT_LIVRE,
				EtatOpperation.ECHEC,
				"Emprunt rate."
			);
			historyService.addToHistory(historyRequestDTO);
			
			throw new IllegalArgumentException("This book is not available.");
		}
		
		CarteAbonnement carteAbonnement = abonne.getCarteAbonnement();

		if (carteAbonnement == null){
			HistoryRequestDTO historyRequestDTO = new HistoryRequestDTO(
				abonne.getName(),
				"Book ID: "+borrowRequestDTO.bookID(),
				TypeOpperation.EMPRUNT_LIVRE,
				EtatOpperation.ECHEC,
				"Emprunt rate."
			);
			historyService.addToHistory(historyRequestDTO);
			
			throw new IllegalArgumentException("This abonne has no card yet.");
		}
		
		if (!carteAbonnement.isAvailable()){
			HistoryRequestDTO historyRequestDTO = new HistoryRequestDTO(
				abonne.getName(),
				"Book ID: "+borrowRequestDTO.bookID(),
				TypeOpperation.EMPRUNT_LIVRE,
				EtatOpperation.ECHEC,
				"Emprunt rate."
			);
			historyService.addToHistory(historyRequestDTO);
			
			throw new IllegalArgumentException("This card is not available.");
		}
		
		if (carteAbonnement.getDuree() <= 1) {
			HistoryRequestDTO historyRequestDTO = new HistoryRequestDTO(
				abonne.getName(),
				"Book ID: "+borrowRequestDTO.bookID(),
				TypeOpperation.EMPRUNT_LIVRE,
				EtatOpperation.ECHEC,
				"Emprunt rate."
			);
			historyService.addToHistory(historyRequestDTO);
			
			throw new IllegalArgumentException("Your card availability is over.");
		}
		
		Book bookToBorrow = bookServices.getBookId(borrowRequestDTO.bookID());
		
		if (!bookToBorrow.isEstDisponible()){
			HistoryRequestDTO historyRequestDTO = new HistoryRequestDTO(
				abonne.getName(),
				"Book ID: "+borrowRequestDTO.bookID(),
				TypeOpperation.EMPRUNT_LIVRE,
				EtatOpperation.ECHEC,
				"Emprunt rate."
			);
			historyService.addToHistory(historyRequestDTO);
			
			throw  new IllegalArgumentException("This book is not available.");
		}
		
		bookToBorrow.setEstDisponible(false);
		bookRepository.save(bookToBorrow);
		
		BorrowBook borrowBook = new BorrowBook();
		
		borrowBook.setGerant(gerant);
		borrowBook.setAbonne(abonne);
		borrowBook.setBook(bookToBorrow);
		borrowBook.setDateEmprunt(LocalDate.now());
		borrowBook.setDelaiEmprunt(borrowRequestDTO.delaiEmprunt());
		
		HistoryRequestDTO historyRequestDTO = new HistoryRequestDTO(
			abonne.getName(),
			"Book ID: "+borrowRequestDTO.bookID(),
			TypeOpperation.EMPRUNT_LIVRE, EtatOpperation.SUCCES,
			"Emprunt reussit."
		);
		historyService.addToHistory(historyRequestDTO);

		borrowBookRepository.save(borrowBook);
		
	}
	
	public BorrowResponseDTO getBorrowByID(int gerantID,int borrowID, int abonneID){

		//remplacer l'idée du gérant par son mot de passe
		User gerant = userRepository.findById(gerantID).orElseThrow(
				() -> new IllegalArgumentException("There is no Gerant with ID: "+gerantID)
		);
		Optional<BorrowBook>optionalBorrowBook = borrowBookRepository.findByIdAndAbonne_Id(borrowID, abonneID);
		
		if (optionalBorrowBook.isEmpty()){
			throw new IllegalArgumentException("No borrow for this user yet");
		}
		
		BorrowBook borrowBook = optionalBorrowBook.get();
		
		if (borrowBook.getGerant().getId() != gerantID){
			throw new IllegalArgumentException("This operation is only for Gerant.");
		}
		
		return new BorrowResponseDTO(
			borrowBook.getId(),
			borrowBook.getGerant().getName(),
			borrowBook.getAbonne().getName(),
			borrowBook.getBook().getTitre(),
			borrowBook.getDateEmprunt(),
			borrowBook.getDelaiEmprunt()
		);
	}
	
	public List<BorrowResponseDTO>getAllBorrows(int gerantID){
		
		User gerant = userRepository.findById(gerantID).orElseThrow(() -> new IllegalArgumentException("Gerant not found with the ID: " + gerantID));
	
		if (gerant.getRoleName() != Role.GERANT){
			throw new IllegalArgumentException("This operation is only for Gerant.");
		}
		
		List<BorrowBook>borrowBookList = borrowBookRepository.findAllByGerant_Id(gerantID);
		
		List<BorrowResponseDTO>borrowResponseDTOList = new ArrayList<>();
		
		for (BorrowBook borrow : borrowBookList){
			borrowResponseDTOList.add(
				new BorrowResponseDTO(
					borrow.getId(),
					borrow.getGerant().getName(),
					borrow.getAbonne().getName(),
					borrow.getBook().getTitre(),
					borrow.getDateEmprunt(),
					borrow.getDelaiEmprunt()
				)
			);
		}
		
		return borrowResponseDTOList;
	
	}

	public List<BorrowResponseDTO>getAllBorrowsByAbonne_Id(int abonneID){
		User abonne = userRepository.findById(abonneID).orElseThrow(
				() -> new IllegalArgumentException("There is no User with ID: "+abonneID)
		);
		List<BorrowBook> borrowBookList = borrowBookRepository.findAllByAbonne_Id(abonne.getId());

		List<BorrowResponseDTO>borrowResponseDTOList2 = new ArrayList<>();

		for (BorrowBook borrow : borrowBookList){
			borrowResponseDTOList2.add(
					new BorrowResponseDTO(
							borrow.getId(),
							borrow.getGerant().getName(),
							borrow.getAbonne().getName(),
							borrow.getBook().getTitre(),
							borrow.getDateEmprunt(),
							borrow.getDelaiEmprunt()

					)
			);
		}
		return borrowResponseDTOList2;
	}
}
