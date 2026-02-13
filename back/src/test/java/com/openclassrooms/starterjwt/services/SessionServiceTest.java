package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SessionService - Tests unitaires")
class SessionServiceTest {

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SessionService sessionService;

    private Session session;
    private User user;

    @BeforeEach
    void setUp() {
        session = new Session();
        session.setId(1L);
        session.setName("Yoga Session");
        session.setUsers(new ArrayList<>());

        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setFirstName("Loic");
        user.setLastName("Sadou");
    }


    @Test
    @DisplayName("create() - Devrait créer une session avec succès")
    void testCreate_Success() {
        when(sessionRepository.save(session)).thenReturn(session);

        Session result = sessionService.create(session);

        assertNotNull(result);
        assertEquals(session.getId(), result.getId());
        assertEquals(session.getName(), result.getName());
        verify(sessionRepository, times(1)).save(session);
    }


    @Test
    @DisplayName("delete() - Devrait supprimer une session par son ID")
    void testDelete_Success() {
        Long sessionId = 1L;
        doNothing().when(sessionRepository).deleteById(sessionId);

        sessionService.delete(sessionId);

        verify(sessionRepository, times(1)).deleteById(sessionId);
    }


    @Test
    @DisplayName("findAll() - Devrait retourner toutes les sessions")
    void testFindAll_Success() {
        Session session2 = new Session();
        session2.setId(2L);
        session2.setName("Pilates Session");

        List<Session> sessions = Arrays.asList(session, session2);
        when(sessionRepository.findAll()).thenReturn(sessions);

        List<Session> result = sessionService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Yoga Session", result.get(0).getName());
        assertEquals("Pilates Session", result.get(1).getName());
        verify(sessionRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findAll() - Devrait retourner une liste vide si aucune session")
    void testFindAll_EmptyList() {
        when(sessionRepository.findAll()).thenReturn(new ArrayList<>());

        List<Session> result = sessionService.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(sessionRepository, times(1)).findAll();
    }


    @Test
    @DisplayName("getById() - Devrait retourner une session par son ID")
    void testGetById_Success() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

        Session result = sessionService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Yoga Session", result.getName());
        verify(sessionRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("getById() - Devrait retourner null si session non trouvée")
    void testGetById_NotFound() {
        when(sessionRepository.findById(999L)).thenReturn(Optional.empty());

        Session result = sessionService.getById(999L);

        assertNull(result);
        verify(sessionRepository, times(1)).findById(999L);
    }


    @Test
    @DisplayName("update() - Devrait mettre à jour une session")
    void testUpdate_Success() {
        Session updatedSession = new Session();
        updatedSession.setName("Updated Yoga Session");

        when(sessionRepository.save(any(Session.class))).thenAnswer(invocation -> {
            Session s = invocation.getArgument(0);
            s.setId(1L);
            return s;
        });

        Session result = sessionService.update(1L, updatedSession);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(sessionRepository, times(1)).save(updatedSession);
    }


    @Test
    @DisplayName("participate() - Devrait permettre la participation d'un utilisateur")
    void testParticipate_Success() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(sessionRepository.save(session)).thenReturn(session);

        sessionService.participate(1L, 1L);

        assertTrue(session.getUsers().contains(user));
        assertEquals(1, session.getUsers().size());
        verify(sessionRepository).findById(1L);
        verify(userRepository).findById(1L);
        verify(sessionRepository).save(session);
    }

    @Test
    @DisplayName("participate() - Devrait lever NotFoundException si session non trouvée")
    void testParticipate_SessionNotFound() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(NotFoundException.class, () -> {
            sessionService.participate(1L, 1L);
        });

        verify(sessionRepository).findById(1L);
        verify(userRepository).findById(1L);
        verify(sessionRepository, never()).save(any());
    }

    @Test
    @DisplayName("participate() - Devrait lever NotFoundException si utilisateur non trouvé")
    void testParticipate_UserNotFound() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            sessionService.participate(1L, 1L);
        });

        verify(sessionRepository).findById(1L);
        verify(userRepository).findById(1L);
        verify(sessionRepository, never()).save(any());
    }

    @Test
    @DisplayName("participate() - Devrait lever BadRequestException si utilisateur déjà participant")
    void testParticipate_AlreadyParticipating() {
        session.getUsers().add(user); // Utilisateur déjà dans la liste
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class, () -> {
            sessionService.participate(1L, 1L);
        });

        verify(sessionRepository).findById(1L);
        verify(userRepository).findById(1L);
        verify(sessionRepository, never()).save(any());
    }


    @Test
    @DisplayName("noLongerParticipate() - Devrait retirer un utilisateur de la session")
    void testNoLongerParticipate_Success() {
        session.getUsers().add(user); // Utilisateur participant
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(sessionRepository.save(session)).thenReturn(session);

        sessionService.noLongerParticipate(1L, 1L);

        assertFalse(session.getUsers().contains(user));
        assertEquals(0, session.getUsers().size());
        verify(sessionRepository).findById(1L);
        verify(sessionRepository).save(session);
    }

    @Test
    @DisplayName("noLongerParticipate() - Devrait lever NotFoundException si session non trouvée")
    void testNoLongerParticipate_SessionNotFound() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            sessionService.noLongerParticipate(1L, 1L);
        });

        verify(sessionRepository).findById(1L);
        verify(sessionRepository, never()).save(any());
    }

    @Test
    @DisplayName("noLongerParticipate() - Devrait lever BadRequestException si utilisateur ne participe pas")
    void testNoLongerParticipate_UserNotParticipating() {
        // session.getUsers() est vide - l'utilisateur ne participe pas
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

        assertThrows(BadRequestException.class, () -> {
            sessionService.noLongerParticipate(1L, 1L);
        });

        verify(sessionRepository).findById(1L);
        verify(sessionRepository, never()).save(any());
    }

    @Test
    @DisplayName("noLongerParticipate() - Devrait retirer uniquement l'utilisateur spécifié")
    void testNoLongerParticipate_RemovesOnlySpecifiedUser() {
        User user2 = new User();
        user2.setId(2L);
        user2.setEmail("user2@example.com");

        session.getUsers().add(user);
        session.getUsers().add(user2);

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(sessionRepository.save(session)).thenReturn(session);

        sessionService.noLongerParticipate(1L, 1L);

        assertEquals(1, session.getUsers().size());
        assertFalse(session.getUsers().contains(user));
        assertTrue(session.getUsers().contains(user2));
        verify(sessionRepository).save(session);
    }
}
