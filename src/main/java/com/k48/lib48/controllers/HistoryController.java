package com.k48.lib48.controllers;


import com.k48.lib48.models.History;
import com.k48.lib48.myEnum.EtatOpperation;
import com.k48.lib48.myEnum.TypeOpperation;
import com.k48.lib48.service.HistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "/history")
public class HistoryController {
	private HistoryService historyService;
	private static final Logger log = LoggerFactory.getLogger(HistoryController.class);
	
	public HistoryController(HistoryService historyService) {
		this.historyService = historyService;
	}
	
	//    ----------------------------------------------------------------------------------------------------------------------------------------
   @PreAuthorize("hasRole('GERANT')")
    @GetMapping(path = "/get/{historyID}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<History> getHistoryById(@PathVariable int historyID){
            try {
                return new ResponseEntity<>(historyService.getHistoryByID(historyID), HttpStatus.OK);
            }catch (IllegalArgumentException e){
		log.error(e.getMessage());
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }catch (Exception e){
		log.error(e.getMessage());
		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
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
	
	@PreAuthorize("hasRole('GERANT')")
	@GetMapping("/get/user")
	public ResponseEntity<List<History>> getByUserName(@RequestParam String userName) {
		try {
                    return new ResponseEntity<>(historyService.findByUserName(userName), HttpStatus.OK);
                }catch (IllegalArgumentException e){
			log.error(e.getMessage());
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                } catch (Exception e) {
			log.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
}
