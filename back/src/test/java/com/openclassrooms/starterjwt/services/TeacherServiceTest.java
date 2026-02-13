package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
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
@DisplayName("TeacherService - Tests unitaires")
class TeacherServiceTest {

    @Mock
    private TeacherRepository teacherRepository;

    @InjectMocks
    private TeacherService teacherService;

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
    @DisplayName("findAll() - Devrait retourner tous les professeurs")
    void testFindAll_Success() {
        List<Teacher> teachers = Arrays.asList(teacher1, teacher2);
        when(teacherRepository.findAll()).thenReturn(teachers);

        List<Teacher> result = teacherService.findAll();
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Marie", result.get(0).getFirstName());
        assertEquals("Dupont", result.get(0).getLastName());
        assertEquals("Jean", result.get(1).getFirstName());
        assertEquals("Martin", result.get(1).getLastName());
        verify(teacherRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findAll() - Devrait retourner une liste vide si aucun professeur")
    void testFindAll_EmptyList() {
        when(teacherRepository.findAll()).thenReturn(new ArrayList<>());

        List<Teacher> result = teacherService.findAll();
        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertEquals(0, result.size());
        verify(teacherRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findById() - Devrait retourner un professeur par son ID")
    void testFindById_Success() {
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher1));

        Teacher result = teacherService.findById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Marie", result.getFirstName());
        assertEquals("Dupont", result.getLastName());
        verify(teacherRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("findById() - Devrait retourner null si professeur non trouvé")
    void testFindById_NotFound() {
        when(teacherRepository.findById(999L)).thenReturn(Optional.empty());

        Teacher result = teacherService.findById(999L);
        assertNull(result);
        verify(teacherRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("findById() - Devrait retourner null pour un ID null")
    void testFindById_NullId() {
        when(teacherRepository.findById(null)).thenReturn(Optional.empty());

        Teacher result = teacherService.findById(null);
        assertNull(result);
        verify(teacherRepository, times(1)).findById(null);
    }

    @Test
    @DisplayName("findById() - Devrait gérer les différents professeurs")
    void testFindById_DifferentTeachers() {
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher1));
        when(teacherRepository.findById(2L)).thenReturn(Optional.of(teacher2));

        Teacher resultTeacher1 = teacherService.findById(1L);
        Teacher resultTeacher2 = teacherService.findById(2L);
        assertNotNull(resultTeacher1);
        assertNotNull(resultTeacher2);
        assertNotEquals(resultTeacher1.getId(), resultTeacher2.getId());
        assertEquals("Marie", resultTeacher1.getFirstName());
        assertEquals("Jean", resultTeacher2.getFirstName());
        verify(teacherRepository, times(1)).findById(1L);
        verify(teacherRepository, times(1)).findById(2L);
    }
}
