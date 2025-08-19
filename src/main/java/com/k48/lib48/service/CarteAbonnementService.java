package com.k48.lib48.service;


import com.k48.lib48.models.CarteAbonnement;
import com.k48.lib48.models.User;
import com.k48.lib48.myEnum.Role;
import com.k48.lib48.myEnum.TypeAbonnement;
import com.k48.lib48.repository.CarteAbonnementRepository;
import com.k48.lib48.repository.UserRepositories;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import static com.k48.lib48.myEnum.TypeAbonnement.*;

@Service
public class CarteAbonnementService {
	private final CarteAbonnementRepository carteAbonnementRepository;
	private final UserRepositories userRepositories;
	
	public CarteAbonnementService(CarteAbonnementRepository carteAbonnementRepository, UserRepositories userRepositories) {
		this.carteAbonnementRepository = carteAbonnementRepository;
		this.userRepositories = userRepositories;
	}
	
//	Abonne CARTEaBONNEMENT MANAGEMENT--------------------------------------------------------------------------------------------------------------

	public void createCarte(int abonneID, TypeAbonnement typeAbonnement){
		Optional<User>optionalUser = userRepositories.findById(abonneID);
		
		if (optionalUser.isEmpty()){
			throw new IllegalArgumentException("Abonne not found with the ID: "+abonneID);
		}
		
		User abonne = optionalUser.get();
		
		if (abonne.getRoleName().equals(Role.GERANT)){
			throw new IllegalArgumentException("This operation is only for abonne.");
		}
		
		if (abonne.getCarteAbonnement() != null) {
			throw new IllegalArgumentException(abonne.getName()+" has already a card; use it.");
		}
		
		CarteAbonnement carteAbonnement = new CarteAbonnement();
		
		long min = 100_000_000L;
		long max = 999_999_999L;
		Random random = new Random();
		long randomNumber = min + random.nextLong(max - min + 1);
		
		carteAbonnement.setCardNumber(randomNumber);
		
		carteAbonnement.setTypeAbonnement(typeAbonnement);
		carteAbonnement.setAvailable(true);
		if (typeAbonnement.equals(VIP)){
			carteAbonnement.setDuree(60);
		} else if (typeAbonnement.equals(CUSTUM)) {
			carteAbonnement.setDuree(30);
		} else if (typeAbonnement.equals(STANDART)) {
			carteAbonnement.setDuree(15);
		}
		
		carteAbonnementRepository.save(carteAbonnement);
		
		abonne.setCarteAbonnement(carteAbonnement);
		
		userRepositories.save(abonne);
		
	}
	
	public CarteAbonnement getCardID(int abonneID){
		Optional<User>optionalUser = userRepositories.findById(abonneID);
		
		if (optionalUser.isEmpty()){
			throw new IllegalArgumentException("Abonne not found with the ID: "+abonneID);
		}
		
		User abonne = optionalUser.get();
		
		CarteAbonnement carteAbonnement = abonne.getCarteAbonnement();
		
		if (carteAbonnement == null){
			throw new IllegalArgumentException("This abonne has no card yet.");
		}
		
		return carteAbonnement;
	}
	
	public void subscribe(int abonneID, TypeAbonnement typeAbonnement){
		Optional<User>optionalUser = userRepositories.findById(abonneID);
		
		if (optionalUser.isEmpty()){
			throw new IllegalArgumentException("Abonne not found with the ID: "+abonneID);
		}
		
		User abonne = optionalUser.get();
		
		if (abonne.getRoleName().equals(Role.GERANT)){
			throw new IllegalArgumentException("This operation is only for abonne.");
		}
		
		CarteAbonnement carteAbonnement = abonne.getCarteAbonnement();
		
		if (carteAbonnement == null) {
			throw new IllegalArgumentException("Abonne has no card yet.");
		}
		
		carteAbonnement.setTypeAbonnement(typeAbonnement);
		carteAbonnement.setAvailable(true);
		if (carteAbonnement.getTypeAbonnement().equals(VIP)){
			carteAbonnement.setDuree(60);
		} else if (carteAbonnement.getTypeAbonnement().equals(CUSTUM)) {
			carteAbonnement.setDuree(30);
		} else if (carteAbonnement.getTypeAbonnement().equals(STANDART)) {
			carteAbonnement.setDuree(15);
		}
		
		abonne.setCarteAbonnement(carteAbonnement);
		userRepositories.save(abonne);
		
		carteAbonnementRepository.save(carteAbonnement);
	}
	
	public boolean deleteCard(int abonneID){
		Optional<User>optionalUser = userRepositories.findById(abonneID);
		
		if (optionalUser.isEmpty()){
			throw new IllegalArgumentException("Abonne not found with the ID: "+abonneID);
		}
		
		User abonne = optionalUser.get();
		
		if (abonne.getRoleName().equals(Role.GERANT)){
			throw new IllegalArgumentException("This operation is only for abonne.");
		}
		
		abonne.setCarteAbonnement(null);
		
		userRepositories.save(abonne);
		
		carteAbonnementRepository.delete(abonne.getCarteAbonnement());
		
		return  true;
	}
	
//	Gerant CardAbonnement Management-------------------------------------------------------------------------------
	public void revoqueCard(int abonneID, int gerantID){
		Optional<User>optionalGerant = userRepositories.findById(gerantID);
		
		if (optionalGerant.isEmpty()){
			throw new IllegalArgumentException("Gerant not found with the ID: "+gerantID);
		}
		
		User gerant = optionalGerant.get();
		
		if (gerant.getRoleName().equals(Role.ABONNE)){
			throw new IllegalArgumentException("This is only for gerant access");
		}
		
		Optional<User>optionalUser = userRepositories.findById(abonneID);
		
		if (optionalUser.isEmpty()){
			throw new IllegalArgumentException("Abonne not found with the ID: "+abonneID);
		}
		
		User abonne = optionalUser.get();
		
		CarteAbonnement carteAbonnement = abonne.getCarteAbonnement();
		
		carteAbonnement.setAvailable(false);
		carteAbonnement.setDuree(0);
		
		abonne.setCarteAbonnement(carteAbonnement);
		
		userRepositories.save(abonne);
		
		carteAbonnementRepository.save(carteAbonnement);
	}
	
	public List<CarteAbonnement>getAllCards(int gerantID){
		Optional<User>optionalGerant = userRepositories.findById(gerantID);
		
		if (optionalGerant.isEmpty()){
			throw new IllegalArgumentException("Gerant not found with the ID: "+gerantID);
		}
		
		User gerant = optionalGerant.get();
		
		if (gerant.getRoleName().equals(Role.ABONNE)){
			throw new IllegalArgumentException("This is only for gerant access");
		}
		
		return carteAbonnementRepository.findAll();
	}
}
