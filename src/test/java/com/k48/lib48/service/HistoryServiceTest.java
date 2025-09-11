package com.k48.lib48.service;

import com.k48.lib48.dto.HistoryRequestDTO;
import com.k48.lib48.models.History;
import com.k48.lib48.models.User;
import com.k48.lib48.myEnum.EtatOpperation;
import com.k48.lib48.myEnum.TypeOpperation;
import com.k48.lib48.repository.HistoryRepository;
import com.k48.lib48.repository.UserRepositories;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HistoryServiceTest {

    @Mock
    private HistoryRepository historyRepository;

    @Mock
    private UserRepositories userRepositories;

    @InjectMocks
    private HistoryService historyService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddToHistory() {
        HistoryRequestDTO dto = new HistoryRequestDTO(
                "Lesley",
                "book12",
                TypeOpperation.AJOUTER_LIVRE,
                EtatOpperation.SUCCES,
                "les détails"
        );

        historyService.addToHistory(dto);

        verify(historyRepository, times(1)).save(any(History.class));
    }

    @Test
    void testGetHistoryByIDExists() {
        History history = new History();
        history.setId(1);
        when(historyRepository.existsById(1)).thenReturn(true);
        when(historyRepository.findById(1)).thenReturn(Optional.of(history));

        History result = historyService.getHistoryByID(1);
        assertEquals(1, result.getId());
    }

    @Test
    void testGetHistoryByIDNotExists() {
        when(historyRepository.existsById(1)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> historyService.getHistoryByID(1));
    }

    @Test
    void testFindByTypeOperations() {
        historyService.findByTypeOperations(TypeOpperation.AJOUTER_LIVRE);
        verify(historyRepository).findByTypeOpperation(TypeOpperation.AJOUTER_LIVRE);
    }

    @Test
    void testFindByEtatOperation() {
        historyService.findByEtatOperation(EtatOpperation.SUCCES);
        verify(historyRepository).findByEtatOperation(EtatOpperation.SUCCES);
    }

    @Test
    void testFindByUserNameExists() {
        User user = new User();
        user.setName("john");

        when(userRepositories.findByNameIgnoreCase("john")).thenReturn(user);
        historyService.findByUserName("john");
        verify(historyRepository).findAllByUserNameIgnoreCase("john");
    }

}

