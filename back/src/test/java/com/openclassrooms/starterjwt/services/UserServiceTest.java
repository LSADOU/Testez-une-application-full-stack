package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService - Tests unitaires")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("test@example.com")
                .firstName("Loic")
                .lastName("Sadou")
                .password("password123")
                .admin(false)
                .build();
    }

    @Test
    @DisplayName("delete() - Devrait supprimer un utilisateur par son ID")
    void testDelete_Success() {
        Long userId = 1L;
        doNothing().when(userRepository).deleteById(userId);

        userService.delete(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    @DisplayName("delete() - Devrait appeler deleteById même si l'utilisateur n'existe pas")
    void testDelete_UserNotExists() {
        Long userId = 999L;
        doNothing().when(userRepository).deleteById(userId);

        userService.delete(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    @DisplayName("findById() - Devrait retourner un utilisateur par son ID")
    void testFindById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("Loic", result.getFirstName());
        assertEquals("Sadou", result.getLastName());
        assertFalse(result.isAdmin());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("findById() - Devrait retourner null si utilisateur non trouvé")
    void testFindById_NotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        User result = userService.findById(999L);

        assertNull(result);
        verify(userRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("findById() - Devrait retourner null pour un ID null")
    void testFindById_NullId() {
        when(userRepository.findById(null)).thenReturn(Optional.empty());

        User result = userService.findById(null);

        assertNull(result);
        verify(userRepository, times(1)).findById(null);
    }

    @Test
    @DisplayName("findById() - Devrait retourner un utilisateur admin")
    void testFindById_AdminUser() {
        User adminUser = User.builder()
                .id(2L)
                .email("admin@example.com")
                .firstName("Admin")
                .lastName("User")
                .password("adminpass")
                .admin(true)
                .build();

        when(userRepository.findById(2L)).thenReturn(Optional.of(adminUser));

        User result = userService.findById(2L);

        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals("admin@example.com", result.getEmail());
        assertTrue(result.isAdmin());
        verify(userRepository, times(1)).findById(2L);
    }

    @Test
    @DisplayName("findById() - Devrait gérer plusieurs recherches successives")
    void testFindById_MultipleSearches() {
        User user2 = User.builder()
                .id(2L)
                .email("user2@example.com")
                .firstName("Jane")
                .lastName("Smith")
                .password("pass456")
                .admin(false)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        User result1 = userService.findById(1L);
        User result2 = userService.findById(2L);
        User result3 = userService.findById(999L);

        assertNotNull(result1);
        assertNotNull(result2);
        assertNull(result3);
        assertEquals("Loic", result1.getFirstName());
        assertEquals("Jane", result2.getFirstName());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(2L);
        verify(userRepository, times(1)).findById(999L);
    }
}
