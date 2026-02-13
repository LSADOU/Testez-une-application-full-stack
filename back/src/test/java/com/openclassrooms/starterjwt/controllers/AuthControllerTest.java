package com.openclassrooms.starterjwt.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.payload.request.SignupRequest;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("AuthController - Tests d'intégration")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private UserRepository userRepository;

    private LoginRequest loginRequest;
    private SignupRequest signupRequest;
    private User user;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        signupRequest = new SignupRequest();
        signupRequest.setEmail("newuser@example.com");
        signupRequest.setFirstName("Loic");
        signupRequest.setLastName("Sadou");
        signupRequest.setPassword("password123");

        user = User.builder()
                .id(1L)
                .email("test@example.com")
                .firstName("Loic")
                .lastName("Sadou")
                .password("encodedPassword")
                .admin(false)
                .build();
    }

    @Test
    @DisplayName("POST /api/auth/login - Devrait authentifier un utilisateur avec succès")
    void testLogin_Success() throws Exception {
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("test@example.com")
                .firstName("Loic")
                .lastName("Sadou")
                .password("encodedPassword")
                .build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("fake-jwt-token");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").value("fake-jwt-token"))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("test@example.com"))
                .andExpect(jsonPath("$.firstName").value("Loic"))
                .andExpect(jsonPath("$.lastName").value("Sadou"))
                .andExpect(jsonPath("$.admin").value(false));

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtils, times(1)).generateJwtToken(authentication);
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    @DisplayName("POST /api/auth/login - Devrait authentifier un utilisateur admin")
    void testLogin_AdminUser() throws Exception {
        User adminUser = User.builder()
                .id(2L)
                .email("admin@example.com")
                .firstName("Admin")
                .lastName("User")
                .password("encodedPassword")
                .admin(true)
                .build();

        UserDetailsImpl adminUserDetails = UserDetailsImpl.builder()
                .id(2L)
                .username("admin@example.com")
                .firstName("Admin")
                .lastName("User")
                .password("encodedPassword")
                .build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                adminUserDetails, null, adminUserDetails.getAuthorities());

        LoginRequest adminLoginRequest = new LoginRequest();
        adminLoginRequest.setEmail("admin@example.com");
        adminLoginRequest.setPassword("adminpass");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("admin-jwt-token");
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(adminUser));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminLoginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.admin").value(true));

        verify(userRepository, times(1)).findByEmail("admin@example.com");
    }

    @Test
    @DisplayName("POST /api/auth/login - Devrait gérer le cas où l'utilisateur n'est pas trouvé après authentification")
    void testLogin_UserNotFoundAfterAuth() throws Exception {
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("test@example.com")
                .firstName("Loic")
                .lastName("Sadou")
                .password("encodedPassword")
                .build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("fake-jwt-token");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.admin").value(false));

        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    @DisplayName("POST /api/auth/register - Devrait enregistrer un nouvel utilisateur")
    void testRegister_Success() throws Exception {
        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword123");
        when(userRepository.save(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("User registered successfully!")));

        verify(userRepository, times(1)).existsByEmail("newuser@example.com");
        verify(passwordEncoder, times(1)).encode("password123");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("POST /api/auth/register - Devrait retourner une erreur si l'email existe déjà")
    void testRegister_EmailAlreadyExists() throws Exception {
        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(true);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Error: Email is already taken!")));

        verify(userRepository, times(1)).existsByEmail("newuser@example.com");
        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    @DisplayName("POST /api/auth/register - Devrait valider les champs requis")
    void testRegister_ValidationErrors() throws Exception {
        SignupRequest invalidRequest = new SignupRequest();
        invalidRequest.setEmail("");
        invalidRequest.setFirstName("");
        invalidRequest.setLastName("");
        invalidRequest.setPassword("123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("POST /api/auth/register - Devrait valider le format de l'email")
    void testRegister_InvalidEmailFormat() throws Exception {
        SignupRequest invalidEmailRequest = new SignupRequest();
        invalidEmailRequest.setEmail("invalid-email");
        invalidEmailRequest.setFirstName("Loic");
        invalidEmailRequest.setLastName("Sadou");
        invalidEmailRequest.setPassword("password123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidEmailRequest)))
                .andExpect(status().isBadRequest());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("POST /api/auth/register - Devrait valider la longueur du prénom")
    void testRegister_FirstNameTooShort() throws Exception {
        SignupRequest shortFirstNameRequest = new SignupRequest();
        shortFirstNameRequest.setEmail("test@example.com");
        shortFirstNameRequest.setFirstName("Jo"); // Moins de 3 caractères
        shortFirstNameRequest.setLastName("Sadou");
        shortFirstNameRequest.setPassword("password123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shortFirstNameRequest)))
                .andExpect(status().isBadRequest());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("POST /api/auth/register - Devrait valider la longueur du mot de passe")
    void testRegister_PasswordTooShort() throws Exception {
        SignupRequest shortPasswordRequest = new SignupRequest();
        shortPasswordRequest.setEmail("test@example.com");
        shortPasswordRequest.setFirstName("Loic");
        shortPasswordRequest.setLastName("Sadou");
        shortPasswordRequest.setPassword("12345"); // Moins de 6 caractères

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shortPasswordRequest)))
                .andExpect(status().isBadRequest());

        verify(userRepository, never()).save(any(User.class));
    }
}
