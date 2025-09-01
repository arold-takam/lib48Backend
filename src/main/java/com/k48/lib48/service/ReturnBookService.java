package com.k48.lib48.service;

import com.k48.lib48.dto.HistoryDTO;
import com.k48.lib48.dto.ReturnRequestDTO;
import com.k48.lib48.dto.ReturnResponseDTO;
import com.k48.lib48.models.*;
import com.k48.lib48.myEnum.EtatLivre;
import com.k48.lib48.myEnum.Role;
import com.k48.lib48.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ReturnBookService {
	private final ReturnBookRepository returnBookRepository;
	private final BorrowBookRepository borrowBookRepository;
	private final UserRepositories userRepositories;
	private final CarteAbonnementRepository carteAbonnementRepository;
	private BookRespositories bookRespositories;
	private final HistoryService historyService;

	private final UserServices userServices;
	
	public ReturnBookService(ReturnBookRepository returnBookRepository, BorrowBookRepository borrowBookRepository, UserRepositories userRepositories, BookRespositories bookRespositories, CarteAbonnementRepository carteAbonnementRepository, UserServices userServices, HistoryService historyService) {
		this.returnBookRepository = returnBookRepository;
		this.borrowBookRepository = borrowBookRepository;
		this.userRepositories = userRepositories;
		this.bookRespositories = bookRespositories;
		this.carteAbonnementRepository = carteAbonnementRepository;
		this.userServices = userServices;
		this.historyService = historyService;
	}
	
	
//	RETURNING MANAGEMENT-----------------------------------------------------------------------------------------------------------------------------------------------
	
	public void makeReturn(int idGerant, EtatLivre nouvelEtatLivre, ReturnRequestDTO dto) {
		User gerant = getGerant(idGerant);
		BorrowBook borrowBook = getBorrow(dto.borrowBookID());
		checkAlreadyReturned(borrowBook);
		
		User abonne = getAbonne(borrowBook.getAbonne().getId());
		CarteAbonnement carte = getCarte(abonne);
		Book book = borrowBook.getBook();
		EtatLivre ancienEtat = book.getEtatLivre();

		HistoryDTO historyGerantTrue = new HistoryDTO("Gerant",book.getTitre(),"RETURN","SUCCESS", LocalDateTime.now(),"Retour réussi");
		HistoryDTO historyGerantFalse = new HistoryDTO("Gerant",book.getTitre(),"RETURN","FAILED", LocalDateTime.now(),"Echec du retour");
		HistoryDTO historyAbonneTrue = new HistoryDTO("Abonnée",book.getTitre(),"RETURN","SUCCESS",LocalDateTime.now(),"Retour réussi");
		HistoryDTO historyAbonneFalse = new HistoryDTO("Abonnée",book.getTitre(),"RETURN","FAILED",LocalDateTime.now(),"Echec du retour");

		
		if (ancienEtat.equals(EtatLivre.MAUVAIS_ETAT) && !nouvelEtatLivre.equals(EtatLivre.MAUVAIS_ETAT)) {
			historyService.createHistory(historyAbonneFalse);
			historyService.createHistory(historyGerantFalse);
			throw new IllegalArgumentException("The book is already in bad condition and can't be returned in good condition.");
		}
		
		if (ancienEtat.equals(EtatLivre.BON_ETAT) && nouvelEtatLivre.equals(EtatLivre.NEUF)) {
			historyService.createHistory(historyAbonneFalse);
			historyService.createHistory(historyGerantFalse);
			throw new IllegalArgumentException("The book is already in good condition and can't be returned in best condition.");
		}
		
		int delaiEmprunt = borrowBook.getDelaiEmprunt();
		int delaiReel = (int) ChronoUnit.DAYS.between(borrowBook.getDateEmprunt(), dto.dateRetour());
		
		applyPenalty(carte, ancienEtat,  nouvelEtatLivre, delaiReel > delaiEmprunt);
		updateBook(book, nouvelEtatLivre);
		
		ReturnBook retour = buildReturnBook(gerant, borrowBook, nouvelEtatLivre, dto.dateRetour());

		historyService.createHistory(historyAbonneTrue);
		historyService.createHistory(historyGerantTrue);

		returnBookRepository.save(retour);
	}
	
	
	public ReturnResponseDTO getReturnByID(int gerantID, int returnID){
		Optional<ReturnBook>optionalReturnBook = returnBookRepository.findById(returnID);
		
		if (optionalReturnBook.isEmpty()) {
			throw new IllegalArgumentException("Return not found with the ID: " + returnID);
		}
		
		ReturnBook returnBook = optionalReturnBook.get();
		
		User gerantReturning  = userRepositories.findById(gerantID).orElseThrow(() -> new IllegalArgumentException("Gerant not found with the ID: " + gerantID));
		
		return new ReturnResponseDTO(
			returnBook.getId(),
			gerantReturning.getName(),
			returnBook.getBorrowBookConcerned().getAbonne().getName(),
			returnBook.getBorrowBookConcerned().getBook().getTitre(),
			returnBook.getDateRetour(),
			returnBook.getNouvelEtatLivre()
		);
		
	}
	
	public ReturnResponseDTO getReturnByAbonneID(int gerantID, int abonneID){
		User abonne = userServices.getUserID(abonneID);
		User gerant = userServices.getUserID(gerantID);

		ReturnBook returnBook = returnBookRepository.findByBorrowBookConcerned_Abonne(abonne);
		
		return new ReturnResponseDTO(
			returnBook.getId(),
			gerant.getName(),
			returnBook.getBorrowBookConcerned().getAbonne().getName(),
			returnBook.getBorrowBookConcerned().getBook().getTitre(),
			returnBook.getDateRetour(),
			returnBook.getNouvelEtatLivre()
		);
		
	}
	
	public List<ReturnResponseDTO>getAllReturns(int gerantID){
		User gerant = userServices.getUserID(gerantID);
		
		List<ReturnBook>returnBookList = returnBookRepository.findAll();
		
		List<ReturnResponseDTO>returnResponseDTOList = new ArrayList<>();
		
		for (ReturnBook returnBook : returnBookList){
			
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
	
	public List<ReturnResponseDTO>getAllReturnsByDate(int gerantID, LocalDate dateRetour){
		User gerant = userServices.getUserID(gerantID);
		
		List<ReturnBook>returnBookList = returnBookRepository.findAllByDateRetour(dateRetour);
		
		List<ReturnResponseDTO>returnResponseDTOList = new ArrayList<>();
		
		for (ReturnBook returnBook : returnBookList){
			
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
		return userRepositories.findById(id)
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
		return userRepositories.findById(id)
			.filter(u -> u.getRoleName().equals(Role.ABONNE))
			.orElseThrow(() -> new IllegalArgumentException("Abonné not found with ID: " + id));
	}
	
	private CarteAbonnement getCarte(User abonne) {
		CarteAbonnement carte = abonne.getCarteAbonnement();
		if (carte == null) throw new IllegalArgumentException("Cet abonné n’a pas encore de carte.");
		return carte;
	}
	
//UTILITIES METHODS--------------------------------------------------------------------------------------------------------------------------
	
	private void applyPenalty(CarteAbonnement carte, EtatLivre ancienEtat, EtatLivre nouvelEtat, boolean enRetard) {
		if (enRetard){
			if (nouvelEtat.equals(ancienEtat)){
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
		
		if (nouvelEtat.equals(EtatLivre.MAUVAIS_ETAT)){
			if (!ancienEtat.equals(EtatLivre.MAUVAIS_ETAT)){
				carte.setDuree(carte.getDuree() / 2);
				
				carteAbonnementRepository.save(carte);
				return;
			}
		}
	}
	
	private void updateBook(Book book, EtatLivre nouvelEtat) {
		book.setEtatLivre(nouvelEtat);
		book.setEstDisponible(true);
		bookRespositories.save(book);
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

