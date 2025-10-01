package com.k48.lib48.service;

import com.k48.lib48.dto.HistoryRequestDTO;
import com.k48.lib48.models.CarteAbonnement;
import com.k48.lib48.models.User;
import com.k48.lib48.myEnum.EtatOpperation;
import com.k48.lib48.myEnum.Role;
import com.k48.lib48.myEnum.TypeAbonnement;
import com.k48.lib48.myEnum.TypeOpperation;
import com.k48.lib48.repository.CarteAbonnementRepository;
import com.k48.lib48.repository.UserRepositories;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import static com.k48.lib48.myEnum.TypeAbonnement.*;

@Service
public class CarteAbonnementService {
	private final CarteAbonnementRepository carteAbonnementRepository;
	private final UserRepositories userRepositories;
	private final HistoryService historyService;
	
	public CarteAbonnementService(CarteAbonnementRepository carteAbonnementRepository, UserRepositories userRepositories, HistoryService historyService) {
		this.carteAbonnementRepository = carteAbonnementRepository;
		this.userRepositories = userRepositories;
		this.historyService = historyService;
	}

//	Abonne CARTEaBONNEMENT MANAGEMENT--------------------------------------------------------------------------------------------------------------
	
	public void createCarte(int abonneID, TypeAbonnement typeAbonnement) {
		User abonne = userRepositories.findById(abonneID).orElseThrow(() -> new IllegalArgumentException("Abonne not found with the ID: " + abonneID));
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User authenticatedUser = (User) authentication.getPrincipal();
		if (!authenticatedUser.getMail().equalsIgnoreCase(abonne.getMail())) {
			throw new IllegalArgumentException("You are not authorized to update this user.");
		}
		
		if (abonne.getRoleName().equals(Role.GERANT)) {
			HistoryRequestDTO historyRequestDTO = new HistoryRequestDTO(abonne.getName(), "Book ID: " + null, TypeOpperation.ABONNEMENT, EtatOpperation.ECHEC, "Abonnement rate.");
			historyService.addToHistory(historyRequestDTO);
			
			throw new IllegalArgumentException("This operation is only for abonne.");
		}
		
		if (abonne.getCarteAbonnement() != null) {
			HistoryRequestDTO historyRequestDTO = new HistoryRequestDTO(abonne.getName(), "Book ID: " + null, TypeOpperation.ABONNEMENT, EtatOpperation.ECHEC, "Abonnement rate.");
			historyService.addToHistory(historyRequestDTO);
			
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
		
		HistoryRequestDTO historyRequestDTO = new HistoryRequestDTO(abonne.getName(), "Book ID: " + null, TypeOpperation.ABONNEMENT, EtatOpperation.SUCCES, "Abonnement reussi.");
		historyService.addToHistory(historyRequestDTO);
		
		userRepositories.save(abonne);
		
	}
	
	public CarteAbonnement getCardID(int abonneID) {
		User abonne = userRepositories.findById(abonneID).orElseThrow(() -> new IllegalArgumentException("Abonne not found with the ID: " + abonneID));
		
		CarteAbonnement carteAbonnement = abonne.getCarteAbonnement();
		
		if (carteAbonnement == null) {
			throw new IllegalArgumentException("This abonne has no card yet.");
		}
		
		return carteAbonnement;
	}
	
	public void subscribe(int abonneID, TypeAbonnement typeAbonnement) {
		User abonne = userRepositories.findById(abonneID).orElseThrow(() -> new IllegalArgumentException("Abonne not found with the ID: " + abonneID));
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User authenticatedUser = (User) authentication.getPrincipal();
		if (!authenticatedUser.getMail().equalsIgnoreCase(abonne.getMail())) {
			throw new IllegalArgumentException("You are not authorized to update this user.");
		}
		
		if (abonne.getRoleName().equals(Role.GERANT)) {
			HistoryRequestDTO historyRequestDTO = new HistoryRequestDTO(abonne.getName(), "Book ID: " + null, TypeOpperation.REABONNEMENT, EtatOpperation.ECHEC, "Reabonnement rate.");
			historyService.addToHistory(historyRequestDTO);
			
			throw new IllegalArgumentException("This operation is only for abonne.");
		}
		
		CarteAbonnement carteAbonnement = abonne.getCarteAbonnement();
		
		if (carteAbonnement == null) {
			HistoryRequestDTO historyRequestDTO = new HistoryRequestDTO(abonne.getName(), "Book ID: " + null, TypeOpperation.REABONNEMENT, EtatOpperation.ECHEC, "Reabonnement rate.");
			historyService.addToHistory(historyRequestDTO);
			
			throw new IllegalArgumentException("Abonne has no card yet.");
		}
		
		carteAbonnement.setAvailable(true);
		
		carteAbonnement = setCardAvailability(carteAbonnement, typeAbonnement);
		
		abonne.setCarteAbonnement(carteAbonnement);
		userRepositories.save(abonne);
		
		HistoryRequestDTO historyRequestDTO = new HistoryRequestDTO(abonne.getName(), "Book ID: " + null, TypeOpperation.REABONNEMENT, EtatOpperation.SUCCES, "Reabonnement reussit.");
		historyService.addToHistory(historyRequestDTO);
		
		carteAbonnementRepository.save(carteAbonnement);
	}
	
	public boolean deleteCard(int abonneID) {
		User abonne = userRepositories.findById(abonneID).orElseThrow(() -> new IllegalArgumentException("Abonne not found with the ID: " + abonneID));
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User authenticatedUser = (User) authentication.getPrincipal();
		if (!authenticatedUser.getMail().equalsIgnoreCase(abonne.getMail())) {
			throw new IllegalArgumentException("You are not authorized to update this user.");
		}
		
		if (abonne.getRoleName().equals(Role.GERANT)) {
			HistoryRequestDTO historyRequestDTO = new HistoryRequestDTO(abonne.getName(), "Book ID: " + null, TypeOpperation.SURPESSION_CARTE, EtatOpperation.ECHEC, "Carte non supprimee.");
			historyService.addToHistory(historyRequestDTO);
			
			throw new IllegalArgumentException("This operation is only for abonne.");
		}
		
		CarteAbonnement carte = abonne.getCarteAbonnement();
		if (carte == null) {
			HistoryRequestDTO historyRequestDTO = new HistoryRequestDTO(abonne.getName(), "Book ID: " + null, TypeOpperation.SURPESSION_CARTE, EtatOpperation.ECHEC, "Carte non supprimee.");
			historyService.addToHistory(historyRequestDTO);
			
			throw new IllegalArgumentException("This abonne has no card yet.");
		}
		abonne.setCarteAbonnement(null);
		userRepositories.save(abonne);
		carteAbonnementRepository.delete(carte);
		
		HistoryRequestDTO historyRequestDTO = new HistoryRequestDTO(abonne.getName(), "Book ID: " + null, TypeOpperation.SURPESSION_CARTE, EtatOpperation.SUCCES, "Carte supprimee.");
		historyService.addToHistory(historyRequestDTO);
		
		return true;
	}
	
	//	Gerant CardAbonnement Management-------------------------------------------------------------------------------
	public void revoqueCard(int abonneID, int gerantID) {
		User gerant = userRepositories.findById(gerantID).orElseThrow(() -> new IllegalArgumentException("Gerant not found with the ID: " + gerantID));
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User aunthenticatedUser = (User) authentication.getPrincipal();
		if (aunthenticatedUser.getId() != gerant.getId()) {
			 throw new IllegalArgumentException("You are not authorized to update this user.");
		}
		
		if (gerant.getRoleName().equals(Role.ABONNE)) {
			HistoryRequestDTO historyRequestDTO = new HistoryRequestDTO(gerant.getName(), "Book ID: " + null, TypeOpperation.SUSPENSION_CARTE, EtatOpperation.ECHEC, "Carte non suspendue.");
			historyService.addToHistory(historyRequestDTO);
			
			throw new IllegalArgumentException("This is only for gerant access");
		}
		
		User abonne = userRepositories.findById(abonneID).orElseThrow(() -> new IllegalArgumentException("Abonne not found with the ID: " + abonneID));
		
		CarteAbonnement carteAbonnement = abonne.getCarteAbonnement();
		carteAbonnement.setAvailable(false);
		carteAbonnement.setDuree(0);
		
		abonne.setCarteAbonnement(carteAbonnement);
		
		userRepositories.save(abonne);
		
		HistoryRequestDTO historyRequestDTO = new HistoryRequestDTO(gerant.getName(), "Book ID: " + null, TypeOpperation.SUSPENSION_CARTE, EtatOpperation.SUCCES, "Carte suspendue.");
		historyService.addToHistory(historyRequestDTO);
		
		carteAbonnementRepository.save(carteAbonnement);
	}
	
	public List<CarteAbonnement> getAllCards(int gerantID) {
		User gerant = userRepositories.findById(gerantID).orElseThrow(() -> new IllegalArgumentException("Gerant not found with the ID: " + gerantID));
		
		if (gerant.getRoleName().equals(Role.ABONNE)) {
			throw new IllegalArgumentException("This is only for gerant access");
		}
		
		return carteAbonnementRepository.findAll();
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
}
