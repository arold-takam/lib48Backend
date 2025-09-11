package com.k48.lib48.controller;

// Importations pour les nouvelles annotations Spring Boot 3.4+
import com.k48.lib48.controllers.UserController;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

// Autres importations nécessaires
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.k48.lib48.dto.UserRequestDTO;
import com.k48.lib48.models.CarteAbonnement;
import com.k48.lib48.models.User;
import com.k48.lib48.myEnum.Role;
import com.k48.lib48.myEnum.TypeAbonnement;
import com.k48.lib48.service.CarteAbonnementService;
import com.k48.lib48.service.UserServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Arrays;
import java.util.List;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // Nouvelles annotations Spring Boot 3.4+ au lieu de @MockBean
    @MockitoBean
    private UserServices userServices;

    @MockitoBean
    private CarteAbonnementService carteAbonnementService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserRequestDTO userRequestDTO;
    private User user;
    private CarteAbonnement carteAbonnement;

    @BeforeEach
    void setUp() {
        userRequestDTO = new UserRequestDTO("John Doe", "john@email.com", "password123");

        user = new User();
        user.setName("John Doe");
        user.setMail("john@email.com");
        user.setPassword("password123");
        user.setRoleName(Role.ABONNE);

        carteAbonnement = new CarteAbonnement();
        carteAbonnement.setTypeAbonnement(TypeAbonnement.STANDART);
    }

    // TESTS POUR LA GESTION DES UTILISATEURS

    @Test
    void createUser_WithValidData_ShouldReturnCreated() throws Exception {
        // Arrange
        doNothing().when(userServices).createUser(any(UserRequestDTO.class), any(Role.class));

        // Act & Assert
        mockMvc.perform(post("/user/create")
                        .param("roleName", "ABONNE")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDTO)))
                .andExpect(status().isCreated());

        verify(userServices, times(1)).createUser(any(UserRequestDTO.class), eq(Role.ABONNE));
    }

    @Test
    void createUser_WithIllegalArgument_ShouldReturnUnauthorized() throws Exception {
        // Arrange
        doThrow(new IllegalArgumentException("Error")).when(userServices)
                .createUser(any(UserRequestDTO.class), any(Role.class));

        // Act & Assert
        mockMvc.perform(post("/user/create")
                        .param("roleName", "ABONNE")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDTO)))
                .andExpect(status().isUnauthorized());

        verify(userServices, times(1)).createUser(any(UserRequestDTO.class), any(Role.class));
    }

    @Test
    void getUserID_WithExistingId_ShouldReturnUser() throws Exception {
        // Arrange
        when(userServices.getUserID(1)).thenReturn(user);

        // Act & Assert
        mockMvc.perform(get("/user/get/{userID}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.mail").value("john@email.com"));

        verify(userServices, times(1)).getUserID(1);
    }

    @Test
    void getUserID_WithNonExistingId_ShouldReturnNotFound() throws Exception {
        // Arrange
        when(userServices.getUserID(999)).thenThrow(new IllegalArgumentException("Not found"));

        // Act & Assert
        mockMvc.perform(get("/user/get/{userID}", 999))
                .andExpect(status().isNotFound());

        verify(userServices, times(1)).getUserID(999);
    }

    @Test
    void getUserRoleAndName_WithExistingUser_ShouldReturnUser() throws Exception {
        // Arrange
        when(userServices.getUserRoleAndName("John Doe", Role.ABONNE)).thenReturn(user);

        // Act & Assert
        mockMvc.perform(get("/user/get/byRole/{role}", "ABONNE")
                        .param("name", "John Doe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"));

        verify(userServices, times(1)).getUserRoleAndName("John Doe", Role.ABONNE);
    }

    @Test
    void getUserRoleAndName_WithNonExistingUser_ShouldReturnNotFound() throws Exception {
        // Arrange
        when(userServices.getUserRoleAndName("Unknown", Role.ABONNE))
                .thenThrow(new IllegalArgumentException("Not found"));

        // Act & Assert
        mockMvc.perform(get("/user/get/byRole/{role}", "ABONNE")
                        .param("name", "Unknown"))
                .andExpect(status().isNotFound());

        verify(userServices, times(1)).getUserRoleAndName("Unknown", Role.ABONNE);
    }

    @Test
    void getAllUsers_ShouldReturnUserList() throws Exception {
        // Arrange
        List<User> userList = Arrays.asList(user, new User());
        when(userServices.getAllUsers()).thenReturn(userList);

        // Act & Assert
        mockMvc.perform(get("/user/get"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(userServices, times(1)).getAllUsers();
    }

    @Test
    void getAllUsersRole_WithRole_ShouldReturnFilteredList() throws Exception {
        // Arrange
        List<User> userList = Arrays.asList(user);
        when(userServices.getAllUsersRole(Role.ABONNE)).thenReturn(userList);

        // Act & Assert
        mockMvc.perform(get("/user/get/all/{role}", "ABONNE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(userServices, times(1)).getAllUsersRole(Role.ABONNE);
    }

    @Test
    void updateUser_WithValidData_ShouldReturnOk() throws Exception {
        // Arrange
        doNothing().when(userServices).updateUser(eq(1), any(Role.class), any(UserRequestDTO.class));

        // Act & Assert
        mockMvc.perform(put("/user/update/{userID}", 1)
                        .param("roleName", "GERANT")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDTO)))
                .andExpect(status().isOk());

        verify(userServices, times(1)).updateUser(eq(1), eq(Role.GERANT), any(UserRequestDTO.class));
    }

    @Test
    void updateUser_WithNonExistingId_ShouldReturnNotFound() throws Exception {
        // Arrange
        doThrow(new IllegalArgumentException("Not found")).when(userServices)
                .updateUser(eq(999), any(Role.class), any(UserRequestDTO.class));

        // Act & Assert
        mockMvc.perform(put("/user/update/{userID}", 999)
                        .param("roleName", "GERANT")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDTO)))
                .andExpect(status().isNotFound());

        verify(userServices, times(1)).updateUser(eq(999), any(Role.class), any(UserRequestDTO.class));
    }

    @Test
    void deleteUser_WithExistingId_ShouldReturnNoContent() throws Exception {
        // Arrange
        when(userServices.deleteUser(1)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(delete("/user/delete/{userID}", 1))
                .andExpect(status().isNoContent());

        verify(userServices, times(1)).deleteUser(1);
    }

    @Test
    void deleteUser_WithNonExistingId_ShouldReturnNotFound() throws Exception {
        // Arrange
        when(userServices.deleteUser(999)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(delete("/user/delete/{userID}", 999))
                .andExpect(status().isNotFound());

        verify(userServices, times(1)).deleteUser(999);
    }

    @Test
    void deleteUser_WithException_ShouldReturnBadRequest() throws Exception {
        // Arrange
        when(userServices.deleteUser(1)).thenThrow(new RuntimeException("Error"));

        // Act & Assert
        mockMvc.perform(delete("/user/delete/{userID}", 1))
                .andExpect(status().isBadRequest());

        verify(userServices, times(1)).deleteUser(1);
    }

    // TESTS POUR LA GESTION DES CARTES D'ABONNEMENT

    @Test
    void createCard_WithValidData_ShouldReturnOk() throws Exception {
        // Arrange
        doNothing().when(carteAbonnementService).createCarte(1, TypeAbonnement.STANDART);

        // Act & Assert
        mockMvc.perform(post("/user/create/card/{abonneID}", 1)
                        .param("typeAbonnement", "STANDART"))
                .andExpect(status().isOk());

        verify(carteAbonnementService, times(1)).createCarte(1, TypeAbonnement.STANDART);
    }

    @Test
    void createCard_WithNonExistingAbonne_ShouldReturnNotFound() throws Exception {
        // Arrange
        doThrow(new IllegalArgumentException("Abonne not found"))
                .when(carteAbonnementService).createCarte(999, TypeAbonnement.STANDART);

        // Act & Assert
        mockMvc.perform(post("/user/create/card/{abonneID}", 999)
                        .param("typeAbonnement", "STANDART"))
                .andExpect(status().isNotFound());

        verify(carteAbonnementService, times(1)).createCarte(999, TypeAbonnement.STANDART);
    }

    @Test
    void getCardID_WithExistingAbonne_ShouldReturnCard() throws Exception {
        // Arrange
        when(carteAbonnementService.getCardID(1)).thenReturn(carteAbonnement);

        // Act & Assert
        mockMvc.perform(get("/user/get/card/{abonneID}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.typeAbonnement").value("STANDART"));

        verify(carteAbonnementService, times(1)).getCardID(1);
    }

    @Test
    void subscribe_WithValidData_ShouldReturnOk() throws Exception {
        // Arrange
        doNothing().when(carteAbonnementService).subscribe(1, TypeAbonnement.VIP);

        // Act & Assert
        mockMvc.perform(put("/user/subscribe/card/byAbonne/{abonneID}", 1)
                        .param("typeAbonnement", "VIP"))
                .andExpect(status().isOk());

        verify(carteAbonnementService, times(1)).subscribe(1, TypeAbonnement.VIP);
    }

    @Test
    void deleteCard_WithExistingAbonne_ShouldReturnNoContent() throws Exception {
        // Arrange
        when(carteAbonnementService.deleteCard(1)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(delete("/user/delete/card/{abonneID}", 1))
                .andExpect(status().isNoContent());

        verify(carteAbonnementService, times(1)).deleteCard(1);
    }

    @Test
    void revoqueCard_WithValidData_ShouldReturnOk() throws Exception {
        // Arrange
        doNothing().when(carteAbonnementService).revoqueCard(1, 2);

        // Act & Assert
        mockMvc.perform(put("/user/revoque/card/{abonneID}", 1)
                        .param("gerantID", "2"))
                .andExpect(status().isOk());

        verify(carteAbonnementService, times(1)).revoqueCard(1, 2);
    }

    @Test
    void getAllCards_WithExistingGerant_ShouldReturnCardList() throws Exception {
        // Arrange
        List<CarteAbonnement> cards = Arrays.asList(carteAbonnement);
        when(carteAbonnementService.getAllCards(1)).thenReturn(cards);

        // Act & Assert
        mockMvc.perform(get("/user/get/card/byGerant/{gerantID}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(carteAbonnementService, times(1)).getAllCards(1);
    }

    // TESTS DE VALIDATION


    @Test
    void createUser_WithMissingRole_ShouldReturnBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDTO)))
                .andExpect(status().isBadRequest());

        verify(userServices, never()).createUser(any(), any());
    }
}