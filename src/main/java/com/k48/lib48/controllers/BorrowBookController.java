package com.k48.lib48.controllers;

import com.k48.lib48.dto.BorrowRequestDTO;
import com.k48.lib48.dto.BorrowResponseDTO;
import com.k48.lib48.service.BorrowBookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "/borrowBook")
public class BorrowBookController {
	private final BorrowBookService borrowBookService;
	private static final Logger log = LoggerFactory.getLogger(BorrowBookController.class);
	
	public BorrowBookController(BorrowBookService borrowBookService) {
		this.borrowBookService = borrowBookService;
	}
	
	//	BORROWING MANAGEMENT-----------------------------------------------------------------------------------------------------------------
	@PreAuthorize("hasRole('ABONNE')")
	@PostMapping(path = "/create/{gerantID}")
	public ResponseEntity<?> makeBorrow(@PathVariable int gerantID, @RequestBody BorrowRequestDTO borrowRequestDTO){
		try {
			borrowBookService.makeBorrow(gerantID, borrowRequestDTO);
			
			return new ResponseEntity<>(HttpStatus.OK);
		}catch (IllegalArgumentException e){
			log.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}catch (Exception e){
			log.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping(path = "/get/{gerantID}")
	public ResponseEntity<BorrowResponseDTO> getBorrowByID(@PathVariable int gerantID, @RequestParam int borrowID,@RequestParam int abonneID){
		try {
			BorrowResponseDTO borrowResponseDTO = borrowBookService.getBorrowByID(gerantID,borrowID ,abonneID);
			
			return new ResponseEntity<>(borrowResponseDTO, HttpStatus.OK);
		}catch (IllegalArgumentException e){
			log.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}catch (Exception e){
			log.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping(path = "/get/all", produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<List<BorrowResponseDTO>>getAll(){
		List<BorrowResponseDTO>list = borrowBookService.getAll();
		
		return new ResponseEntity<>(list, HttpStatus.OK);
	}
	
	@GetMapping(path = "/get/all/{gerantID}")
	public ResponseEntity<List<BorrowResponseDTO> >getAllBorrows(@PathVariable int gerantID){
		
		try {
			List<BorrowResponseDTO> borrowResponseDTOList = borrowBookService.getAllBorrows(gerantID);
			
			return new ResponseEntity<>(borrowResponseDTOList, HttpStatus.OK);
		}catch (IllegalArgumentException e){
			log.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}catch (Exception e){
			log.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}

	@GetMapping(path = "/get/all/byAbonneID/{abonneId}")
	public ResponseEntity<List<BorrowResponseDTO>>getAllBorrowsByAbonne_Id(@PathVariable int abonneId){
		try {
			List<BorrowResponseDTO> borrowResponseDTOList = borrowBookService.getAllBorrowsByAbonne_Id(abonneId);
			return new ResponseEntity<>(borrowResponseDTOList, HttpStatus.OK);
		}catch (IllegalArgumentException e){
			log.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}catch (Exception e){
			log.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
}
