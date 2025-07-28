package com.k48.lib48.controller;

import com.k48.lib48.dto.UserRequestDTO;
import com.k48.lib48.dto.UserResponseDTO;
import com.k48.lib48.models.User;
import com.k48.lib48.myEnum.Role;
import com.k48.lib48.service.UserServices;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "/user")
public class UserController {
	private final UserServices userServices;
	
	public UserController(UserServices userServices) {
		this.userServices = userServices;
	}
	
	//	USER MANAGEMENT----------------------------------------------------------------------------------------------------------------------------------------------------------
	
	@PostMapping(path = "/create", consumes = APPLICATION_JSON_VALUE)
	public ResponseEntity<?>createUser(@RequestParam Role roleName, @RequestBody UserRequestDTO userRequestDTO){
		try {
			
			userServices.createUser(userRequestDTO, roleName);
			
			return new ResponseEntity<>(HttpStatus.CREATED);
		}catch (IllegalArgumentException e){
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
	}
	
	@GetMapping(path = "/get/{userID}", produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<User>getUserID(@PathVariable int userID){
		try {
			User user = userServices.getUserID(userID);
			
			return new ResponseEntity<>(user, HttpStatus.OK);
		}catch (IllegalArgumentException e){
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	@GetMapping(path = "/get/byRole/{role}", produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<User>getUserRoleAndName(@PathVariable Role role, @RequestParam String name){
		try {
			User user = userServices.getUserRoleAndName(name, role);
			
			return new ResponseEntity<>(user, HttpStatus.OK);
		}catch (IllegalArgumentException e){
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	@GetMapping(path = "/get", produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<List<User>>getAllUsers(){
		List<User>userList = userServices.getAllUsers();
		
		return new ResponseEntity<>(userList, HttpStatus.OK);
	}
	
	@GetMapping(path = "/get/all/{role}", produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<List<User>>getAllUsersRole(@PathVariable Role role){
		List<User>userList = userServices.getAllUsersRole(role);
		
		return new ResponseEntity<>(userList, HttpStatus.OK);
	}
	
	@PutMapping(path = "/update/{userID}", consumes = APPLICATION_JSON_VALUE)
	public ResponseEntity<?>updateUser(@PathVariable int userID, @RequestParam Role roleName, @RequestBody UserRequestDTO userRequestDTO){
		try {
			userServices.updateUser(userID, roleName, userRequestDTO);
			
			return new ResponseEntity<>(HttpStatus.OK);
		}catch (IllegalArgumentException e){
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}catch (Exception e){
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@DeleteMapping(path = "/delete/{userID}")
	public ResponseEntity<Void>deleteUser(@PathVariable int userID){
		try {
			boolean delete = userServices.deleteUser(userID);
			
			if (delete){
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		}catch (Exception e){
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
}
