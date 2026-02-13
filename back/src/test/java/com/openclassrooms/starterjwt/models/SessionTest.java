package com.openclassrooms.starterjwt.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Session Model - Tests unitaires")
class SessionTest {

    private Session session1;
    private Session session2;
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

        session1 = Session.builder()
                .id(1L)
                .name("Yoga Session")
                .date(new Date())
                .description("Test session")
                .teacher(teacher)
                .users(new ArrayList<>())
                .build();

        session2 = Session.builder()
                .id(2L)
                .name("Pilates Session")
                .date(new Date())
                .description("Pilates description")
                .teacher(teacher)
                .users(new ArrayList<>())
                .build();
    }


    @Test
    @DisplayName("Builder - Devrait créer une session avec toutes les propriétés")
    void testBuilder_AllProperties() {
        Date sessionDate = new Date();
        LocalDateTime now = LocalDateTime.now();
        List<User> users = Arrays.asList(user1, user2);

        Session session = Session.builder()
                .id(1L)
                .name("Complete Session")
                .date(sessionDate)
                .description("Complete description")
                .teacher(teacher)
                .users(users)
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertEquals(1L, session.getId());
        assertEquals("Complete Session", session.getName());
        assertEquals(sessionDate, session.getDate());
        assertEquals("Complete description", session.getDescription());
        assertEquals(teacher, session.getTeacher());
        assertEquals(2, session.getUsers().size());
        assertEquals(now, session.getCreatedAt());
        assertEquals(now, session.getUpdatedAt());
    }

    @Test
    @DisplayName("Builder - Devrait créer une session avec les champs minimaux")
    void testBuilder_MinimalFields() {
        Date sessionDate = new Date();

        Session session = Session.builder()
                .name("Minimal Session")
                .date(sessionDate)
                .description("Description")
                .build();

        assertNull(session.getId());
        assertEquals("Minimal Session", session.getName());
        assertNull(session.getTeacher());
        assertNull(session.getUsers());
    }


    @Test
    @DisplayName("Setters - Devrait mettre à jour toutes les propriétés")
    void testSetters_AllProperties() {
        Session session = new Session();
        Date newDate = new Date();
        LocalDateTime now = LocalDateTime.now();
        List<User> users = Arrays.asList(user1);

        session.setId(5L);
        session.setName("Updated Session");
        session.setDate(newDate);
        session.setDescription("Updated description");
        session.setTeacher(teacher);
        session.setUsers(users);
        session.setCreatedAt(now);
        session.setUpdatedAt(now);

        assertEquals(5L, session.getId());
        assertEquals("Updated Session", session.getName());
        assertEquals(newDate, session.getDate());
        assertEquals("Updated description", session.getDescription());
        assertEquals(teacher, session.getTeacher());
        assertEquals(1, session.getUsers().size());
        assertEquals(now, session.getCreatedAt());
        assertEquals(now, session.getUpdatedAt());
    }


    @Test
    @DisplayName("equals() - Deux sessions avec le même ID devraient être égales")
    void testEquals_SameId() {
        Session sessionA = Session.builder().id(1L).name("Session A").build();
        Session sessionB = Session.builder().id(1L).name("Session B").build();

        assertEquals(sessionA, sessionB);
    }

    @Test
    @DisplayName("equals() - Deux sessions avec des IDs différents ne devraient pas être égales")
    void testEquals_DifferentId() {
        assertNotEquals(session1, session2);
    }

    @Test
    @DisplayName("equals() - Une session devrait être égale à elle-même")
    void testEquals_SameObject() {
        assertEquals(session1, session1);
    }

    @Test
    @DisplayName("equals() - Une session ne devrait pas être égale à null")
    void testEquals_Null() {
        assertNotEquals(null, session1);
    }


    @Test
    @DisplayName("hashCode() - Deux sessions avec le même ID devraient avoir le même hashCode")
    void testHashCode_SameId() {
        Session sessionA = Session.builder().id(1L).name("A").build();
        Session sessionB = Session.builder().id(1L).name("B").build();

        assertEquals(sessionA.hashCode(), sessionB.hashCode());
    }

    @Test
    @DisplayName("hashCode() - Deux sessions avec des IDs différents peuvent avoir des hashCodes différents")
    void testHashCode_DifferentId() {
        assertNotEquals(session1.hashCode(), session2.hashCode());
    }


    @Test
    @DisplayName("toString() - Devrait contenir les informations principales")
    void testToString_ContainsMainInfo() {
        String result = session1.toString();

        assertNotNull(result);
        assertTrue(result.contains("Session"));
    }


    @Test
    @DisplayName("Chaînage - Devrait permettre le chaînage des setters")
    void testChaining_Setters() {
        Date sessionDate = new Date();

        Session session = new Session()
                .setName("Chained Session")
                .setDate(sessionDate)
                .setDescription("Chained description")
                .setTeacher(teacher);

        assertEquals("Chained Session", session.getName());
        assertEquals(sessionDate, session.getDate());
        assertEquals("Chained description", session.getDescription());
        assertEquals(teacher, session.getTeacher());
    }


    @Test
    @DisplayName("NoArgsConstructor - Devrait créer une session vide")
    void testNoArgsConstructor() {
        Session session = new Session();

        assertNotNull(session);
        assertNull(session.getId());
        assertNull(session.getName());
        assertNull(session.getTeacher());
    }

    @Test
    @DisplayName("AllArgsConstructor - Devrait créer une session avec tous les champs")
    void testAllArgsConstructor() {
        Date sessionDate = new Date();
        LocalDateTime now = LocalDateTime.now();
        List<User> users = Arrays.asList(user1);

        Session session = new Session(
                1L,
                "Full Session",
                sessionDate,
                "Full description",
                teacher,
                users,
                now,
                now
        );

        assertEquals(1L, session.getId());
        assertEquals("Full Session", session.getName());
        assertEquals(sessionDate, session.getDate());
        assertEquals("Full description", session.getDescription());
        assertEquals(teacher, session.getTeacher());
        assertEquals(1, session.getUsers().size());
        assertEquals(now, session.getCreatedAt());
        assertEquals(now, session.getUpdatedAt());
    }


    @Test
    @DisplayName("Devrait pouvoir ajouter des utilisateurs à une session")
    void testAddUsers() {
        session1.getUsers().add(user1);
        session1.getUsers().add(user2);

        assertEquals(2, session1.getUsers().size());
        assertTrue(session1.getUsers().contains(user1));
        assertTrue(session1.getUsers().contains(user2));
    }

    @Test
    @DisplayName("Devrait pouvoir retirer des utilisateurs d'une session")
    void testRemoveUsers() {
        session1.setUsers(new ArrayList<>(Arrays.asList(user1, user2)));

        session1.getUsers().remove(user1);

        assertEquals(1, session1.getUsers().size());
        assertFalse(session1.getUsers().contains(user1));
        assertTrue(session1.getUsers().contains(user2));
    }

    @Test
    @DisplayName("Devrait gérer une liste vide d'utilisateurs")
    void testEmptyUsersList() {
        session1.setUsers(new ArrayList<>());

        assertNotNull(session1.getUsers());
        assertTrue(session1.getUsers().isEmpty());
    }


    @Test
    @DisplayName("Devrait pouvoir changer le professeur d'une session")
    void testChangeTeacher() {
        Teacher newTeacher = Teacher.builder()
                .id(2L)
                .firstName("Jean")
                .lastName("Martin")
                .build();

        session1.setTeacher(newTeacher);

        assertEquals(newTeacher, session1.getTeacher());
        assertEquals(2L, session1.getTeacher().getId());
    }

    @Test
    @DisplayName("Devrait accepter une session sans professeur")
    void testSessionWithoutTeacher() {
        session1.setTeacher(null);

        assertNull(session1.getTeacher());
    }


    @Test
    @DisplayName("Devrait pouvoir définir une date dans le futur")
    void testFutureDate() {
        Date futureDate = new Date(System.currentTimeMillis() + 86400000); // +1 jour

        session1.setDate(futureDate);

        assertEquals(futureDate, session1.getDate());
        assertTrue(session1.getDate().after(new Date()));
    }

    @Test
    @DisplayName("Devrait pouvoir définir une date dans le passé")
    void testPastDate() {
        Date pastDate = new Date(System.currentTimeMillis() - 86400000); // -1 jour

        session1.setDate(pastDate);

        assertEquals(pastDate, session1.getDate());
        assertTrue(session1.getDate().before(new Date()));
    }
}
