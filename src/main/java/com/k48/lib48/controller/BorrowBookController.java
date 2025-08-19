package com.k48.lib48.controller;

import com.k48.lib48.dto.BorrowRequestDTO;
import com.k48.lib48.dto.BorrowResponseDTO;
import com.k48.lib48.models.BorrowBook;
import com.k48.lib48.myEnum.Role;
import com.k48.lib48.service.BorrowBookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/borrowBook")
public class BorrowBookController {
	private BorrowBookService borrowBookService;
	
	public BorrowBookController(BorrowBookService borrowBookService) {
		this.borrowBookService = borrowBookService;
	}
	
	//	BORROWING MANAGEMENT-----------------------------------------------------------------------------------------------------------------
	
	@PostMapping(path = "/create{gerantID}")
	public ResponseEntity<?> makeBorrow(@PathVariable int gerantID, @RequestBody BorrowRequestDTO borrowRequestDTO){
		try {
			borrowBookService.makeBorrow(gerantID, borrowRequestDTO);
			
			return new ResponseEntity<>(HttpStatus.OK);
		}catch (IllegalArgumentException e){
			System.out.println(e.getMessage());
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}catch (Exception e){
			System.out.println(e.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	
	@GetMapping(path = "/get/{gerantID}")
	public ResponseEntity<BorrowResponseDTO> getBorrowByID(@PathVariable int gerantID, @RequestParam int abonneID){
		try {
			BorrowResponseDTO borrowResponseDTO = borrowBookService.getBorrowByID(gerantID, abonneID);
			
			return new ResponseEntity<>(borrowResponseDTO, HttpStatus.OK);
		}catch (IllegalArgumentException e){
			System.out.println(e.getMessage());
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}catch (Exception e){
			System.out.println(e.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping(path = "/get/all/{gerantID}")
	public ResponseEntity<List<BorrowResponseDTO> >getAllBorrows(@PathVariable int gerantID){
		
		try {
			List<BorrowResponseDTO> borrowResponseDTOList = borrowBookService.getAllBorrows(gerantID);
			
			return new ResponseEntity<>(borrowResponseDTOList, HttpStatus.OK);
		}catch (IllegalArgumentException e){
			System.out.println(e.getMessage());
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}catch (Exception e){
			System.out.println(e.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
	}
	
}
