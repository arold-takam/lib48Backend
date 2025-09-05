package com.k48.lib48.controller;


import com.k48.lib48.dto.HistoryRequestDTO;
import com.k48.lib48.models.History;
import com.k48.lib48.myEnum.EtatOpperation;
import com.k48.lib48.myEnum.TypeOpperation;
import com.k48.lib48.service.HistoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "/api/history")
public class HistoryController {
	
	private HistoryService historyService;
	
	public HistoryController(HistoryService historyService) {
		this.historyService = historyService;
	}
	
	//    -----------------------------------------------------------------------------------------------------------------------------------------
    @GetMapping(path = "/get/{historyID}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<History> getHistoryById(@PathVariable int historyID){
            try {
                return new ResponseEntity<>(historyService.getHistoryByID(historyID), HttpStatus.OK);
            }catch (IllegalArgumentException e){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
    }
    
	@GetMapping(path = "/get/all/byTypeOperation", produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<List<History>> getAllHistoryByTypeOperation(@RequestParam TypeOpperation typeOpperation) {
		return new ResponseEntity<>(historyService.findByTypeOperations(typeOpperation), HttpStatus.OK);
	}
	
	@GetMapping("/All/byEtat")
	public ResponseEntity<List<History>> getHistoryByEtat(@RequestParam EtatOpperation etatOpperation) {
		return new ResponseEntity<>(historyService.findByEtatOperation(etatOpperation), HttpStatus.OK);
	}
	
	@GetMapping("/user/{userID}")
	public ResponseEntity<List<History>> getByUserName(@RequestBody String userName) {
		try {
                    return new ResponseEntity<>(historyService.findByUserName(userName), HttpStatus.OK);
                }catch (IllegalArgumentException e){
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
	}
	
}
