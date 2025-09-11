package com.k48.lib48.controller;

import com.k48.lib48.dto.ReturnRequestDTO;
import com.k48.lib48.models.*;
import com.k48.lib48.myEnum.EtatLivre;
import com.k48.lib48.myEnum.Role;
import com.k48.lib48.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ReturnBookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepositories userRepositories;

    @Autowired
    private BookRespositories bookRespositories;

    @Autowired
    private BorrowBookRepository borrowBookRepository;

    @Autowired
    private ReturnBookRepository returnBookRepository;

    @Autowired
    private CarteAbonnementRepository carteAbonnementRepository;

    @Autowired
    private CategoryRepositories categoryRepositories;

    private User gerant;
    private User abonne;
    private Book book;
    private BorrowBook borrowBook;

    @BeforeEach
    void setup() throws Exception {
        gerant = new User();
        gerant.setRoleName(Role.GERANT);
        gerant.setName("Gerant1");
        userRepositories.save(gerant);

        abonne = new User();
        abonne.setRoleName(Role.ABONNE);
        abonne.setName("Abonne1");
        CarteAbonnement carte = new CarteAbonnement();
        carte.setAvailable(true);
        carte.setDuree(10);
        carteAbonnementRepository.save(carte);
        abonne.setCarteAbonnement(carte);
        userRepositories.save(abonne);

        //Créer une categorie
        Category cat = new Category();
        cat.setNom("Cat1");
        cat.setDescription("Cat1");
        categoryRepositories.save(cat);

        book = new Book();
        book.setTitre("Livre Test");
        book.setAuteur("Victor Hugo");
        book.setEditeur("Penguin");
        book.setEtatLivre(EtatLivre.BON_ETAT);
        book.setCategory(cat);
        book.setEstDisponible(true);
        bookRespositories.save(book);

        borrowBook = new BorrowBook();
        borrowBook.setAbonne(abonne);
        borrowBook.setBook(book);
        borrowBook.setDateEmprunt(LocalDate.now());
        borrowBook.setDelaiEmprunt(7);
        borrowBookRepository.save(borrowBook);
    }

    @Test
    void makeReturn_success() throws Exception {
        String requestBody = """
            {
                "borrowBookID": %d,
                "dateRetour": "%s"
            }
            """.formatted(borrowBook.getId(), LocalDate.now());

        mockMvc.perform(post("/returnBook/create/" + gerant.getId())
                        .param("etatLivre", "BON_ETAT")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        List<ReturnBook> returns = returnBookRepository.findAll();
        assertEquals(9, returns.size());
    }

    @Test
    void getReturnByID_success() throws Exception {
        ReturnBook returnBook = new ReturnBook();
        returnBook.setBorrowBookConcerned(borrowBook);
        returnBook.setGerantReturningID(gerant.getId());
        returnBook.setNouvelEtatLivre(EtatLivre.BON_ETAT);
        returnBook.setDateRetour(LocalDate.now());
        returnBookRepository.save(returnBook);

        mockMvc.perform(get("/returnBook/get/" + gerant.getId())
                        .param("returnID", String.valueOf(returnBook.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.livreName").value(book.getTitre()))
                .andExpect(jsonPath("$.abonneName").value(abonne.getName()))
                .andExpect(jsonPath("$.nouvelEtatLivre").value("BON_ETAT"));
    }
}
