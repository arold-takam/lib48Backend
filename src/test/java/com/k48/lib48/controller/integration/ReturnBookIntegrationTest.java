package com.k48.lib48.controller.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.k48.lib48.dto.ReturnRequestDTO;
import com.k48.lib48.models.*;
import com.k48.lib48.myEnum.*;
import com.k48.lib48.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.time.LocalDate;
import java.util.UUID;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.testcontainers.containers.PostgreSQLContainer;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Testcontainers
public class ReturnBookIntegrationTest {
	
	@Container // Démarre un vrai Postgres
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");
	
	@DynamicPropertySource // Lie l'app au Postgres dynamique
	static void configureProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", postgres::getJdbcUrl);
		registry.add("spring.datasource.username", postgres::getUsername);
		registry.add("spring.datasource.password", postgres::getPassword);
	}
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BookRepository bookRepository;
	
	@Autowired
	private BorrowBookRepository borrowRepository;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Autowired
	private CarteAbonnementRepository carteAbonnementRepository;
	
	private User gerant;
	private BorrowBook borrow;
	
	@BeforeEach
	void setUp() {
		// 1. Catégorie
		Category cat = new Category();
		cat.setNom("Informatique");
		categoryRepository.save(cat);
		
		// 2. Carte d'Abonnement (Correction du type Long)
		CarteAbonnement carte = new CarteAbonnement();
		carte.setCardNumber(System.currentTimeMillis());
		carte.setDuree(12);
		carteAbonnementRepository.save(carte);
		
		// 3. Gérant
		gerant = new User();
		gerant.setName("AdminTest");
		gerant.setMail("admin@test.com");
		gerant.setRoleName(Role.GERANT);
		gerant.setPassword("password");
		userRepository.save(gerant);
		
		// 4. Abonné
		User abonne = new User();
		abonne.setName("Lecteur");
		abonne.setMail("lecteur@test.com");
		abonne.setRoleName(Role.ABONNE);
		abonne.setCarteAbonnement(carte);
		userRepository.save(abonne);
		
		// 5. Livre (Fix NOT NULL etat_livre)
		Book book = new Book();
		book.setTitre("Livre Test " + UUID.randomUUID());
		book.setEstDisponible(false);
		book.setCategory(cat);
		book.setEtatLivre(EtatLivre.BON_ETAT);
		bookRepository.save(book);
		
		// 6. Emprunt (Fix dates)
		borrow = new BorrowBook();
		borrow.setBook(book);
		borrow.setGerant(gerant);
		borrow.setAbonne(abonne);
		borrow.setStatus(BorrowStatus.EN_COURS);
		borrow.setDateEmprunt(LocalDate.now().minusDays(10));
		// Utilise le bon setter pour la date de retour prévue si présent
		borrowRepository.save(borrow);
	}
	
	@Test
	@WithMockUser(username = "admin@test.com", roles = "GERANT")
	void testMakeReturnSuccess() throws Exception {
		ReturnRequestDTO returnRequestDTO = new ReturnRequestDTO(
			LocalDate.now(),
			borrow.getId()
		);
		
		mockMvc.perform(post("/returnBook/create/" + gerant.getId())
			                .param("etatLivre", "BON_ETAT")
			                .contentType(MediaType.APPLICATION_JSON)
			                .content(objectMapper.writeValueAsString(returnRequestDTO)))
			.andExpect(status().isOk());
	}
}