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
		User abonne = validateAbonne(borrowRequestDTO.abonneID());
		
		User gerant = validateGerant(gerantID);
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User authenticatedUser = (User) authentication.getPrincipal();
		if (authenticatedUser.getId() != abonne.getId()) {
			logHistory( abonne.getName(), "Book ID: "+borrowRequestDTO.bookID(), TypeOpperation.EMPRUNT_LIVRE, EtatOpperation.ECHEC, "Emprunt rate." );
			
			throw new IllegalArgumentException("You are not authorized to borrow this book.");
		}
		
		Optional<Book> livreAEmprunterOpt = bookRepository.findById(borrowRequestDTO.bookID());
		if (livreAEmprunterOpt.isEmpty()) {
			logHistory( abonne.getName(), "Book ID: "+borrowRequestDTO.bookID(), TypeOpperation.EMPRUNT_LIVRE, EtatOpperation.ECHEC, "Emprunt rate." );
			
			throw new IllegalArgumentException("Book not found with the ID: " + borrowRequestDTO.bookID());
		}
		
		Book livreAEmprunter = livreAEmprunterOpt.get();
		if (!livreAEmprunter.isEstDisponible()) {
			logHistory(abonne.getName(), "Book ID: "+borrowRequestDTO.bookID(), TypeOpperation.EMPRUNT_LIVRE, EtatOpperation.ECHEC, "Emprunt rate." );
			
			throw new IllegalArgumentException("This book is not available.");
		}
		
		CarteAbonnement carteAbonnement = abonne.getCarteAbonnement();
		validateCarte(carteAbonnement, abonne, borrowRequestDTO.bookID());
		
		Book bookToBorrow = bookServices.getBookId(borrowRequestDTO.bookID());
		
		if (!bookToBorrow.isEstDisponible()){
			logHistory( abonne.getName(), "Book ID: "+borrowRequestDTO.bookID(), TypeOpperation.EMPRUNT_LIVRE, EtatOpperation.ECHEC, "Emprunt rate." );
			
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
		
		logHistory(abonne.getName(), "Book ID: "+borrowRequestDTO.bookID(), TypeOpperation.EMPRUNT_LIVRE, EtatOpperation.SUCCES, "Emprunt reussit." );

		borrowBookRepository.save(borrowBook);
		
	}
	
	public BorrowResponseDTO getBorrowByID(int gerantID,int borrowID, int abonneID){
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
		
		User gerant =validateGerant(gerantID);
		
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
		User abonne = validateAbonne(abonneID);
		
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
	
//	UTILITIES METHODS---------------------------------------------------------------------------------------------------------------------------------
	private void logHistory(String userName, String bookRef, TypeOpperation type, EtatOpperation etat, String message) {
		historyService.addToHistory(new HistoryRequestDTO(userName, bookRef, type, etat, message));
	}
	
	private User validateAbonne(int abonneID) {
		User abonne = userRepository.findById(abonneID)
			              .orElseThrow(() -> new IllegalArgumentException("Abonné introuvable"));
		if (abonne.getRoleName() != Role.ABONNE) {
			logHistory(abonne.getName(), "Abonne ID: " + abonneID, TypeOpperation.EMPRUNT_LIVRE, EtatOpperation.ECHEC, "Rôle invalide");
			throw new IllegalArgumentException("L'utilisateur n'est pas un abonné");
		}
		return abonne;
	}
	
	private User validateGerant(int gerantID) {
		User gerant = userRepository.findById(gerantID)
			              .orElseThrow(() -> new IllegalArgumentException("Gérant introuvable"));
		if (gerant.getRoleName() != Role.GERANT) {
			throw new IllegalArgumentException("L'utilisateur n'est pas un gérant");
		}
		return gerant;
	}
	
	private void validateCarte(CarteAbonnement carte, User abonne, long bookID) {
		if (carte == null || !carte.isAvailable() || carte.getDuree() <= 1) {
			logHistory(abonne.getName(), "Book ID: " + bookID, TypeOpperation.EMPRUNT_LIVRE, EtatOpperation.ECHEC, "Carte invalide");
			throw new IllegalArgumentException("Carte d'abonnement invalide ou expirée");
		}
	}
	
	
}
