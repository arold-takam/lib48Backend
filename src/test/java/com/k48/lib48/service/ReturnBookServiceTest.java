package com.k48.lib48.service;

import com.k48.lib48.dto.ReturnRequestDTO;
import com.k48.lib48.dto.ReturnResponseDTO;
import com.k48.lib48.models.*;
import com.k48.lib48.myEnum.EtatLivre;
import com.k48.lib48.myEnum.Role;
import com.k48.lib48.repository.*;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
class ReturnBookServiceTest {

    @Autowired
    private ReturnBookRepository returnBookRepository;
    @Autowired
    private BorrowBookRepository borrowBookRepository;
    @Autowired
    private UserRepositories userRepositories;
    @Autowired
    private CarteAbonnementRepository carteAbonnementRepository;
    @Autowired
    private BookRespositories bookRespositories;
    @Autowired
    private UserServices userServices;
    @Autowired
    private HistoryService historyService;

    @Autowired
    CategoryRepositories categoryRepositories;

    private ReturnBookService returnBookService;

    private User gerant;
    private User abonne;
    private Book book;
    private BorrowBook borrowBook;

    @BeforeEach
    void setup() {
        returnBookService = new ReturnBookService(returnBookRepository, borrowBookRepository, userRepositories, bookRespositories, carteAbonnementRepository, userServices, historyService);

        // Créer gérant
        gerant = new User();
        gerant.setRoleName(Role.GERANT);
        gerant.setName("Gerant1");
        userRepositories.save(gerant);

        // Créer abonné et carte
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

        // Créer livre
        book = new Book();
        book.setTitre("Livre Test");
        book.setAuteur("Victor Hugo");
        book.setEditeur("Penguin");
        book.setCategory(cat);
        book.setEtatLivre(EtatLivre.BON_ETAT);
        book.setEstDisponible(true);
        bookRespositories.save(book);

        // Créer emprunt
        borrowBook = new BorrowBook();
        borrowBook.setAbonne(abonne);
        borrowBook.setBook(book);
        borrowBook.setDateEmprunt(LocalDate.now().minusDays(5));
        borrowBook.setDelaiEmprunt(7);
        borrowBookRepository.save(borrowBook);
    }

    @Test
    void makeReturn_success() {
        ReturnRequestDTO dto = new ReturnRequestDTO(LocalDate.now(), borrowBook.getId());

        assertDoesNotThrow(() -> returnBookService.makeReturn(gerant.getId(), EtatLivre.BON_ETAT, dto));

        List<ReturnBook> returns = returnBookRepository.findAll();
        assertEquals(9, returns.size());
        assertEquals(book.getId(), returns.get(returns.size()-1).getBorrowBookConcerned().getBook().getId());
    }

    @Test
    void makeReturn_alreadyReturned_throwsException() {
        ReturnRequestDTO dto = new ReturnRequestDTO(LocalDate.now(), borrowBook.getId());
        returnBookService.makeReturn(gerant.getId(), EtatLivre.BON_ETAT, dto);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> returnBookService.makeReturn(gerant.getId(), EtatLivre.BON_ETAT, dto));
        assertEquals("Ce livre a déjà été retourné.", exception.getMessage());
    }

    @Test
    void makeReturn_retard_penaltyApplied() {
        borrowBook.setDelaiEmprunt(3);
        borrowBookRepository.save(borrowBook);

        ReturnRequestDTO dto = new ReturnRequestDTO(LocalDate.now(), borrowBook.getId());
        returnBookService.makeReturn(gerant.getId(), EtatLivre.BON_ETAT, dto);

        CarteAbonnement carte = userRepositories.findById(abonne.getId()).get().getCarteAbonnement();
        assertTrue(carte.getDuree() < 10); // pénalité appliquée
    }

    @Test
    void getReturnByID_success() {
        ReturnRequestDTO dto = new ReturnRequestDTO(LocalDate.now(), borrowBook.getId());
        returnBookService.makeReturn(gerant.getId(), EtatLivre.BON_ETAT, dto);

        List <ReturnBook> savedReturnList = returnBookRepository.findAll();
        ReturnBook savedReturn = savedReturnList.get(savedReturnList.size()-1);
        ReturnResponseDTO response = returnBookService.getReturnByID(gerant.getId(), savedReturn.getId());

        assertEquals(savedReturn.getId(), response.idReturn());
        assertEquals(book.getTitre(), response.livreName());
        assertEquals(abonne.getName(), response.abonneName());
    }

    @Test
    void getReturnByID_notFound_throwsException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> returnBookService.getReturnByID(gerant.getId(), 999));
        assertEquals("Return not found with the ID: 999", ex.getMessage());
    }

    @Test
    void getReturnByAbonneID_emptyList() {
        List<ReturnResponseDTO> returns = returnBookService.getReturnByAbonneID(gerant.getId(), abonne.getId());
        assertTrue(returns.isEmpty());
    }
}
