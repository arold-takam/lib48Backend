package com.k48.lib48.service;


import com.k48.lib48.dto.BorrowRequestDTO;
import com.k48.lib48.models.*;
import com.k48.lib48.myEnum.Role;
import com.k48.lib48.repository.BookRespositories;
import com.k48.lib48.repository.BorrowBookRepository;
import com.k48.lib48.repository.UserRepositories;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BorrowBookServiceTest {

    @Mock
    private UserRepositories userRepo;
    @Mock
    private BorrowBookRepository borrowRepo;
    @Mock
    private BookRespositories bookRepo;
    @Mock
    private BookServices bookServices;
    @Mock
    private HistoryService historyService;

    @InjectMocks
    private BorrowBookService borrowService;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void makeBorrow_success() {
        // Arrange
        User gerant = new User();
        gerant.setId(1);
        gerant.setRoleName(Role.GERANT);

        User abonne = new User();
        abonne.setId(2);
        abonne.setRoleName(Role.ABONNE);
        CarteAbonnement carte = new CarteAbonnement();
        carte.setAvailable(true);
        carte.setDuree(5);
        abonne.setCarteAbonnement(carte);

        Book book = new Book();
        book.setId(3L);
        book.setEstDisponible(true);

        BorrowRequestDTO dto = new BorrowRequestDTO(abonne.getId(), book.getId(), 7);

        when(userRepo.findById(1)).thenReturn(Optional.of(gerant));
        when(userRepo.findById(2)).thenReturn(Optional.of(abonne));
        when(bookRepo.findById(3L)).thenReturn(Optional.of(book));
        when(bookServices.getBookId(3)).thenReturn(book);

        // Act
        borrowService.makeBorrow(1, dto);

        // Assert
        verify(borrowRepo, times(1)).save(any(BorrowBook.class));
        verify(historyService, times(1)).addToHistory(any());
    }

    @Test
    void makeBorrow_fails_ifGerantNotFound() {
        when(userRepo.findById(1)).thenReturn(Optional.empty());

        BorrowRequestDTO dto = new BorrowRequestDTO(2, 3, 7);

        assertThrows(IllegalArgumentException.class, () -> borrowService.makeBorrow(1, dto));
    }

    @Test
    void makeBorrow_fails_ifBookNotAvailable() {
        User gerant = new User(); gerant.setId(1); gerant.setRoleName(Role.GERANT);
        User abonne = new User(); abonne.setId(2); abonne.setRoleName(Role.ABONNE);
       CarteAbonnement carte = new CarteAbonnement();
       carte.setAvailable(true);
       carte.setDuree(10);
       abonne.setCarteAbonnement(carte);

        Book book = new Book(); book.setId(3L); book.setEstDisponible(false);

        BorrowRequestDTO dto = new BorrowRequestDTO(2, 3, 7);

        when(userRepo.findById(1)).thenReturn(Optional.of(gerant));
        when(userRepo.findById(2)).thenReturn(Optional.of(abonne));
        when(bookRepo.findById(3L)).thenReturn(Optional.of(book));

        assertThrows(IllegalArgumentException.class, () -> borrowService.makeBorrow(1, dto));
    }
}

