package com.k48.lib48.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.k48.lib48.dto.BookRequestDTO;
import com.k48.lib48.dto.BookUpDateDTO;
import com.k48.lib48.dto.HistoryRequestDTO;
import com.k48.lib48.models.Book;
import com.k48.lib48.models.Category;
import com.k48.lib48.models.User;
import com.k48.lib48.myEnum.EtatLivre;
import com.k48.lib48.myEnum.EtatOpperation;
import com.k48.lib48.myEnum.Role;
import com.k48.lib48.myEnum.TypeOpperation;
import com.k48.lib48.repository.BookRespositories;
import com.k48.lib48.repository.CategoryRepositories;
import com.k48.lib48.repository.UserRepositories;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class BookServicesTest {

    @Mock
    private BookRespositories bookRepository;

    @Mock
    private CategoryRepositories categoryRepo;

    @Mock
    private HistoryService historyService;

    @Mock
    private UserRepositories userRepositories;

    @InjectMocks
    private BookServices bookServices;

    private BookRequestDTO bookRequestDTO;
    private BookUpDateDTO bookUpDateDTO;
    private Book book;
    private Category category;
    private User gerant;
    private MultipartFile mockFile;

    @BeforeEach
    void setUp() {
        bookRequestDTO = new BookRequestDTO("Test Book", "Test Author", "Test Publisher");
        bookUpDateDTO = new BookUpDateDTO("Updated Book", "Updated Author", true, "Updated Publisher");

        category = new Category();
        category.setId(1L);
        category.setNom("Fiction");

        gerant = new User();
        gerant.setName("Admin");
        gerant.setRoleName(Role.GERANT);

        book = new Book();
        book.setId(1L);
        book.setTitre("Test Book");
        book.setAuteur("Test Author");
        book.setEditeur("Test Publisher");
        book.setEstDisponible(true);
        book.setEtatLivre(EtatLivre.NEUF);
        book.setCategory(category);

        mockFile = mock(MultipartFile.class);
    }

    @Test
    void getAllBooks_ShouldReturnBookList() {
        // Arrange
        when(bookRepository.findAll()).thenReturn(List.of(book));

        // Act
        List<Book> result = bookServices.getAllBooks();

        // Assert
        assertEquals(1, result.size());
        verify(bookRepository, times(1)).findAll();
    }

    @Test
    void getBookId_WithExistingId_ShouldReturnBook() {
        // Arrange
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        // Act
        Book result = bookServices.getBookId(1L);

        // Assert
        assertNotNull(result);
        assertEquals("Test Book", result.getTitre());
        verify(bookRepository, times(1)).findById(1L);
    }

    @Test
    void getBookId_WithNonExistingId_ShouldThrowException() {
        // Arrange
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> {
            bookServices.getBookId(999L);
        });
        verify(bookRepository, times(1)).findById(999L);
    }

    @Test
    void getBooksByTitle_WithExistingTitle_ShouldReturnBook() {
        // Arrange
        when(bookRepository.findByTitreIgnoreCase("Test Book")).thenReturn(book);

        // Act
        Book result = bookServices.getBooksByTitle("Test Book");

        // Assert
        assertNotNull(result);
        assertEquals("Test Book", result.getTitre());
        verify(bookRepository, times(1)).findByTitreIgnoreCase("Test Book");
    }

    @Test
    void getBooksByTitle_WithNonExistingTitle_ShouldThrowException() {
        // Arrange
        when(bookRepository.findByTitreIgnoreCase("Unknown")).thenReturn(null);

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> {
            bookServices.getBooksByTitle("Unknown");
        });
        verify(bookRepository, times(1)).findByTitreIgnoreCase("Unknown");
    }

    @Test
    void getBooksByCategorieNom_WithExistingCategory_ShouldReturnBookList() {
        // Arrange
        when(categoryRepo.findByNomIgnoreCase("Fiction")).thenReturn(category);
        when(bookRepository.findAllByCategory(category)).thenReturn(List.of(book));

        // Act
        List<Book> result = bookServices.getBooksByCategorieNom("Fiction");

        // Assert
        assertEquals(1, result.size());
        verify(categoryRepo, times(1)).findByNomIgnoreCase("Fiction");
        verify(bookRepository, times(1)).findAllByCategory(category);
    }

    @Test
    void getBooksByCategorieNom_WithNonExistingCategory_ShouldThrowException() {
        // Arrange
        when(categoryRepo.findByNomIgnoreCase("Unknown")).thenReturn(null);

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> {
            bookServices.getBooksByCategorieNom("Unknown");
        });
        verify(categoryRepo, times(1)).findByNomIgnoreCase("Unknown");
    }

    @Test
    void createBook_WithValidData_ShouldSaveBook() {
        // Arrange
        when(bookRepository.existsByTitre("Test Book")).thenReturn(false);
        when(categoryRepo.findById(1L)).thenReturn(Optional.of(category));
        when(userRepositories.findAllByRoleName(Role.GERANT)).thenReturn(List.of(gerant));
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        // Act
        Book result = bookServices.createBook(1L, bookRequestDTO, null);

        // Assert
        assertNotNull(result);
        verify(bookRepository, times(1)).existsByTitre("Test Book");
        verify(categoryRepo, times(1)).findById(1L);
        verify(bookRepository, times(1)).save(any(Book.class));
        verify(historyService, times(1)).addToHistory(any(HistoryRequestDTO.class));
    }

    @Test
    void createBook_WithExistingTitle_ShouldThrowException() {
        // Arrange
        when(bookRepository.existsByTitre("Test Book")).thenReturn(true);
        when(userRepositories.findAllByRoleName(Role.GERANT)).thenReturn(List.of(gerant));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            bookServices.createBook(1L, bookRequestDTO, null);
        });
        verify(bookRepository, times(1)).existsByTitre("Test Book");
        verify(historyService, times(1)).addToHistory(any(HistoryRequestDTO.class));
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void createBook_WithNullTitle_ShouldThrowException() {
        // Arrange
        BookRequestDTO invalidDTO = new BookRequestDTO(null, "Author", "Publisher");
        when(userRepositories.findAllByRoleName(Role.GERANT)).thenReturn(List.of(gerant));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            bookServices.createBook(1L, invalidDTO, null);
        });
        verify(historyService, times(1)).addToHistory(any(HistoryRequestDTO.class));
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void updateBook_WithValidData_ShouldUpdateBook() {
        // Arrange
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(categoryRepo.findById(1L)).thenReturn(Optional.of(category));
        when(userRepositories.findAllByRoleName(Role.GERANT)).thenReturn(List.of(gerant));
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        // Act
        Book result = bookServices.updateBook(1L, EtatLivre.BON_ETAT, 1L, bookUpDateDTO, null);

        // Assert
        assertNotNull(result);
        verify(bookRepository, times(1)).findById(1L);
        verify(categoryRepo, times(1)).findById(1L);
        verify(bookRepository, times(1)).save(any(Book.class));
        verify(historyService, times(1)).addToHistory(any(HistoryRequestDTO.class));
    }

    @Test
    void updateBook_WithNonExistingBook_ShouldThrowException() {
        // Arrange
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> {
            bookServices.updateBook(999L, EtatLivre.BON_ETAT, 1L, bookUpDateDTO, null);
        });
        verify(bookRepository, times(1)).findById(999L);
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void deleteBook_WithExistingId_ShouldDeleteBook() {
        // Arrange
        when(bookRepository.existsById(1L)).thenReturn(true);
        when(userRepositories.findAllByRoleName(Role.GERANT)).thenReturn(List.of(gerant));
        doNothing().when(bookRepository).deleteById(1L);

        // Act
        bookServices.deleteBook(1L);

        // Assert
        verify(bookRepository, times(1)).existsById(1L);
        verify(bookRepository, times(1)).deleteById(1L);
        verify(historyService, times(1)).addToHistory(any(HistoryRequestDTO.class));
    }

    @Test
    void deleteBook_WithNonExistingId_ShouldThrowException() {
        // Arrange
        when(bookRepository.existsById(999L)).thenReturn(false);
        when(userRepositories.findAllByRoleName(Role.GERANT)).thenReturn(List.of(gerant));

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> {
            bookServices.deleteBook(999L);
        });
        verify(bookRepository, times(1)).existsById(999L);
        verify(bookRepository, never()).deleteById(anyLong());
        verify(historyService, times(1)).addToHistory(any(HistoryRequestDTO.class));
    }



}