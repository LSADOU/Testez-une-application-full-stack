package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.mapper.UserMapper;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("UserController - Tests d'intégration")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserMapper userMapper;

    private User user;
    private UserDto userDto;

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

        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setEmail("test@example.com");
        userDto.setFirstName("Loic");
        userDto.setLastName("Sadou");
        userDto.setAdmin(false);
    }


    @Test
    @DisplayName("GET /api/user/{id} - Devrait retourner un utilisateur par son ID")
    @WithMockUser
    void testFindById_Success() throws Exception {
        when(userService.findById(1L)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);

        mockMvc.perform(get("/api/user/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.firstName").value("Loic"))
                .andExpect(jsonPath("$.lastName").value("Sadou"))
                .andExpect(jsonPath("$.admin").value(false));

        verify(userService, times(1)).findById(1L);
        verify(userMapper, times(1)).toDto(user);
    }

    @Test
    @DisplayName("GET /api/user/{id} - Devrait retourner 404 si utilisateur non trouvé")
    @WithMockUser
    void testFindById_NotFound() throws Exception {
        when(userService.findById(999L)).thenReturn(null);

        mockMvc.perform(get("/api/user/999"))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).findById(999L);
        verify(userMapper, never()).toDto(any(User.class));
    }

    @Test
    @DisplayName("GET /api/user/{id} - Devrait retourner 400 pour un ID invalide")
    @WithMockUser
    void testFindById_InvalidId() throws Exception {
        mockMvc.perform(get("/api/user/invalid"))
                .andExpect(status().isBadRequest());

        verify(userService, never()).findById(anyLong());
        verify(userMapper, never()).toDto(any(User.class));
    }

    @Test
    @DisplayName("GET /api/user/{id} - Devrait retourner un utilisateur admin")
    @WithMockUser
    void testFindById_AdminUser() throws Exception {
        User adminUser = User.builder()
                .id(2L)
                .email("admin@example.com")
                .firstName("Admin")
                .lastName("User")
                .password("adminpass")
                .admin(true)
                .build();

        UserDto adminUserDto = new UserDto();
        adminUserDto.setId(2L);
        adminUserDto.setEmail("admin@example.com");
        adminUserDto.setAdmin(true);

        when(userService.findById(2L)).thenReturn(adminUser);
        when(userMapper.toDto(adminUser)).thenReturn(adminUserDto);

        mockMvc.perform(get("/api/user/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.admin").value(true));

        verify(userService, times(1)).findById(2L);
    }


    @Test
    @DisplayName("DELETE /api/user/{id} - Devrait supprimer l'utilisateur si c'est le sien")
    @WithMockUser(username = "test@example.com")
    void testDelete_Success() throws Exception {
        when(userService.findById(1L)).thenReturn(user);
        doNothing().when(userService).delete(1L);

        mockMvc.perform(delete("/api/user/1"))
                .andExpect(status().isOk());

        verify(userService, times(1)).findById(1L);
        verify(userService, times(1)).delete(1L);
    }

    @Test
    @DisplayName("DELETE /api/user/{id} - Devrait retourner 404 si utilisateur non trouvé")
    @WithMockUser(username = "test@example.com")
    void testDelete_NotFound() throws Exception {
        when(userService.findById(999L)).thenReturn(null);

        mockMvc.perform(delete("/api/user/999"))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).findById(999L);
        verify(userService, never()).delete(anyLong());
    }

    @Test
    @DisplayName("DELETE /api/user/{id} - Devrait retourner 401 si utilisateur tente de supprimer un autre compte")
    @WithMockUser(username = "other@example.com")
    void testDelete_Unauthorized() throws Exception {
        when(userService.findById(1L)).thenReturn(user);

        mockMvc.perform(delete("/api/user/1"))
                .andExpect(status().isUnauthorized());

        verify(userService, times(1)).findById(1L);
        verify(userService, never()).delete(anyLong());
    }

    @Test
    @DisplayName("DELETE /api/user/{id} - Devrait retourner 400 pour un ID invalide")
    @WithMockUser
    void testDelete_InvalidId() throws Exception {
        mockMvc.perform(delete("/api/user/invalid"))
                .andExpect(status().isBadRequest());

        verify(userService, never()).findById(anyLong());
        verify(userService, never()).delete(anyLong());
    }

    @Test
    @DisplayName("DELETE /api/user/{id} - Devrait permettre la suppression avec des IDs numériques valides")
    @WithMockUser(username = "user123@example.com")
    void testDelete_ValidNumericId() throws Exception {
        User user123 = User.builder()
                .id(123L)
                .email("user123@example.com")
                .firstName("User")
                .lastName("123")
                .password("password")
                .admin(false)
                .build();

        when(userService.findById(123L)).thenReturn(user123);
        doNothing().when(userService).delete(123L);

        mockMvc.perform(delete("/api/user/123"))
                .andExpect(status().isOk());

        verify(userService, times(1)).findById(123L);
        verify(userService, times(1)).delete(123L);
    }
}
