package com.k48.lib48.service;

import com.k48.lib48.dto.HistoryRequestDTO;
import com.k48.lib48.dto.UserRequestDTO;
import com.k48.lib48.models.User;
import com.k48.lib48.myEnum.EtatOpperation;
import com.k48.lib48.myEnum.Role;
import com.k48.lib48.myEnum.TypeOpperation;
import com.k48.lib48.repository.BorrowBookRepository;
import com.k48.lib48.repository.ReturnBookRepository;
import com.k48.lib48.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServices {
	private final UserRepository userRepository;
	private final HistoryService historyService;
	private final PasswordEncoder passwordEncoder;
	private final BorrowBookRepository borrowBookRepository;
	private final ReturnBookRepository returnBookRepository;
	
	public UserServices(UserRepository userRepository, HistoryService historyService, PasswordEncoder passwordEncoder, BorrowBookRepository borrowBookRepository, ReturnBookRepository returnBookRepository) {
		this.userRepository = userRepository;
		this.historyService = historyService;
		this.passwordEncoder = passwordEncoder;
		this.borrowBookRepository = borrowBookRepository;
		this.returnBookRepository = returnBookRepository;
	}
	
	//	USER MANAGEMENT----------------------------------------------------------------------------------------------------------------------------------------------------------
	
	public void createUser(UserRequestDTO userRequestDTO, Role roleName){
		if (userRequestDTO == null ||
			    userRequestDTO.name() == null || userRequestDTO.name().isBlank() ||
			    userRequestDTO.mail() == null || userRequestDTO.mail().isBlank() ||
			    userRequestDTO.password() == null || userRequestDTO.password().isBlank()) {
			
			HistoryRequestDTO historyRequestDTO = new HistoryRequestDTO(
				userRequestDTO.name(),
				"N/A",
				TypeOpperation.INSCRIPTION,
				EtatOpperation.ECHEC,
				"Utilisateur non enregistre."
			);
			historyService.addToHistory(historyRequestDTO);
			
			throw new IllegalArgumentException("Invalid user data.");
		}
		
		User user = new User();
		
		User existingUser = userRepository.findByMailIgnoreCase(userRequestDTO.mail());
		if (existingUser != null) {
			HistoryRequestDTO historyRequestDTO = new HistoryRequestDTO(
				userRequestDTO.name(),
				"N/A",
				TypeOpperation.INSCRIPTION,
				EtatOpperation.ECHEC,
				"Utilisateur non enregistre."
			);
			historyService.addToHistory(historyRequestDTO);
			throw new IllegalArgumentException("Email already in use.");
		}
		
		String userName = userRequestDTO.name().trim();
		user.setName(userName);
		user.setMail(userRequestDTO.mail());
		if (!userRequestDTO.password().startsWith("$2a$")){
			user.setPassword(passwordEncoder.encode(userRequestDTO.password()));
		}else {
			user.setPassword(userRequestDTO.password());
		}
		user.setRoleName(roleName);
		
		userRepository.save(user);
		
		HistoryRequestDTO historyRequestDTO = new HistoryRequestDTO(
			userRequestDTO.name(),
			"N/A",
			TypeOpperation.INSCRIPTION,
			EtatOpperation.SUCCES,
			"Utilisateur enregistré."
		);
		historyService.addToHistory(historyRequestDTO);
	}
	
	public User getUserID(int userID){
		User user = userRepository.findById(userID).orElseThrow(()->new IllegalArgumentException("No user found at the ID: "+userID));
	
		
		return user;
	}
	
	public User getUserRoleAndName(String name, Role role){
		User user = userRepository.findByNameIgnoreCaseAndRoleName(name, role);
		if (user == null){
			throw new IllegalArgumentException("No  "+role+" found at the name of: "+name);
		}
		
		return user;
	}
	
	public List<User> getAllUsers(){
		return userRepository.findAll();
	}
	
	public List<User> getAllUsersRole(Role role){
		return userRepository.findAllByRoleName(role);
	}
	
	public User findByMailIgnoreCase(String mail){
		if (mail.isBlank()){
			throw new IllegalArgumentException("Entrer a right mail.");
		}
		
		return userRepository.findByMailIgnoreCase(mail);
	}
	
	public void updateUser(int userID, Role roleName,  UserRequestDTO userRequestDTO){
		User user= userRepository.findById(userID).orElseThrow(()-> new IllegalArgumentException("User with the ID: "+userID+" not found"));
		
		User existingUser = userRepository.findByMailIgnoreCase(userRequestDTO.mail());
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User aunthenticatedUser = (User) authentication.getPrincipal();
		if (!aunthenticatedUser.getMail().equalsIgnoreCase(user.getMail())){
			logHistory( user.getName(), "Book ID: N/A", TypeOpperation.MODIFICATION_COMPTE, EtatOpperation.ECHEC, "Utilisateur non mis a jour.");
			
			throw new IllegalArgumentException("You are not authorized to update this user.");
		}
		
		if (existingUser != null && existingUser.getId() != userID ) {
			logHistory( user.getName(), "Book ID: N/A", TypeOpperation.MODIFICATION_COMPTE, EtatOpperation.ECHEC, "Utilisateur non mis a jour.");
			
			throw new IllegalArgumentException("Email already in use.");
		}
		
		user.setName(userRequestDTO.name());
		user.setMail(userRequestDTO.mail());
		if (!userRequestDTO.password().startsWith("$2a$")){
			user.setPassword(passwordEncoder.encode(userRequestDTO.password()));
		}else {
			user.setPassword(userRequestDTO.password());
		}
		user.setRoleName(roleName);
		
		userRepository.save(user);
		
		logHistory( userRequestDTO.name(), "Book ID: N/A", TypeOpperation.MODIFICATION_COMPTE, EtatOpperation.SUCCES, "Utilisateur  mis a jour.");
	}
	
	@Transactional
	public boolean deleteUser(int userID){
		User user= userRepository.findById(userID).orElseThrow(()-> new IllegalArgumentException("User with the ID: "+userID+" not found"));
		
		logHistory( user.getName(), "Book ID:  N/A", TypeOpperation.SUPRESSION_COMPTE, EtatOpperation.SUCCES, "Utilisateur  supprime." );
		
//		DELETION PROCESS TO IMPROVE IN THE V2)--------------------------------------------
		returnBookRepository.deleteAllByBorrowBookConcerned_Abonne(user);
		borrowBookRepository.deleteAllByAbonne(user);
//		-------------------------------------------------------------------------------------------------------------
		
		userRepository.delete(user);
		
		return true;
	}
	
//	UTILITIES METHODS-----------------------------------------------------------------------------------------------------------------------
	private void logHistory(String userName, String bookRef, TypeOpperation type, EtatOpperation etat, String message) {
		historyService.addToHistory(new HistoryRequestDTO(userName, bookRef, type, etat, message));
	}
	
}
