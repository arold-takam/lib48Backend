package com.k48.lib48.service;


import com.k48.lib48.dto.UserRequestDTO;
import com.k48.lib48.models.User;
import com.k48.lib48.myEnum.Role;
import com.k48.lib48.repository.UserRepositories;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServices {
	private final UserRepositories userRepositories;
	
	public UserServices(UserRepositories userRepositories) {
		this.userRepositories = userRepositories;
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
	}
	
	public User getUserID(int userID){
		Optional<User>userOptional = userRepositories.findById(userID);
		if (userOptional.isEmpty()){
			throw new IllegalArgumentException("No user found at the ID: "+userID);
		}
		
		return userOptional.get();
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
		Optional<User>userOptional = userRepositories.findById(userID);
		
		if (userOptional.isEmpty()){
			throw new IllegalArgumentException("User with the ID: "+userID+" not found");
		}
		
		User user = userOptional.get();
		
		user.setName(userRequestDTO.name());
		user.setMail(userRequestDTO.mail());
		user.setPassword(userRequestDTO.password());
		user.setRoleName(roleName);
		
		userRepositories.save(user);
	}
	
	public boolean deleteUser(int userID){
		Optional<User>userOptional = userRepositories.findById(userID);
		
		if (userOptional.isEmpty()){
			throw new IllegalArgumentException("User with the ID: "+userID+" not found");
		}
		
		userRepositories.deleteById(userID);
		
		return true;
	}
}
