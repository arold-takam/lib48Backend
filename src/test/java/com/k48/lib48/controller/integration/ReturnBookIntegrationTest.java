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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.UUID;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test") // Utilise H2 défini dans application-test.properties
public class ReturnBookIntegrationTest {
	
	// NOTE : J'ai supprimé @Container et @DynamicPropertySource (plus besoin de Docker/Postgres)
	
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
		// ... (Ton code setUp reste identique)
		Category cat = new Category();
		cat.setNom("Informatique");
		categoryRepository.save(cat);
		
		CarteAbonnement carte = new CarteAbonnement();
		carte.setCardNumber(System.currentTimeMillis());
		carte.setDuree(12);
		carteAbonnementRepository.save(carte);
		
		gerant = new User();
		gerant.setName("AdminTest");
		gerant.setMail("admin@test.com");
		gerant.setRoleName(Role.GERANT);
		gerant.setPassword("password");
		userRepository.save(gerant);
		
		User abonne = new User();
		abonne.setName("Lecteur");
		abonne.setMail("lecteur@test.com");
		abonne.setRoleName(Role.ABONNE);
		abonne.setCarteAbonnement(carte);
		userRepository.save(abonne);
		
		Book book = new Book();
		book.setTitre("Livre Test " + UUID.randomUUID());
		book.setEstDisponible(false);
		book.setCategory(cat);
		book.setEtatLivre(EtatLivre.BON_ETAT);
		bookRepository.save(book);
		
		borrow = new BorrowBook();
		borrow.setBook(book);
		borrow.setGerant(gerant);
		borrow.setAbonne(abonne);
		borrow.setStatus(BorrowStatus.EN_COURS);
		borrow.setDateEmprunt(LocalDate.now().minusDays(10));
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