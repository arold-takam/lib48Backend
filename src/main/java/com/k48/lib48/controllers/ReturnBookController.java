package com.k48.lib48.controllers;

import com.k48.lib48.dto.ReturnRequestDTO;
import com.k48.lib48.dto.ReturnResponseDTO;
import com.k48.lib48.myEnum.EtatLivre;
import com.k48.lib48.service.ReturnBookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(path = "/returnBook")
public class ReturnBookController {
	private final ReturnBookService returnBookService;
	private static final Logger log = LoggerFactory.getLogger(ReturnBookController.class);
	
	public ReturnBookController(ReturnBookService returnBookService) {
		this.returnBookService = returnBookService;
	}

//	RETURNING MANAGEMENT-----------------------------------------------------------------------------------------------------------------------------------------------
	@PreAuthorize("hasRole('GERANT')")
	@PostMapping(path = "/create/{idReturnGerantID}")
	public ResponseEntity<?> makeReturn (@PathVariable int idReturnGerantID, @RequestParam EtatLivre etatLivre, @RequestBody ReturnRequestDTO returnRequestDTO){
		try {
			returnBookService.makeReturn(idReturnGerantID, etatLivre, returnRequestDTO);
			
			return new ResponseEntity<>(HttpStatus.OK);
		}catch (IllegalArgumentException e){
			log.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}catch (Exception e){
			log.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PreAuthorize("hasRole('GERANT')")
	@GetMapping(path = "/get/{gerantID}")
	public ResponseEntity<ReturnResponseDTO> getReturnByID(@PathVariable int gerantID, @RequestParam int returnID){
		try {
			ReturnResponseDTO returnResponseDTO = returnBookService.getReturnByID(gerantID, returnID);
			
			return new ResponseEntity<>(returnResponseDTO, HttpStatus.OK);
		}catch (IllegalArgumentException e){
			log.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}catch (Exception e){
			log.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@PreAuthorize("hasRole('GERANT')")
	@GetMapping(path = "/get/byAbonneID/{gerantID}")
	public ResponseEntity<List<ReturnResponseDTO>> getReturnByAbonneID(@PathVariable int gerantID, @RequestParam int abonneID){
		try {
			List <ReturnResponseDTO> returnResponseDTOList = returnBookService.getReturnByAbonneID(gerantID, abonneID);
			
			return new ResponseEntity<>(returnResponseDTOList, HttpStatus.OK);
		}catch (IllegalArgumentException e){
			log.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}catch (Exception e){
			log.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PreAuthorize("hasRole('GERANT')")
	@GetMapping(path = "/get/all/{gerantID}")
	public ResponseEntity<List<ReturnResponseDTO>> getAllReturns(@PathVariable int gerantID){
		
		return new ResponseEntity<>(returnBookService.getAllReturns(gerantID), HttpStatus.OK);
	}
	
	@PreAuthorize("hasRole('GERANT')")
	@GetMapping(path = "/get/all/byDate/{gerantID}")
	public ResponseEntity<List<ReturnResponseDTO>> getAllReturnsByDate(@PathVariable int gerantID, @RequestParam LocalDate dateRetour){
		
		return new ResponseEntity<>(returnBookService.getAllReturnsByDate(gerantID, dateRetour), HttpStatus.OK);
	}
}
