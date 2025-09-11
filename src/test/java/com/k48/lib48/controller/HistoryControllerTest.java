package com.k48.lib48.controller;

import com.k48.lib48.controllers.HistoryController;
import com.k48.lib48.models.History;
import com.k48.lib48.myEnum.EtatOpperation;
import com.k48.lib48.myEnum.TypeOpperation;
import com.k48.lib48.service.HistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HistoryControllerTest {

    @Mock
    private HistoryService historyService;

    @InjectMocks
    private HistoryController historyController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetHistoryByIdFound() {
        History history = new History();
        history.setId(1);
        when(historyService.getHistoryByID(1)).thenReturn(history);

        ResponseEntity<History> response = historyController.getHistoryById(1);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getId());
    }

    @Test
    void testGetHistoryByIdNotFound() {
        when(historyService.getHistoryByID(1)).thenThrow(new IllegalArgumentException());

        ResponseEntity<History> response = historyController.getHistoryById(1);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetAllHistoryByTypeOperation() {
        History h = new History();
        h.setTypeOpperation(TypeOpperation.AJOUTER_LIVRE);
        when(historyService.findByTypeOperations(TypeOpperation.AJOUTER_LIVRE)).thenReturn(List.of(h));

        ResponseEntity<List<History>> response = historyController.getAllHistoryByTypeOperation(TypeOpperation.AJOUTER_LIVRE);
        assertEquals(1, response.getBody().size());
        assertEquals(TypeOpperation.AJOUTER_LIVRE, response.getBody().get(0).getTypeOpperation());
    }

    @Test
    void testGetHistoryByEtat() {
        History h = new History();
        h.setEtatOperation(EtatOpperation.SUCCES);
        when(historyService.findByEtatOperation(EtatOpperation.SUCCES)).thenReturn(List.of(h));

        ResponseEntity<List<History>> response = historyController.getHistoryByEtat(EtatOpperation.SUCCES);
        assertEquals(1, response.getBody().size());
        assertEquals(EtatOpperation.SUCCES, response.getBody().get(0).getEtatOperation());
    }

    @Test
    void testGetByUserNameFound() {
        History h = new History();
        h.setUserName("john");
        when(historyService.findByUserName("john")).thenReturn(List.of(h));

        ResponseEntity<List<History>> response = historyController.getByUserName("john");
        assertEquals(1, response.getBody().size());
        assertEquals("john", response.getBody().get(0).getUserName());
    }

    @Test
    void testGetByUserNameNotFound() {
        when(historyService.findByUserName("unknown")).thenThrow(new IllegalArgumentException());

        ResponseEntity<List<History>> response = historyController.getByUserName("unknown");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}

