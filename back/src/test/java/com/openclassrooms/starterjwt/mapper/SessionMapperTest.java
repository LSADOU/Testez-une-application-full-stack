package com.openclassrooms.starterjwt.mapper;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.services.TeacherService;
import com.openclassrooms.starterjwt.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("SessionMapper - Tests unitaires")
class SessionMapperTest {

    @Autowired
    private SessionMapper sessionMapper;

    private Session session;
    private SessionDto sessionDto;
    private Teacher teacher;
    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        teacher = Teacher.builder()
                .id(1L)
                .firstName("Marie")
                .lastName("Dupont")
                .build();

        user1 = User.builder()
                .id(1L)
                .email("user1@example.com")
                .firstName("Loic")
                .lastName("Sadou")
                .password("password123")
                .admin(false)
                .build();

        user2 = User.builder()
                .id(2L)
                .email("user2@example.com")
                .firstName("Jane")
                .lastName("Smith")
                .password("password456")
                .admin(false)
                .build();

        Date sessionDate = new Date();
        LocalDateTime now = LocalDateTime.now();

        session = Session.builder()
                .id(1L)
                .name("Yoga Session")
                .date(sessionDate)
                .description("Test session description")
                .teacher(teacher)
                .users(new ArrayList<>(Arrays.asList(user1, user2)))
                .createdAt(now)
                .updatedAt(now)
                .build();

        sessionDto = new SessionDto();
        sessionDto.setId(1L);
        sessionDto.setName("Yoga Session");
        sessionDto.setDate(sessionDate);
        sessionDto.setDescription("Test session description");
        sessionDto.setTeacher_id(1L);
        sessionDto.setUsers(Arrays.asList(1L, 2L));
        sessionDto.setCreatedAt(now);
        sessionDto.setUpdatedAt(now);
    }


    @Test
    @DisplayName("toEntity() - Devrait convertir un SessionDto en Session")
    void testToEntity_Success() {
        Session result = sessionMapper.toEntity(sessionDto);

        assertNotNull(result);
        assertEquals(sessionDto.getId(), result.getId());
        assertEquals(sessionDto.getName(), result.getName());
        assertEquals(sessionDto.getDate(), result.getDate());
        assertEquals(sessionDto.getDescription(), result.getDescription());
    }

    @Test
    @DisplayName("toEntity() - Devrait retourner null pour un DTO null")
    void testToEntity_NullDto() {
        Session result = sessionMapper.toEntity((SessionDto) null);

        assertNull(result);
    }

    @Test
    @DisplayName("toEntity() - Devrait convertir une liste de DTOs en liste d'entités")
    void testToEntity_List() {
        SessionDto sessionDto2 = new SessionDto();
        sessionDto2.setId(2L);
        sessionDto2.setName("Pilates Session");
        sessionDto2.setDate(new Date());
        sessionDto2.setDescription("Pilates description");
        sessionDto2.setTeacher_id(1L);

        List<SessionDto> sessionDtos = Arrays.asList(sessionDto, sessionDto2);

        List<Session> result = sessionMapper.toEntity(sessionDtos);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Yoga Session", result.get(0).getName());
        assertEquals("Pilates Session", result.get(1).getName());
    }


    @Test
    @DisplayName("toDto() - Devrait convertir une Session en SessionDto")
    void testToDto_Success() {
        SessionDto result = sessionMapper.toDto(session);

        assertNotNull(result);
        assertEquals(session.getId(), result.getId());
        assertEquals(session.getName(), result.getName());
        assertEquals(session.getDate(), result.getDate());
        assertEquals(session.getDescription(), result.getDescription());
        assertEquals(session.getTeacher().getId(), result.getTeacher_id());
        assertEquals(session.getUsers().size(), result.getUsers().size());
        assertTrue(result.getUsers().contains(1L));
        assertTrue(result.getUsers().contains(2L));
    }

    @Test
    @DisplayName("toDto() - Devrait retourner null pour une entité null")
    void testToDto_NullEntity() {
        SessionDto result = sessionMapper.toDto((Session) null);

        assertNull(result);
    }

    @Test
    @DisplayName("toDto() - Devrait convertir une liste d'entités en liste de DTOs")
    void testToDto_List() {
        Session session2 = Session.builder()
                .id(2L)
                .name("Pilates Session")
                .date(new Date())
                .description("Pilates description")
                .teacher(teacher)
                .users(new ArrayList<>())
                .build();

        List<Session> sessions = Arrays.asList(session, session2);

        List<SessionDto> result = sessionMapper.toDto(sessions);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Yoga Session", result.get(0).getName());
        assertEquals("Pilates Session", result.get(1).getName());
    }

    @Test
    @DisplayName("toDto() - Devrait gérer une session sans utilisateurs")
    void testToDto_NoUsers() {
        Session sessionWithoutUsers = Session.builder()
                .id(3L)
                .name("Empty Session")
                .date(new Date())
                .description("Session without users")
                .teacher(teacher)
                .users(new ArrayList<>())
                .build();

        SessionDto result = sessionMapper.toDto(sessionWithoutUsers);

        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertNotNull(result.getUsers());
        assertTrue(result.getUsers().isEmpty());
    }

    @Test
    @DisplayName("toDto() - Devrait gérer une session sans professeur")
    void testToDto_NoTeacher() {
        Session sessionWithoutTeacher = Session.builder()
                .id(4L)
                .name("Session without teacher")
                .date(new Date())
                .description("Description")
                .teacher(null)
                .users(new ArrayList<>())
                .build();

        SessionDto result = sessionMapper.toDto(sessionWithoutTeacher);

        assertNotNull(result);
        assertEquals(4L, result.getId());
        assertNull(result.getTeacher_id());
    }
}
