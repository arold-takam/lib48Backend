package com.k48.lib48.service;

import com.k48.lib48.dto.HistoryRequestDTO;
import com.k48.lib48.dto.UserRequestDTO;
import com.k48.lib48.models.User;
import com.k48.lib48.myEnum.Role;
import com.k48.lib48.repository.UserRepositories;
import org.junit.BeforeClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepositories userRepositories;

    @Mock
    private HistoryService historyService;

    @InjectMocks
    private UserServices userServices;

    private User user;
    private UserRequestDTO userRequestDTO;

    //@BeforeEach s'execute avant chaque test
    @BeforeEach
     void setUp() {
        userRequestDTO = new UserRequestDTO("John Doe", "john@email.com", "password123");
        user = new User();
        user.setName("John Doe");
        user.setPassword("password123");
        user.setRoleName(Role.GERANT);
        user.setMail("john@email.com");
    }

    @Test
    void createUserWithValidDataShouldReturnTrue() {
        //Act: Appel de la méthode à tester
        userServices.createUser(userRequestDTO,Role.GERANT);

        //Assert:vérifications
        //verify : vérifie que userRepositories.save a été appelé une fois
        verify(userRepositories, times(1)).save(any(User.class));

        //verify : vérifie que historyService.addHistory a été appelé une fois
        verify(historyService,times(1)).addToHistory(any(HistoryRequestDTO.class));
    }


    @Test
    void createUserWithNullRequestShouldThrowException() {
        // Act & Assert: vérifie qu'une exception est levée
        assertThrows(IllegalArgumentException.class, () -> userServices.createUser(null, Role.ABONNE));

        //Vérifier que save n'a jamais été appelé
        verify(userRepositories,never()).save(any(User.class));
        verify(historyService,never()).addToHistory(any(HistoryRequestDTO.class));
    }

    @Test
    void getUserID_WithExistingId_ShouldReturnUser() {
        // Arrange: configuration du mock
        when(userRepositories.findById(1)).thenReturn(Optional.of(user));

        // Act: appel de la méthode
        User result = userServices.getUserID(1);

        // Assert: vérifications
        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        // verify vérifie que findById a été appelé avec l'argument 1
        verify(userRepositories, times(1)).findById(1);
    }

    @Test
    void getUserID_WithNonExistingId_ShouldThrowException() {
        // Arrange: le mock retourne un Optional vide
        when(userRepositories.findById(999)).thenReturn(Optional.empty());

        // Act & Assert: vérifie qu'une exception est levée
        assertThrows(IllegalArgumentException.class, () -> {
            userServices.getUserID(999);
        });

        verify(userRepositories, times(1)).findById(999);
    }

    @Test
    void getUserRoleAndName_WithExistingUser_ShouldReturnUser() {
        // Arrange
        when(userRepositories.findByNameIgnoreCaseAndRoleName("John Doe", Role.ABONNE))
                .thenReturn(user);

        // Act
        User result = userServices.getUserRoleAndName("John Doe", Role.ABONNE);

        // Assert
        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        verify(userRepositories, times(1))
                .findByNameIgnoreCaseAndRoleName("John Doe", Role.ABONNE);
    }

    @Test
    void getUserRoleAndName_WithNonExistingUser_ShouldThrowException() {
        // Arrange
        when(userRepositories.findByNameIgnoreCaseAndRoleName("Unknown", Role.ABONNE))
                .thenReturn(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            userServices.getUserRoleAndName("Unknown", Role.ABONNE);
        });

        verify(userRepositories, times(1))
                .findByNameIgnoreCaseAndRoleName("Unknown", Role.ABONNE);
    }

    @Test
    void getAllUsers_ShouldReturnUserList() {
        // Arrange
        List<User> userList = Arrays.asList(user, new User());
        when(userRepositories.findAll()).thenReturn(userList);

        // Act
        List<User> result = userServices.getAllUsers();

        // Assert
        assertEquals(2, result.size());
        verify(userRepositories, times(1)).findAll();
    }


    @Test
    void updateUser_WithExistingId_ShouldUpdateUser() {
        // Arrange
        when(userRepositories.findById(1)).thenReturn(Optional.of(user));
        UserRequestDTO updateDTO = new UserRequestDTO("Jane Doe", "jane@email.com", "newpassword");

        // Act
        userServices.updateUser(1, Role.GERANT, updateDTO);

        // Assert
        verify(userRepositories, times(1)).findById(1);
        verify(userRepositories, times(1)).save(any(User.class));
        verify(historyService, times(1)).addToHistory(any(HistoryRequestDTO.class));
    }

    @Test
    void updateUser_WithNonExistingId_ShouldThrowException() {
        // Arrange
        when(userRepositories.findById(999)).thenReturn(Optional.empty());
        UserRequestDTO updateDTO = new UserRequestDTO("Jane Doe", "jane@email.com", "newpassword");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            userServices.updateUser(999, Role.GERANT, updateDTO);
        });

        verify(userRepositories, times(1)).findById(999);
        verify(userRepositories, never()).save(any(User.class));
        verify(historyService, never()).addToHistory(any(HistoryRequestDTO.class));
    }


    @Test
    void deleteUser_WithNonExistingId_ShouldThrowException() {
        // Arrange
        when(userRepositories.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            userServices.deleteUser(999);
        });

        verify(userRepositories, times(1)).findById(999);
        verify(userRepositories, never()).deleteById(anyInt());
        verify(historyService, never()).addToHistory(any(HistoryRequestDTO.class));
    }
}



