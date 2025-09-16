package com.k48.lib48.service;

import com.k48.lib48.dto.HistoryRequestDTO;
import com.k48.lib48.dto.UserRequestDTO;
import com.k48.lib48.models.User;
import com.k48.lib48.myEnum.EtatOpperation;
import com.k48.lib48.myEnum.Role;
import com.k48.lib48.myEnum.TypeOpperation;
import com.k48.lib48.repository.UserRepositories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserServices {
	private final UserRepositories userRepositories;
	private final HistoryService historyService;
	private final PasswordEncoder passwordEncoder;
	
	public UserServices(UserRepositories userRepositories, HistoryService historyService, PasswordEncoder passwordEncoder) {
		this.userRepositories = userRepositories;
		this.historyService = historyService;
		this.passwordEncoder = passwordEncoder;
	}
	
	//	USER MANAGEMENT----------------------------------------------------------------------------------------------------------------------------------------------------------
	
	public void createUser(UserRequestDTO userRequestDTO, Role roleName){
		if (userRequestDTO == null ||
			    userRequestDTO.name() == null || userRequestDTO.name().isBlank() ||
			    userRequestDTO.mail() == null || userRequestDTO.mail().isBlank() ||
			    userRequestDTO.password() == null || userRequestDTO.password().isBlank()) {
			throw new IllegalArgumentException("Invalid user data.");
		}
		
		User user = new User();
		
		User existingUser = userRepositories.findByMailIgnoreCase(userRequestDTO.mail());
		if (existingUser != null) {
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
		
		userRepositories.save(user);
		
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
		return userRepositories.findById(userID).orElseThrow(()->new IllegalArgumentException("No user found at the ID: "+userID));
	}
	
	public User getUserRoleAndName(String name, Role role){
		User user = userRepositories.findByNameIgnoreCaseAndRoleName(name, role);
		
		if (user == null){
			throw new IllegalArgumentException("No  "+role+" found at thz name of: "+name);
		}
		
		return user;
	}
	
	public List<User> getAllUsers(){
		return userRepositories.findAll();
	}
	
	public List<User> getAllUsersRole(Role role){
		return userRepositories.findAllByRoleName(role);
	}
	
	public void updateUser(int userID, Role roleName,  UserRequestDTO userRequestDTO){
		User user= userRepositories.findById(userID).orElseThrow(()-> new IllegalArgumentException("User with the ID: "+userID+" not found"));
		
		User existingUser = userRepositories.findByMailIgnoreCase(userRequestDTO.mail());
		
		if (existingUser != null && existingUser.getId() != userID ) {
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
		
		userRepositories.save(user);
		
		HistoryRequestDTO historyRequestDTO = new HistoryRequestDTO(
			userRequestDTO.name(),
			"Book ID: N/A",
			TypeOpperation.MODIFICATION_COMPTE,
			EtatOpperation.SUCCES,
			"Utilisateur  mis a jour.");
		historyService.addToHistory(historyRequestDTO);
	}
	
	public boolean deleteUser(int userID){
		User user= userRepositories.findById(userID).orElseThrow(()-> new IllegalArgumentException("User with the ID: "+userID+" not found"));
		
		userRepositories.deleteById(user.getId());
		
		HistoryRequestDTO historyRequestDTO = new HistoryRequestDTO(
			user.getName(),
			"Book ID:  N/A",
			TypeOpperation.SUPRESSION_COMPTE,
			EtatOpperation.SUCCES,
			"Utilisateur  supprime.");
		historyService.addToHistory(historyRequestDTO);
		
		return true;
	}
	
}
