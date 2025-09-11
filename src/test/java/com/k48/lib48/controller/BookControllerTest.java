package com.k48.lib48.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.k48.lib48.dto.BookRequestDTO;
import com.k48.lib48.dto.BookUpDateDTO;
import com.k48.lib48.models.Book;
import com.k48.lib48.myEnum.EtatLivre;
import com.k48.lib48.controllers.BookController;
import com.k48.lib48.service.BookServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.mock.web.MockMultipartFile;
import java.util.List;
import java.util.NoSuchElementException;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookServices bookServices;

    @Autowired
    private ObjectMapper objectMapper;

    private Book book;
    private BookRequestDTO bookRequestDTO;
    private BookUpDateDTO bookUpDateDTO;
    private MockMultipartFile mockImage;

    @BeforeEach
    void setUp() {
        book = new Book();
        book.setId(1L);
        book.setTitre("Test Book");
        book.setAuteur("Test Author");
        book.setEditeur("Test Publisher");

        bookRequestDTO = new BookRequestDTO("Test Book", "Test Author", "Test Publisher");
        bookUpDateDTO = new BookUpDateDTO("Updated Book", "Updated Author",true , "Updated Publisher");

        mockImage = new MockMultipartFile(
                "coverImage",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes()
        );
    }

    @Test
    void getAllBooks_ShouldReturnBookList() throws Exception {
        // Arrange
        when(bookServices.getAllBooks()).thenReturn(List.of(book));

        // Act & Assert
        mockMvc.perform(get("/books/get/All"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].titre").value("Test Book"));

        verify(bookServices, times(1)).getAllBooks();
    }

    @Test
    void getBookById_WithExistingId_ShouldReturnBook() throws Exception {
        // Arrange
        when(bookServices.getBookId(1L)).thenReturn(book);

        // Act & Assert
        mockMvc.perform(get("/books/get/ById/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titre").value("Test Book"));

        verify(bookServices, times(1)).getBookId(1L);
    }



    @Test
    void getBookByTitle_WithExistingTitle_ShouldReturnBook() throws Exception {
        // Arrange
        when(bookServices.getBooksByTitle("Test Book")).thenReturn(book);

        // Act & Assert
        mockMvc.perform(get("/books/get/ByTitle")
                        .param("title", "Test Book"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titre").value("Test Book"));

        verify(bookServices, times(1)).getBooksByTitle("Test Book");
    }

    @Test
    void getBookByCategorie_WithExistingCategory_ShouldReturnBookList() throws Exception {
        // Arrange
        when(bookServices.getBooksByCategorieNom("Fiction")).thenReturn(List.of(book));

        // Act & Assert
        mockMvc.perform(get("/books/get/ByCategory")
                        .param("categorie", "Fiction"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].titre").value("Test Book"));

        verify(bookServices, times(1)).getBooksByCategorieNom("Fiction");
    }

    @Test
    void createBook_WithValidData_ShouldReturnCreated() throws Exception {
        // Arrange
        when(bookServices.createBook(eq(1L), any(BookRequestDTO.class), any()))
                .thenReturn(book);

        // Act & Assert
        mockMvc.perform(multipart("/books/create")
                        .file(mockImage)
                        .param("idCategory", "1")
                        .param("titre", "Test Book")
                        .param("auteur", "Test Author")
                        .param("editeur", "Test Publisher"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titre").value("Test Book"));

        verify(bookServices, times(1)).createBook(eq(1L), any(BookRequestDTO.class), any());
    }

    @Test
    void createBook_WithNonExistingCategory_ShouldReturnNotFound() throws Exception {
        // Arrange
        when(bookServices.createBook(eq(999L), any(BookRequestDTO.class), any()))
                .thenThrow(new NoSuchElementException("Category not found"));

        // Act & Assert
        mockMvc.perform(multipart("/books/create")
                        .file(mockImage)
                        .param("idCategory", "999")
                        .param("titre", "Test Book")
                        .param("auteur", "Test Author")
                        .param("editeur", "Test Publisher"))
                .andExpect(status().isNotFound());

        verify(bookServices, times(1)).createBook(eq(999L), any(BookRequestDTO.class), any());
    }

    @Test
    void updateBook_WithValidData_ShouldReturnOk() throws Exception {
        // Arrange
        when(bookServices.updateBook(eq(1L), any(EtatLivre.class), eq(1L), any(BookUpDateDTO.class), any()))
                .thenReturn(book);

        // Act & Assert
        mockMvc.perform(multipart("/books/update/{id}", 1L)
                        .file(mockImage)
                        .param("livreEtat", "NEUF")
                        .param("idCategory", "1")
                        .param("titre", "Updated Book")
                        .param("auteur", "Updated Author")
                        .param("editeur", "Updated Publisher")
                        .param("estDisponible", "true")
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titre").value("Test Book"));

        verify(bookServices, times(1)).updateBook(eq(1L), any(EtatLivre.class), eq(1L), any(BookUpDateDTO.class), any());
    }

    @Test
    void updateBook_WithNonExistingBook_ShouldReturnNotFound() throws Exception {
        // Arrange
        when(bookServices.updateBook(eq(999L), any(EtatLivre.class), eq(1L), any(BookUpDateDTO.class), any()))
                .thenThrow(new NoSuchElementException("Book not found"));

        // Act & Assert
        mockMvc.perform(multipart("/books/update/{id}", 999L)
                        .file(mockImage)
                        .param("livreEtat", "NEUF")
                        .param("idCategory", "1")
                        .param("titre", "Updated Book")
                        .param("auteur", "Updated Author")
                        .param("editeur", "Updated Publisher")
                        .param("estDisponible", "true")
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isNotFound());

        verify(bookServices, times(1)).updateBook(eq(999L), any(EtatLivre.class), eq(1L), any(BookUpDateDTO.class), any());
    }

    @Test
    void deleteBook_WithExistingId_ShouldReturnNoContent() throws Exception {
        // Arrange
        doNothing().when(bookServices).deleteBook(1L);

        // Act & Assert
        mockMvc.perform(delete("/books/{id}", 1L)
                        .param("id", "1"))
                .andExpect(status().isNoContent());

        verify(bookServices, times(1)).deleteBook(1L);
    }


    @Test
    void createBook_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Arrange
        when(bookServices.createBook(eq(1L), any(BookRequestDTO.class), any()))
                .thenThrow(new IllegalArgumentException("Invalid data"));

        // Act & Assert
        mockMvc.perform(multipart("/books/create")
                        .file(mockImage)
                        .param("idCategory", "1")
                        .param("titre", "") // Titre vide
                        .param("auteur", "Test Author")
                        .param("editeur", "Test Publisher"))
                .andExpect(status().isBadRequest());

        verify(bookServices, times(1)).createBook(eq(1L), any(BookRequestDTO.class), any());
    }
}