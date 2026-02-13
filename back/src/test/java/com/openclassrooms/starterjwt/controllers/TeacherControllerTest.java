package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.mapper.TeacherMapper;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.services.TeacherService;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("TeacherController - Tests d'intégration")
class TeacherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TeacherService teacherService;

    @MockBean
    private TeacherMapper teacherMapper;

    private Teacher teacher1;
    private Teacher teacher2;
    private TeacherDto teacherDto1;
    private TeacherDto teacherDto2;

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

        teacherDto1 = new TeacherDto();
        teacherDto1.setId(1L);
        teacherDto1.setFirstName("Marie");
        teacherDto1.setLastName("Dupont");

        teacherDto2 = new TeacherDto();
        teacherDto2.setId(2L);
        teacherDto2.setFirstName("Jean");
        teacherDto2.setLastName("Martin");
    }


    @Test
    @DisplayName("GET /api/teacher/{id} - Devrait retourner un professeur par son ID")
    @WithMockUser
    void testFindById_Success() throws Exception {
        when(teacherService.findById(1L)).thenReturn(teacher1);
        when(teacherMapper.toDto(teacher1)).thenReturn(teacherDto1);

        mockMvc.perform(get("/api/teacher/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.firstName").value("Marie"))
                .andExpect(jsonPath("$.lastName").value("Dupont"));

        verify(teacherService, times(1)).findById(1L);
        verify(teacherMapper, times(1)).toDto(teacher1);
    }

    @Test
    @DisplayName("GET /api/teacher/{id} - Devrait retourner 404 si professeur non trouvé")
    @WithMockUser
    void testFindById_NotFound() throws Exception {
        when(teacherService.findById(999L)).thenReturn(null);

        mockMvc.perform(get("/api/teacher/999"))
                .andExpect(status().isNotFound());

        verify(teacherService, times(1)).findById(999L);
        verify(teacherMapper, never()).toDto(any(Teacher.class));
    }

    @Test
    @DisplayName("GET /api/teacher/{id} - Devrait retourner 400 pour un ID invalide")
    @WithMockUser
    void testFindById_InvalidId() throws Exception {
        mockMvc.perform(get("/api/teacher/invalid"))
                .andExpect(status().isBadRequest());

        verify(teacherService, never()).findById(anyLong());
        verify(teacherMapper, never()).toDto(any(Teacher.class));
    }

    @Test
    @DisplayName("GET /api/teacher/{id} - Devrait gérer les IDs avec des caractères spéciaux")
    @WithMockUser
    void testFindById_SpecialCharacters() throws Exception {
        mockMvc.perform(get("/api/teacher/abc123"))
                .andExpect(status().isBadRequest());

        verify(teacherService, never()).findById(anyLong());
    }


    @Test
    @DisplayName("GET /api/teacher - Devrait retourner tous les professeurs")
    @WithMockUser
    void testFindAll_Success() throws Exception {
        List<Teacher> teachers = Arrays.asList(teacher1, teacher2);
        List<TeacherDto> teacherDtos = Arrays.asList(teacherDto1, teacherDto2);

        when(teacherService.findAll()).thenReturn(teachers);
        when(teacherMapper.toDto(teachers)).thenReturn(teacherDtos);

        mockMvc.perform(get("/api/teacher"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].firstName").value("Marie"))
                .andExpect(jsonPath("$[0].lastName").value("Dupont"))
                .andExpect(jsonPath("$[1].firstName").value("Jean"))
                .andExpect(jsonPath("$[1].lastName").value("Martin"));

        verify(teacherService, times(1)).findAll();
        verify(teacherMapper, times(1)).toDto(teachers);
    }

    @Test
    @DisplayName("GET /api/teacher - Devrait retourner une liste vide si aucun professeur")
    @WithMockUser
    void testFindAll_EmptyList() throws Exception {
        List<Teacher> emptyTeachers = new ArrayList<>();
        List<TeacherDto> emptyDtos = new ArrayList<>();

        when(teacherService.findAll()).thenReturn(emptyTeachers);
        when(teacherMapper.toDto(emptyTeachers)).thenReturn(emptyDtos);

        mockMvc.perform(get("/api/teacher"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(teacherService, times(1)).findAll();
    }

    @Test
    @DisplayName("GET /api/teacher - Devrait retourner un seul professeur dans la liste")
    @WithMockUser
    void testFindAll_SingleTeacher() throws Exception {
        List<Teacher> teachers = Arrays.asList(teacher1);
        List<TeacherDto> teacherDtos = Arrays.asList(teacherDto1);

        when(teacherService.findAll()).thenReturn(teachers);
        when(teacherMapper.toDto(teachers)).thenReturn(teacherDtos);

        mockMvc.perform(get("/api/teacher"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].firstName").value("Marie"));

        verify(teacherService, times(1)).findAll();
    }
}
