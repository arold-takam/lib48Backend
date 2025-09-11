
package com.k48.lib48.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.k48.lib48.dto.BorrowRequestDTO;
import com.k48.lib48.dto.BorrowResponseDTO;
import com.k48.lib48.models.*;
import com.k48.lib48.myEnum.EtatLivre;
import com.k48.lib48.myEnum.Role;
import com.k48.lib48.repository.BookRespositories;
import com.k48.lib48.repository.BorrowBookRepository;
import com.k48.lib48.repository.CategoryRepositories;
import com.k48.lib48.repository.UserRepositories;
import com.k48.lib48.service.BorrowBookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureTestDatabase // Utilise une base en mémoire H2
class BorrowBookControllerIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepositories userRepo;

    @Autowired
    private BookRespositories bookRepo;

    @Autowired
    private BorrowBookRepository borrowRepo;

    @Autowired
    private BorrowBookService borrowService;

    private MockMvc mockMvc;
    @Autowired
    private CategoryRepositories categoryRepositories;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        borrowRepo.deleteAll();
        userRepo.deleteAll();
        bookRepo.deleteAll();
    }

    @Test
    void makeBorrow_success() throws Exception {
        // Créer un gérant
        User gerant = new User();
        gerant.setRoleName(Role.GERANT);
        gerant.setName("Gerant1");
        userRepo.save(gerant);

        // Créer un abonné
        User abonne = new User();
        abonne.setRoleName(Role.ABONNE);
        abonne.setName("Abonne1");
        CarteAbonnement carte = new CarteAbonnement();
        carte.setAvailable(true);
        carte.setDuree(10);
        abonne.setCarteAbonnement(carte);
        userRepo.save(abonne);

        //Créer une categorie
        Category cat = new Category();
        cat.setNom("Cat1");
        cat.setDescription("Cat1");
        categoryRepositories.save(cat);

        // Créer un livre
        Book book = new Book();
        book.setTitre("Livre Test");
        book.setAuteur("Victor Hugo");
        book.setEditeur("Penguin");
        book.setEtatLivre(EtatLivre.NEUF);
        book.setEstDisponible(true);
        book.setCategory(cat);
        bookRepo.save(book);

        BorrowRequestDTO dto = new BorrowRequestDTO(abonne.getId(), book.getId(), 7);

        mockMvc.perform(post("/borrowBook/create" + gerant.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void getBorrowById_success() throws Exception {
        // Créer gérant, abonné et livre
        User gerant = new User();
        gerant.setRoleName(Role.GERANT);
        gerant.setName("Gerant1");
        userRepo.save(gerant);

        User abonne = new User();
        abonne.setRoleName(Role.ABONNE);
        abonne.setName("Abonne1");
        CarteAbonnement carte = new CarteAbonnement();
        carte.setAvailable(true);
        carte.setDuree(10);
        abonne.setCarteAbonnement(carte);
        userRepo.save(abonne);

        //Créer une categorie
        Category cat = new Category();
        cat.setNom("Cat1");
        cat.setDescription("Cat1");
        categoryRepositories.save(cat);

        Book book = new Book();
        book.setTitre("Livre Test");
        book.setAuteur("Victor Hugo");
        book.setEditeur("Penguin");
        book.setEtatLivre(EtatLivre.NEUF);
        book.setEstDisponible(true);
        book.setCategory(cat);
        bookRepo.save(book);

        // Faire un emprunt
        BorrowRequestDTO dto = new BorrowRequestDTO(abonne.getId(), book.getId(), 7);
        borrowService.makeBorrow(gerant.getId(), dto);

        BorrowResponseDTO response = borrowService.getAllBorrowsByAbonne_Id(abonne.getId()).get(0);

        mockMvc.perform(get("/borrowBook/get/" + gerant.getId())
                        .param("borrowID", String.valueOf(response.idBorrow()))
                        .param("abonneID", String.valueOf(abonne.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookTitre").value("Livre Test"))
                .andExpect(jsonPath("$.abonneId").value(abonne.getId()))
                .andExpect(jsonPath("$.etatBorrow").value("EN_COURS"));;
    }

    @Test
    void getAllBorrows_success() throws Exception {
        User gerant = new User();
        gerant.setRoleName(Role.GERANT);
        gerant.setName("Gerant1");
        userRepo.save(gerant);

        mockMvc.perform(get("/borrowBook/get/all/" + gerant.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void getAllBorrowsByAbonne_success() throws Exception {
        User abonne = new User();
        abonne.setRoleName(Role.ABONNE);
        abonne.setName("Abonne1");
        CarteAbonnement carte = new CarteAbonnement();
        carte.setAvailable(true);
        carte.setDuree(10);
        abonne.setCarteAbonnement(carte);
        userRepo.save(abonne);

        mockMvc.perform(get("/borrowBook/get/all/byAbonneID/" + abonne.getId()))
                .andExpect(status().isOk());
    }
}
