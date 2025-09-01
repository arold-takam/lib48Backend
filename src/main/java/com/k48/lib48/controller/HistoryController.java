package com.k48.lib48.controller;


import com.k48.lib48.dto.HistoryDTO;
import com.k48.lib48.models.History;
import com.k48.lib48.service.HistoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping(path = "/api/history")
public class HistoryController {

    private HistoryService historyService;

    public HistoryController(HistoryService historyService) {
        this.historyService = historyService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<HistoryDTO>> getAllHistory() {
      return new ResponseEntity<>(historyService.getAllHistory(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HistoryDTO> getHistoryById(@RequestParam Long id) {
        try {
            return new ResponseEntity<>(historyService.getHistoryById(id), HttpStatus.OK);
        }catch (NoSuchElementException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/etat-operation/{etat}")
    public ResponseEntity<List<HistoryDTO>> getHistoryByEtat(@PathVariable String etat) {
        List<HistoryDTO> historyDTOS = historyService.getEtatOperation(etat);
        return new ResponseEntity<>(historyDTOS, HttpStatus.OK);
    }
    @GetMapping("/book-title/{title}")
    public ResponseEntity<List<History>> getByBookTitle(@PathVariable String title) {
        List<History> histories = historyService.getBookTitle(title);
        return ResponseEntity.ok(histories);
    }

    @GetMapping("/operation/{operation}")
    public ResponseEntity<List<History>> getByOperation(@PathVariable String operation) {
        List<History> histories = historyService.getOperation(operation);
        return ResponseEntity.ok(histories);
    }

    @GetMapping("/user/{name}")
    public ResponseEntity<List<History>> getByUser(@PathVariable String name) {
        List<History> histories = historyService.getUser(name);
        return ResponseEntity.ok(histories);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteHistory(@PathVariable Long id) {
        try {
            historyService.deleteHistory(id);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
