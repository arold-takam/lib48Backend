package com.k48.lib48.controller;

import com.k48.lib48.dto.CarteRequestDTO;
import com.k48.lib48.dto.UserRequestDTO;
import com.k48.lib48.dto.UserResponseDTO;
import com.k48.lib48.models.CarteAbonnement;
import com.k48.lib48.models.User;
import com.k48.lib48.myEnum.Role;
import com.k48.lib48.myEnum.TypeAbonnement;
import com.k48.lib48.service.CarteAbonnementService;
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
	private final CarteAbonnementService carteAbonnementService;
	
	public UserController(UserServices userServices, CarteAbonnementService carteAbonnementService) {
		this.userServices = userServices;
		this.carteAbonnementService = carteAbonnementService;
	}
	
	//	USER MANAGEMENT----------------------------------------------------------------------------------------------------------------------------------------------------------
	
	@PostMapping(path = "/create", consumes = APPLICATION_JSON_VALUE)
	public ResponseEntity<?>createUser(@RequestParam Role roleName, @RequestBody UserRequestDTO userRequestDTO){
		try {
			
			userServices.createUser(userRequestDTO, roleName);
			
			return new ResponseEntity<>(HttpStatus.CREATED);
		}catch (IllegalArgumentException e){
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
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
				return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
			}else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		}catch (Exception e){
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	
	
//	CARD MANAGEMENT-------------------------------------------------------------------------------------------------------------------------------------------------------------------------

	//	Abonne CARTEaBONNEMENT MANAGEMENT--------------------------------------------------------------------------------------------------------------
	
	@PostMapping(path = "/create/card/{abonneID}")
	public ResponseEntity<?>createCard(@PathVariable int abonneID, @RequestParam TypeAbonnement typeAbonnement){
		try {
			carteAbonnementService.createCarte(abonneID, typeAbonnement);
			
			return new ResponseEntity<>(HttpStatus.OK);
		}catch (IllegalArgumentException e){
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}catch (Exception e){
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping(path = "/get/card/{abonneID}", produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<CarteAbonnement>getCardID(@PathVariable int abonneID){
		try {
			CarteAbonnement carteAbonnement = carteAbonnementService.getCardID(abonneID);
			
			return new ResponseEntity<>(carteAbonnement, HttpStatus.OK);
		}catch (IllegalArgumentException e){
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}catch (Exception e){
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@PutMapping(path = "/subscribe/card/byAbonne/{abonneID}")
	public ResponseEntity<?>subscribe(@PathVariable int abonneID, @RequestParam TypeAbonnement typeAbonnement){
		try {
			carteAbonnementService.subscribe(abonneID, typeAbonnement);
			
			return new ResponseEntity<>(HttpStatus.OK);
		}catch (IllegalArgumentException e){
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}catch (Exception e){
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@DeleteMapping(path = "/delete/card/{abonneID}")
	public ResponseEntity<?>deleteCard(@PathVariable int abonneID){
		try {
			boolean delete = carteAbonnementService.deleteCard(abonneID);
			
			if (delete){
				return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
			}else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			
		}catch (Exception e){
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	
//	Gerant CardAbonnement Management-------------------------------------------------------------------------------
	@PutMapping(path = "/revoque/card/{abonneID}")
	public ResponseEntity<?>revoqueCard(@PathVariable int abonneID, @RequestParam int gerantID){
		try {
			carteAbonnementService.revoqueCard(abonneID, gerantID);
			
			return new ResponseEntity<>(HttpStatus.OK);
		}catch (IllegalArgumentException e){
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}catch (Exception e){
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping(path = "/get/card/byGerant/{gerantID}")
	public ResponseEntity<List<CarteAbonnement>>getAllCards(@PathVariable int gerantID){
		try {
			List<CarteAbonnement>carteAbonnementList = carteAbonnementService.getAllCards(gerantID);
			
			return new ResponseEntity<>(carteAbonnementList, HttpStatus.OK);
		}catch (IllegalArgumentException e){
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}catch (Exception e){
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
}
