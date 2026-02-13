package com.openclassrooms.starterjwt.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("User Model - Tests unitaires")
class UserTest {

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = User.builder()
                .id(1L)
                .email("test@example.com")
                .firstName("Loic")
                .lastName("Sadou")
                .password("password123")
                .admin(false)
                .build();

        user2 = User.builder()
                .id(1L)
                .email("test2@example.com")
                .firstName("Jane")
                .lastName("Smith")
                .password("password456")
                .admin(true)
                .build();
    }


    @Test
    @DisplayName("Builder - Devrait créer un utilisateur avec toutes les propriétés")
    void testBuilder_AllProperties() {
        LocalDateTime now = LocalDateTime.now();

        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .firstName("Loic")
                .lastName("Sadou")
                .password("password")
                .admin(true)
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertEquals(1L, user.getId());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("Loic", user.getFirstName());
        assertEquals("Sadou", user.getLastName());
        assertEquals("password", user.getPassword());
        assertTrue(user.isAdmin());
        assertEquals(now, user.getCreatedAt());
        assertEquals(now, user.getUpdatedAt());
    }

    @Test
    @DisplayName("Builder - Devrait créer un utilisateur non-admin par défaut")
    void testBuilder_NonAdminDefault() {
        User user = User.builder()
                .email("user@example.com")
                .firstName("Test")
                .lastName("User")
                .password("pass")
                .admin(false)
                .build();

        assertFalse(user.isAdmin());
    }


    @Test
    @DisplayName("Setters - Devrait mettre à jour toutes les propriétés")
    void testSetters_AllProperties() {
        User user = new User();
        LocalDateTime now = LocalDateTime.now();

        user.setId(5L);
        user.setEmail("updated@example.com");
        user.setFirstName("Updated");
        user.setLastName("Name");
        user.setPassword("newpassword");
        user.setAdmin(true);
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        assertEquals(5L, user.getId());
        assertEquals("updated@example.com", user.getEmail());
        assertEquals("Updated", user.getFirstName());
        assertEquals("Name", user.getLastName());
        assertEquals("newpassword", user.getPassword());
        assertTrue(user.isAdmin());
        assertEquals(now, user.getCreatedAt());
        assertEquals(now, user.getUpdatedAt());
    }


    @Test
    @DisplayName("equals() - Deux utilisateurs avec le même ID devraient être égaux")
    void testEquals_SameId() {
        User userA = User.builder().id(1L).email("a@test.com").firstName("A").lastName("Test").password("pass").admin(false).build();
        User userB = User.builder().id(1L).email("b@test.com").firstName("B").lastName("Test").password("pass").admin(false).build();

        assertEquals(userA, userB);
    }

    @Test
    @DisplayName("equals() - Deux utilisateurs avec des IDs différents ne devraient pas être égaux")
    void testEquals_DifferentId() {
        User userA = User.builder().id(1L).email("test@test.com").firstName("Test").lastName("User").password("pass").admin(false).build();
        User userB = User.builder().id(2L).email("test@test.com").firstName("Test").lastName("User").password("pass").admin(false).build();

        assertNotEquals(userA, userB);
    }

    @Test
    @DisplayName("equals() - Un utilisateur devrait être égal à lui-même")
    void testEquals_SameObject() {
        assertEquals(user1, user1);
    }

    @Test
    @DisplayName("equals() - Un utilisateur ne devrait pas être égal à null")
    void testEquals_Null() {
        assertNotEquals(null, user1);
    }


    @Test
    @DisplayName("hashCode() - Deux utilisateurs avec le même ID devraient avoir le même hashCode")
    void testHashCode_SameId() {
        User userA = User.builder().id(1L).email("a@test.com").firstName("A").lastName("Test").password("pass").admin(false).build();
        User userB = User.builder().id(1L).email("b@test.com").firstName("B").lastName("Test").password("pass").admin(false).build();

        assertEquals(userA.hashCode(), userB.hashCode());
    }

    @Test
    @DisplayName("hashCode() - Deux utilisateurs avec des IDs différents peuvent avoir des hashCodes différents")
    void testHashCode_DifferentId() {
        User userA = User.builder().id(1L).email("a@test.com").firstName("A").lastName("Test").password("pass").admin(false).build();
        User userB = User.builder().id(2L).email("b@test.com").firstName("B").lastName("Test").password("pass").admin(false).build();

        assertNotEquals(userA.hashCode(), userB.hashCode());
    }


    @Test
    @DisplayName("toString() - Devrait contenir les informations principales")
    void testToString_ContainsMainInfo() {
        String result = user1.toString();

        assertNotNull(result);
        assertTrue(result.contains("User"));
    }


    @Test
    @DisplayName("Chaînage - Devrait permettre le chaînage des setters")
    void testChaining_Setters() {
        User user = new User()
                .setEmail("chain@test.com")
                .setFirstName("Chain")
                .setLastName("Test")
                .setPassword("pass")
                .setAdmin(true);

        assertEquals("chain@test.com", user.getEmail());
        assertEquals("Chain", user.getFirstName());
        assertEquals("Test", user.getLastName());
        assertTrue(user.isAdmin());
    }


    @Test
    @DisplayName("NoArgsConstructor - Devrait créer un utilisateur vide")
    void testNoArgsConstructor() {
        User user = new User();

        assertNotNull(user);
        assertNull(user.getId());
        assertNull(user.getEmail());
    }

    @Test
    @DisplayName("RequiredArgsConstructor - Devrait créer un utilisateur avec les champs requis")
    void testRequiredArgsConstructor() {
        User user = new User("test@example.com", "Sadou", "Loic", "password", true);

        assertEquals("test@example.com", user.getEmail());
        assertEquals("Sadou", user.getLastName());
        assertEquals("Loic", user.getFirstName());
        assertEquals("password", user.getPassword());
        assertTrue(user.isAdmin());
    }

    @Test
    @DisplayName("AllArgsConstructor - Devrait créer un utilisateur avec tous les champs")
    void testAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();

        User user = new User(1L, "test@example.com", "Sadou", "Loic", "password", false, now, now);

        assertEquals(1L, user.getId());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("Sadou", user.getLastName());
        assertEquals("Loic", user.getFirstName());
        assertEquals("password", user.getPassword());
        assertFalse(user.isAdmin());
        assertEquals(now, user.getCreatedAt());
        assertEquals(now, user.getUpdatedAt());
    }
}
