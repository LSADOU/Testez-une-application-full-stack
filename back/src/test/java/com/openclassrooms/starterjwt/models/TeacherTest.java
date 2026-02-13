package com.openclassrooms.starterjwt.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Teacher Model - Tests unitaires")
class TeacherTest {

    private Teacher teacher1;
    private Teacher teacher2;

    @BeforeEach
    void setUp() {
        teacher1 = Teacher.builder()
                .id(1L)
                .firstName("Marie")
                .lastName("Dupont")
                .build();

        teacher2 = Teacher.builder()
                .id(2L)
                .firstName("Jean")
                .lastName("Martin")
                .build();
    }


    @Test
    @DisplayName("Builder - Devrait créer un professeur avec toutes les propriétés")
    void testBuilder_AllProperties() {
        LocalDateTime now = LocalDateTime.now();

        Teacher teacher = Teacher.builder()
                .id(1L)
                .firstName("Marie")
                .lastName("Dupont")
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertEquals(1L, teacher.getId());
        assertEquals("Marie", teacher.getFirstName());
        assertEquals("Dupont", teacher.getLastName());
        assertEquals(now, teacher.getCreatedAt());
        assertEquals(now, teacher.getUpdatedAt());
    }

    @Test
    @DisplayName("Builder - Devrait créer un professeur avec les champs minimaux")
    void testBuilder_MinimalFields() {
        Teacher teacher = Teacher.builder()
                .firstName("Test")
                .lastName("Teacher")
                .build();

        assertNull(teacher.getId());
        assertEquals("Test", teacher.getFirstName());
        assertEquals("Teacher", teacher.getLastName());
    }


    @Test
    @DisplayName("Setters - Devrait mettre à jour toutes les propriétés")
    void testSetters_AllProperties() {
        Teacher teacher = new Teacher();
        LocalDateTime now = LocalDateTime.now();

        teacher.setId(5L);
        teacher.setFirstName("Updated");
        teacher.setLastName("Name");
        teacher.setCreatedAt(now);
        teacher.setUpdatedAt(now);

        assertEquals(5L, teacher.getId());
        assertEquals("Updated", teacher.getFirstName());
        assertEquals("Name", teacher.getLastName());
        assertEquals(now, teacher.getCreatedAt());
        assertEquals(now, teacher.getUpdatedAt());
    }


    @Test
    @DisplayName("equals() - Deux professeurs avec le même ID devraient être égaux")
    void testEquals_SameId() {
        Teacher teacherA = Teacher.builder().id(1L).firstName("A").lastName("Test").build();
        Teacher teacherB = Teacher.builder().id(1L).firstName("B").lastName("Test").build();

assertEquals(teacherA, teacherB);
    }

    @Test
    @DisplayName("equals() - Deux professeurs avec des IDs différents ne devraient pas être égaux")
    void testEquals_DifferentId() {
assertNotEquals(teacher1, teacher2);
    }

    @Test
    @DisplayName("equals() - Un professeur devrait être égal à lui-même")
    void testEquals_SameObject() {
assertEquals(teacher1, teacher1);
    }

    @Test
    @DisplayName("equals() - Un professeur ne devrait pas être égal à null")
    void testEquals_Null() {
assertNotEquals(null, teacher1);
    }


    @Test
    @DisplayName("hashCode() - Deux professeurs avec le même ID devraient avoir le même hashCode")
    void testHashCode_SameId() {
        Teacher teacherA = Teacher.builder().id(1L).firstName("A").build();
        Teacher teacherB = Teacher.builder().id(1L).firstName("B").build();

assertEquals(teacherA.hashCode(), teacherB.hashCode());
    }

    @Test
    @DisplayName("hashCode() - Deux professeurs avec des IDs différents peuvent avoir des hashCodes différents")
    void testHashCode_DifferentId() {
assertNotEquals(teacher1.hashCode(), teacher2.hashCode());
    }


    @Test
    @DisplayName("toString() - Devrait contenir les informations principales")
    void testToString_ContainsMainInfo() {
        String result = teacher1.toString();

        assertNotNull(result);
        assertTrue(result.contains("Teacher"));
    }


    @Test
    @DisplayName("Chaînage - Devrait permettre le chaînage des setters")
    void testChaining_Setters() {
        Teacher teacher = new Teacher()
                .setFirstName("Chain")
                .setLastName("Test");

        assertEquals("Chain", teacher.getFirstName());
        assertEquals("Test", teacher.getLastName());
    }


    @Test
    @DisplayName("NoArgsConstructor - Devrait créer un professeur vide")
    void testNoArgsConstructor() {
        Teacher teacher = new Teacher();

        assertNotNull(teacher);
        assertNull(teacher.getId());
        assertNull(teacher.getFirstName());
        assertNull(teacher.getLastName());
    }

    @Test
    @DisplayName("AllArgsConstructor - Devrait créer un professeur avec tous les champs")
    void testAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();

        Teacher teacher = new Teacher(1L, "Dupont", "Marie", now, now);

        assertEquals(1L, teacher.getId());
        assertEquals("Dupont", teacher.getLastName());
        assertEquals("Marie", teacher.getFirstName());
        assertEquals(now, teacher.getCreatedAt());
        assertEquals(now, teacher.getUpdatedAt());
    }


    @Test
    @DisplayName("Devrait accepter des noms avec accents")
    void testAccentedNames() {
        Teacher teacher = Teacher.builder()
                .firstName("José")
                .lastName("François")
                .build();

        assertEquals("José", teacher.getFirstName());
        assertEquals("François", teacher.getLastName());
    }

    @Test
    @DisplayName("Devrait accepter des noms composés")
    void testCompoundNames() {
        Teacher teacher = Teacher.builder()
                .firstName("Jean-Pierre")
                .lastName("De La Fontaine")
                .build();

        assertEquals("Jean-Pierre", teacher.getFirstName());
        assertEquals("De La Fontaine", teacher.getLastName());
    }
}
