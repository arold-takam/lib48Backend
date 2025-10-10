package com.k48.lib48.service;

import com.k48.lib48.dto.HistoryRequestDTO;
import com.k48.lib48.models.CarteAbonnement;
import com.k48.lib48.models.User;
import com.k48.lib48.myEnum.EtatOpperation;
import com.k48.lib48.myEnum.Role;
import com.k48.lib48.myEnum.TypeAbonnement;
import com.k48.lib48.myEnum.TypeOpperation;
import com.k48.lib48.repository.CarteAbonnementRepository;
import com.k48.lib48.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

import static com.k48.lib48.myEnum.TypeAbonnement.*;

@Service
public class CarteAbonnementService {
	private final CarteAbonnementRepository carteAbonnementRepository;
	private final UserRepository userRepository;
	private final HistoryService historyService;
	
	public CarteAbonnementService(CarteAbonnementRepository carteAbonnementRepository, UserRepository userRepository, HistoryService historyService) {
		this.carteAbonnementRepository = carteAbonnementRepository;
		this.userRepository = userRepository;
		this.historyService = historyService;
	}

//	Abonne CARTEaBONNEMENT MANAGEMENT--------------------------------------------------------------------------------------------------------------
	
	public void createCarte(int abonneID, TypeAbonnement typeAbonnement) {
		User abonne = validateAbonne(abonneID);
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User authenticatedUser = (User) authentication.getPrincipal();
		if (!authenticatedUser.getMail().equalsIgnoreCase(abonne.getMail())) {
			logHistory( abonne.getName(), "Book ID:  N/A", TypeOpperation.ABONNEMENT, EtatOpperation.ECHEC, "Abonnement ratee." );
			
			throw new IllegalArgumentException("You are not authorized to update this user.");
		}
		
		if (abonne.getRoleName().equals(Role.GERANT)) {
			logHistory( abonne.getName(), "Book ID:  N/A", TypeOpperation.ABONNEMENT, EtatOpperation.ECHEC, "Abonnement ratee." );
			
			throw new IllegalArgumentException("This operation is only for abonne.");
		}
		
		if (abonne.getCarteAbonnement() != null) {
			logHistory( abonne.getName(), "Book ID:  N/A", TypeOpperation.ABONNEMENT, EtatOpperation.ECHEC, "Abonnement ratee." );
			
			throw new IllegalArgumentException(abonne.getName() + " has already a card; use it.");
		}
		
		CarteAbonnement carteAbonnement = new CarteAbonnement();
		
		long min = 100_000_000L;
		long max = 999_999_999L;
		Random random = new Random();
		long randomNumber = min + random.nextLong(max - min + 1);
		
		carteAbonnement.setCardNumber(randomNumber);
		
		carteAbonnement.setAvailable(true);
		
		carteAbonnement = setCardAvailability(carteAbonnement, typeAbonnement);
		
		carteAbonnementRepository.save(carteAbonnement);
		
		abonne.setCarteAbonnement(carteAbonnement);
		
		logHistory( abonne.getName(), "Book ID:  N/A", TypeOpperation.ABONNEMENT, EtatOpperation.SUCCES, "Abonnement reussi." );
		
		userRepository.save(abonne);
		
	}
	
	public CarteAbonnement getCardID(int abonneID) {
		User abonne = userRepository.findById(abonneID).orElseThrow(() -> new IllegalArgumentException("Abonne not found with the ID: " + abonneID));
		
		CarteAbonnement carteAbonnement = abonne.getCarteAbonnement();
		if (carteAbonnement == null) {
			throw new IllegalArgumentException("This abonne has no card yet.");
		}
		
		return carteAbonnement;
	}
	
	public List<CarteAbonnement> getAllCards(int gerantID) {
		User gerant = userRepository.findById(gerantID).orElseThrow(() -> new IllegalArgumentException("Gerant not found with the ID: " + gerantID));
		
		if (gerant.getRoleName().equals(Role.ABONNE)) {
			throw new IllegalArgumentException("This is only for gerant access");
		}
		
		return carteAbonnementRepository.findAll();
	}
	
	public void subscribe(int abonneID, TypeAbonnement typeAbonnement) {
		User abonne = userRepository.findById(abonneID).orElseThrow(() -> new IllegalArgumentException("Abonne not found with the ID: " + abonneID));
		
		if (!typeAbonnement.equals(STANDART) && !typeAbonnement.equals(CUSTUM) && !typeAbonnement.equals(VIP)){
			logHistory(abonne.getName(), "Book ID:  N/A", TypeOpperation.REABONNEMENT, EtatOpperation.ECHEC, "Reabonnement rate." );
			
			throw new IllegalArgumentException("This type of subscribe doesn't exist.");
		}
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User authenticatedUser = (User) authentication.getPrincipal();
		if (!authenticatedUser.getMail().equalsIgnoreCase(abonne.getMail())) {
			logHistory(abonne.getName(), "Book ID:  N/A", TypeOpperation.REABONNEMENT, EtatOpperation.ECHEC, "Reabonnement rate." );
			
			throw new IllegalArgumentException("You are not authorized to update this user.");
		}
		
		if (abonne.getRoleName().equals(Role.GERANT)) {
			logHistory(abonne.getName(), "Book ID:  N/A", TypeOpperation.REABONNEMENT, EtatOpperation.ECHEC, "Reabonnement rate." );
			
			throw new IllegalArgumentException("This operation is only for abonne.");
		}
		
		CarteAbonnement carteAbonnement = abonne.getCarteAbonnement();
		
		if (carteAbonnement == null) {
			logHistory(abonne.getName(), "Book ID:  N/A", TypeOpperation.REABONNEMENT, EtatOpperation.ECHEC, "Reabonnement rate." );
			
			throw new IllegalArgumentException("Abonne has no card yet.");
		}
		
		carteAbonnement.setAvailable(true);
		
		carteAbonnement = setCardAvailability(carteAbonnement, typeAbonnement);
		
		abonne.setCarteAbonnement(carteAbonnement);
		userRepository.save(abonne);
		
		logHistory( abonne.getName(), "Book ID:  N/A", TypeOpperation.REABONNEMENT,
			EtatOpperation.SUCCES, "Reabonnement reussit." );
		
		carteAbonnementRepository.save(carteAbonnement);
	}
	
	public boolean deleteCard(int abonneID) {
		User abonne = userRepository.findById(abonneID).orElseThrow(() -> new IllegalArgumentException("Abonne not found with the ID: " + abonneID));
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User authenticatedUser = (User) authentication.getPrincipal();
		if (!authenticatedUser.getMail().equalsIgnoreCase(abonne.getMail())) {
			throw new IllegalArgumentException("You are not authorized to update this user.");
		}
		
		if (abonne.getRoleName().equals(Role.GERANT)) {
			logHistory( "Gerant_Name: N/A", "Book ID:  N/A", TypeOpperation.SUSPENSION_CARTE, EtatOpperation.ECHEC, "Carte non suspendue." );
			
			throw new IllegalArgumentException("This operation is only for abonne.");
		}
		
		CarteAbonnement carte = abonne.getCarteAbonnement();
		if (carte == null) {
			logHistory(abonne.getName(), "Book ID:  N/A", TypeOpperation.SUSPENSION_CARTE, EtatOpperation.ECHEC, "Carte non suspendue." );
			
			throw new IllegalArgumentException("This abonne has no card yet.");
		}
		abonne.setCarteAbonnement(null);
		userRepository.save(abonne);
		carteAbonnementRepository.delete(carte);
		
		logHistory(abonne.getName(), "Book ID: " + null, TypeOpperation.SURPESSION_CARTE, EtatOpperation.SUCCES, "Carte supprimee.");
		
		return true;
	}
	
	//	Gerant CardAbonnement Management-------------------------------------------------------------------------------
	public void revoqueCard(int abonneID, int gerantID) {
		User gerant = userRepository.findById(gerantID).orElseThrow(() -> new IllegalArgumentException("Gerant not found with the ID: " + gerantID));
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User aunthenticatedUser = (User) authentication.getPrincipal();
		if (aunthenticatedUser.getId() != gerant.getId()) {
			logHistory( gerant.getName(), "Book ID:  N/A", TypeOpperation.SUSPENSION_CARTE, EtatOpperation.ECHEC, "Carte non suspendue." );
			
			 throw new IllegalArgumentException("You are not authorized to update this user.");
		}
		
		if (gerant.getRoleName().equals(Role.ABONNE)) {
			logHistory( gerant.getName(), "Book ID:  N/A", TypeOpperation.SUSPENSION_CARTE, EtatOpperation.ECHEC, "Carte non suspendue." );
			
			throw new IllegalArgumentException("This is only for gerant access");
		}
		
		User abonne = validateAbonne(abonneID);
		
		CarteAbonnement carteAbonnement = abonne.getCarteAbonnement();
		carteAbonnement.setAvailable(false);
		carteAbonnement.setDuree(0);
		
		abonne.setCarteAbonnement(carteAbonnement);
		
		userRepository.save(abonne);
		
		logHistory(gerant.getName(), "Book ID:  N/A", TypeOpperation.SUSPENSION_CARTE, EtatOpperation.SUCCES, "Carte suspendue." );
		
		carteAbonnementRepository.save(carteAbonnement);
	}
	
	//--------------UTILITIES METHODS-------------------------------------------------------------------------------------------------------------------------------------------
	private CarteAbonnement setCardAvailability(CarteAbonnement carteAbonnement, TypeAbonnement typeAbonnement) {
		carteAbonnement.setTypeAbonnement(typeAbonnement);
		if (typeAbonnement.equals(VIP)) {
			carteAbonnement.setDuree(60);
		} else if (typeAbonnement.equals(CUSTUM)) {
			carteAbonnement.setDuree(30);
		} else if (typeAbonnement.equals(STANDART)) {
			carteAbonnement.setDuree(15);
		}
		
		return carteAbonnement;
	}
	
	private void logHistory(String userName, String bookRef, TypeOpperation type, EtatOpperation etat, String message) {
		historyService.addToHistory(new HistoryRequestDTO(userName, bookRef, type, etat, message));
	}
	
	private User validateAbonne(int abonneID) {
		User abonne = userRepository.findById(abonneID)
			              .orElseThrow(() -> new IllegalArgumentException("Abonné introuvable"));
		if (abonne.getRoleName() != Role.ABONNE) {
			logHistory(abonne.getName(),"BookID: N/A",  TypeOpperation.ABONNEMENT, EtatOpperation.ECHEC, "Rôle invalide");
			throw new IllegalArgumentException("Opération réservée aux abonnés");
		}
		return abonne;
	}
	
	
}
