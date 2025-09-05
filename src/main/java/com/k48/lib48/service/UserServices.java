package com.k48.lib48.service;

import com.k48.lib48.dto.HistoryRequestDTO;
import com.k48.lib48.dto.UserRequestDTO;
import com.k48.lib48.models.User;
import com.k48.lib48.myEnum.EtatOpperation;
import com.k48.lib48.myEnum.Role;
import com.k48.lib48.myEnum.TypeOpperation;
import com.k48.lib48.repository.UserRepositories;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserServices {
	private final UserRepositories userRepositories;
	private final HistoryService historyService;
	
	public UserServices(UserRepositories userRepositories, HistoryService historyService) {
		this.userRepositories = userRepositories;
		this.historyService = historyService;
	}
	
//	USER MANAGEMENT----------------------------------------------------------------------------------------------------------------------------------------------------------
	
	public void createUser(UserRequestDTO userRequestDTO, Role roleName){
		if (userRequestDTO == null){
			throw new IllegalArgumentException("This user's information are wrong, try again please.");
		}
		
		User user = new User();
		
		user.setName(userRequestDTO.name());
		user.setMail(userRequestDTO.mail());
		user.setPassword(userRequestDTO.password());
		user.setRoleName(roleName);
		
		userRepositories.save(user);
		
		HistoryRequestDTO historyRequestDTO = new HistoryRequestDTO(userRequestDTO.name(), "Book ID: " + null, TypeOpperation.INSCRIPTION, EtatOpperation.SUCCES, "Utilisateur enregistre.");
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
		
		user.setName(userRequestDTO.name());
		user.setMail(userRequestDTO.mail());
		user.setPassword(userRequestDTO.password());
		user.setRoleName(roleName);
		
		userRepositories.save(user);
		
		HistoryRequestDTO historyRequestDTO = new HistoryRequestDTO(userRequestDTO.name(), "Book ID: " + null, TypeOpperation.MODIFICATION_COMPTE, EtatOpperation.SUCCES, "Utilisateur  mis a jour.");
		historyService.addToHistory(historyRequestDTO);
	}
	
	public boolean deleteUser(int userID){
		User user= userRepositories.findById(userID).orElseThrow(()-> new IllegalArgumentException("User with the ID: "+userID+" not found"));
		
		userRepositories.deleteById(user.getId());
		
		HistoryRequestDTO historyRequestDTO = new HistoryRequestDTO(user.getName(), "Book ID: " + null, TypeOpperation.SUPRESSION_COMPTE, EtatOpperation.SUCCES, "Utilisateur  supprime.");
		historyService.addToHistory(historyRequestDTO);
		
		return true;
	}
}
