package com.k48.lib48.service;

import com.k48.lib48.dto.HistoryRequestDTO;
import com.k48.lib48.dto.ReturnRequestDTO;
import com.k48.lib48.dto.ReturnResponseDTO;
import com.k48.lib48.models.*;
import com.k48.lib48.myEnum.*;
import com.k48.lib48.repository.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ReturnBookService {
	private final ReturnBookRepository returnBookRepository;
	private final BorrowBookRepository borrowBookRepository;
	private final UserRepository userRepository;
	private final CarteAbonnementRepository carteAbonnementRepository;
	private final HistoryService historyService;
	private BookRepository bookRepository;
	
	private final UserServices userServices;
	
	public ReturnBookService(ReturnBookRepository returnBookRepository, BorrowBookRepository borrowBookRepository, UserRepository userRepository, BookRepository bookRepository, CarteAbonnementRepository carteAbonnementRepository, UserServices userServices, HistoryService historyService) {
		this.returnBookRepository = returnBookRepository;
		this.borrowBookRepository = borrowBookRepository;
		this.userRepository = userRepository;
		this.bookRepository = bookRepository;
		this.carteAbonnementRepository = carteAbonnementRepository;
		this.userServices = userServices;
		this.historyService = historyService;
	}


//	RETURNING MANAGEMENT-----------------------------------------------------------------------------------------------------------------------------------------------
	public void makeReturn(int idGerant, EtatLivre nouvelEtatLivre, ReturnRequestDTO dto) {
		BorrowBook borrowBook = getBorrow(dto.borrowBookID());
		checkAlreadyReturned(borrowBook);
		
		User abonne = getAbonne(borrowBook.getAbonne().getId());
		CarteAbonnement carte = getCarte(abonne);
		
		Book book = borrowBook.getBook();
		
		User gerant = getGerant(idGerant);
		
//		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//		User aunthenticatedUser = (User) authentication.getPrincipal();
//		if (aunthenticatedUser.getId() != gerant.getId()) {
//			logHistory( abonne.getName(), "Book ID: " + book.getId(), TypeOpperation.RETOUR_LIVRE, EtatOpperation.ECHEC, "Livre non retourne." );
//
//			throw new IllegalArgumentException("You are not authorized to return this book.");
//		}
		
		EtatLivre ancienEtat = book.getEtatLivre();
		if (ancienEtat.equals(EtatLivre.MAUVAIS_ETAT) && !nouvelEtatLivre.equals(EtatLivre.MAUVAIS_ETAT)) {
			logHistory( abonne.getName(), "Book ID: " + book.getId(), TypeOpperation.RETOUR_LIVRE, EtatOpperation.ECHEC, "Livre non retourne." );
			
			throw new IllegalArgumentException("The book is already in bad condition and can't be returned in good condition.");
		}
		
		if (ancienEtat.equals(EtatLivre.BON_ETAT) && nouvelEtatLivre.equals(EtatLivre.NEUF)) {
			logHistory( abonne.getName(), "Book ID: " + book.getId(), TypeOpperation.RETOUR_LIVRE, EtatOpperation.ECHEC, "Livre non retourne." );
			
			throw new IllegalArgumentException("The book is already in good condition and can't be returned in best condition.");
		}
		
		int delaiEmprunt = borrowBook.getDelaiEmprunt();
		int delaiReel = (int) ChronoUnit.DAYS.between(borrowBook.getDateEmprunt(), dto.dateRetour());
		
		applyPenalty(carte, ancienEtat, nouvelEtatLivre, delaiReel > delaiEmprunt);
		updateBook(book, nouvelEtatLivre);
		
		ReturnBook retour = buildReturnBook(gerant, borrowBook, nouvelEtatLivre, dto.dateRetour());
		
		logHistory( abonne.getName(), "Book ID: " + book.getTitre(), TypeOpperation.RETOUR_LIVRE, EtatOpperation.SUCCES, "Livre retourne." );
		
		borrowBook.setStatus(BorrowStatus.TERMINE);
		borrowBookRepository.save(borrowBook);
		
		returnBookRepository.save(retour);
	}
	
	public ReturnResponseDTO getReturnByID(int gerantID, int returnID) {
		Optional<ReturnBook> optionalReturnBook = returnBookRepository.findById(returnID);
		
		if (optionalReturnBook.isEmpty()) {
			throw new IllegalArgumentException("Return not found with the ID: " + returnID);
		}
		
		ReturnBook returnBook = optionalReturnBook.get();
		
		User gerantReturning = userRepository.findById(gerantID).orElseThrow(() -> new IllegalArgumentException("Gerant not found with the ID: " + gerantID));
		
		return new ReturnResponseDTO(
			returnBook.getId(),
			gerantReturning.getName(),
			returnBook.getBorrowBookConcerned().getAbonne().getName(),
			returnBook.getBorrowBookConcerned().getBook().getTitre(),
			returnBook.getDateRetour(),
			returnBook.getNouvelEtatLivre()
		);
		
	}
	
	public List <ReturnResponseDTO> getReturnByAbonneID(int gerantID, int abonneID) {
		User abonne = userServices.getUserID(abonneID);
		User gerant = userServices.getUserID(gerantID);
		
		List<ReturnBook> returnBookList = returnBookRepository.findByBorrowBookConcerned_Abonne(abonne);
		
		List<ReturnResponseDTO> returnResponseDTOList = new ArrayList<>();

		for (ReturnBook returnBook : returnBookList) {
			returnResponseDTOList.add(new ReturnResponseDTO(
					returnBook.getId(),
					gerant.getName(),
					returnBook.getBorrowBookConcerned().getAbonne().getName(),
					returnBook.getBorrowBookConcerned().getBook().getTitre(),
					returnBook.getDateRetour(),
					returnBook.getNouvelEtatLivre()
			));
		}
		return returnResponseDTOList;

	}
	
	public List<ReturnResponseDTO> getAllReturns(int gerantID) {
		User gerant = userServices.getUserID(gerantID);
		
		if (!gerant.getRoleName().equals(Role.GERANT)){
			throw new IllegalArgumentException("This operation is only for Gerant.");
		}
		
		List<ReturnBook> returnBookList = returnBookRepository.findAll();
		
		List<ReturnResponseDTO> returnResponseDTOList = new ArrayList<>();
		
		for (ReturnBook returnBook : returnBookList) {
			
			returnResponseDTOList.add(new ReturnResponseDTO(
				returnBook.getId(),
				gerant.getName(),
				returnBook.getBorrowBookConcerned().getAbonne().getName(),
				returnBook.getBorrowBookConcerned().getBook().getTitre(),
				returnBook.getDateRetour(),
				returnBook.getNouvelEtatLivre()
			));
		}
		
		return returnResponseDTOList;
	}
	
	public List<ReturnResponseDTO> getAllReturnsByDate(int gerantID, LocalDate dateRetour) {
		User gerant = userServices.getUserID(gerantID);
		
		if (!gerant.getRoleName().equals(Role.GERANT)){
			throw new IllegalArgumentException("This operation is only for Gerant.");
		}
		
		List<ReturnBook> returnBookList = returnBookRepository.findAllByDateRetour(dateRetour);
		
		List<ReturnResponseDTO> returnResponseDTOList = new ArrayList<>();
		
		for (ReturnBook returnBook : returnBookList) {
			
			returnResponseDTOList.add(new ReturnResponseDTO(
				returnBook.getId(),
				gerant.getName(),
				returnBook.getBorrowBookConcerned().getAbonne().getName(),
				returnBook.getBorrowBookConcerned().getBook().getTitre(),
				returnBook.getDateRetour(),
				returnBook.getNouvelEtatLivre()
			));
		}
		
		return returnResponseDTOList;
	}


//METHODES UTILES--------------------------------------------------------------------------------------------------------------------------
	private User getGerant(int id) {
		return userRepository.findById(id)
			       .filter(u -> u.getRoleName().equals(Role.GERANT))
			       .orElseThrow(() -> new IllegalArgumentException("Gerant not found with ID: " + id));
	}
	
	private BorrowBook getBorrow(int id) {
		return borrowBookRepository.findById(id)
			       .orElseThrow(() -> new IllegalArgumentException("Borrow not found with ID: " + id));
	}
	
	private void checkAlreadyReturned(BorrowBook borrowBook) {
		if (returnBookRepository.findByBorrowBookConcerned(borrowBook).isPresent()) {
			throw new IllegalStateException("Ce livre a déjà été retourné.");
		}
	}
	
	private User getAbonne(int id) {
		return userRepository.findById(id)
			       .filter(u -> u.getRoleName().equals(Role.ABONNE))
			       .orElseThrow(() -> new IllegalArgumentException("Abonné not found with ID: " + id));
	}
	
	private CarteAbonnement getCarte(User abonne) {
		CarteAbonnement carte = abonne.getCarteAbonnement();
		if (carte == null) throw new IllegalArgumentException("Cet abonné n’a pas encore de carte.");
		return carte;
	}

//UTILITIES METHODS--------------------------------------------------------------------------------------------------------------------------
	private void logHistory(String userName, String bookRef, TypeOpperation type, EtatOpperation etat, String message) {
		historyService.addToHistory(new HistoryRequestDTO(userName, bookRef, type, etat, message));
	}
	
	private void applyPenalty(CarteAbonnement carte, EtatLivre ancienEtat, EtatLivre nouvelEtat, boolean enRetard) {
		if (enRetard) {
			if (nouvelEtat.equals(ancienEtat)) {
				carte.setAvailable(true);
				carte.setDuree(carte.getDuree() / 2);
				
				carteAbonnementRepository.save(carte);
				return;
			} else if (nouvelEtat.equals(EtatLivre.MAUVAIS_ETAT)) {
				carte.setAvailable(false);
				carte.setDuree(0);
				
				carteAbonnementRepository.save(carte);
				return;
			}
		}
		
		if (nouvelEtat.equals(EtatLivre.MAUVAIS_ETAT)) {
			if (!ancienEtat.equals(EtatLivre.MAUVAIS_ETAT)) {
				carte.setDuree(carte.getDuree() / 2);
				
				carteAbonnementRepository.save(carte);
				return;
			}
		}
	}
	
	private void updateBook(Book book, EtatLivre nouvelEtat) {
		book.setEtatLivre(nouvelEtat);
		book.setEstDisponible(true);
		bookRepository.save(book);
	}
	
	private ReturnBook buildReturnBook(User gerant, BorrowBook borrowBook, EtatLivre etat, LocalDate dateRetour) {
		ReturnBook retour = new ReturnBook();
		retour.setGerantReturningID(gerant.getId());
		retour.setBorrowBookConcerned(borrowBook);
		retour.setNouvelEtatLivre(etat);
		retour.setDateRetour(dateRetour);
		return retour;
	}
	
}

