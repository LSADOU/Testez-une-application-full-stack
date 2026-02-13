package com.openclassrooms.starterjwt.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.mapper.SessionMapper;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.services.SessionService;
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
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("SessionController - Tests d'intégration")
class SessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SessionService sessionService;

    @MockBean
    private SessionMapper sessionMapper;

    private Session session;
    private SessionDto sessionDto;

    @BeforeEach
    void setUp() {
        Teacher teacher = Teacher.builder()
                .id(1L)
                .firstName("Marie")
                .lastName("Dupont")
                .build();

        session = Session.builder()
                .id(1L)
                .name("Yoga Session")
                .date(new Date())
                .description("Test session description")
                .teacher(teacher)
                .users(new ArrayList<>())
                .build();

        sessionDto = new SessionDto();
        sessionDto.setId(1L);
        sessionDto.setName("Yoga Session");
        sessionDto.setDate(new Date());
        sessionDto.setDescription("Test session description");
        sessionDto.setTeacher_id(1L);
        sessionDto.setUsers(new ArrayList<>());
    }


    @Test
    @DisplayName("GET /api/session/{id} - Devrait retourner une session par son ID")
    @WithMockUser
    void testFindById_Success() throws Exception {
        when(sessionService.getById(1L)).thenReturn(session);
        when(sessionMapper.toDto(session)).thenReturn(sessionDto);

        mockMvc.perform(get("/api/session/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Yoga Session"))
                .andExpect(jsonPath("$.description").value("Test session description"));

        verify(sessionService, times(1)).getById(1L);
        verify(sessionMapper, times(1)).toDto(session);
    }

    @Test
    @DisplayName("GET /api/session/{id} - Devrait retourner 404 si session non trouvée")
    @WithMockUser
    void testFindById_NotFound() throws Exception {
        when(sessionService.getById(999L)).thenReturn(null);

        mockMvc.perform(get("/api/session/999"))
                .andExpect(status().isNotFound());

        verify(sessionService, times(1)).getById(999L);
        verify(sessionMapper, never()).toDto(any(Session.class));
    }

    @Test
    @DisplayName("GET /api/session/{id} - Devrait retourner 400 pour un ID invalide")
    @WithMockUser
    void testFindById_InvalidId() throws Exception {
        mockMvc.perform(get("/api/session/invalid"))
                .andExpect(status().isBadRequest());

        verify(sessionService, never()).getById(anyLong());
    }


    @Test
    @DisplayName("GET /api/session - Devrait retourner toutes les sessions")
    @WithMockUser
    void testFindAll_Success() throws Exception {
        Session session2 = Session.builder()
                .id(2L)
                .name("Pilates Session")
                .build();

        SessionDto sessionDto2 = new SessionDto();
        sessionDto2.setId(2L);
        sessionDto2.setName("Pilates Session");

        List<Session> sessions = Arrays.asList(session, session2);
        List<SessionDto> sessionDtos = Arrays.asList(sessionDto, sessionDto2);

        when(sessionService.findAll()).thenReturn(sessions);
        when(sessionMapper.toDto(sessions)).thenReturn(sessionDtos);

        mockMvc.perform(get("/api/session"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Yoga Session"))
                .andExpect(jsonPath("$[1].name").value("Pilates Session"));

        verify(sessionService, times(1)).findAll();
        verify(sessionMapper, times(1)).toDto(sessions);
    }

    @Test
    @DisplayName("GET /api/session - Devrait retourner une liste vide si aucune session")
    @WithMockUser
    void testFindAll_EmptyList() throws Exception {
        List<Session> emptySessions = new ArrayList<>();
        List<SessionDto> emptyDtos = new ArrayList<>();

        when(sessionService.findAll()).thenReturn(emptySessions);
        when(sessionMapper.toDto(emptySessions)).thenReturn(emptyDtos);

        mockMvc.perform(get("/api/session"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(sessionService, times(1)).findAll();
    }


    @Test
    @DisplayName("POST /api/session - Devrait créer une session")
    @WithMockUser
    void testCreate_Success() throws Exception {
        when(sessionMapper.toEntity(any(SessionDto.class))).thenReturn(session);
        when(sessionService.create(any(Session.class))).thenReturn(session);
        when(sessionMapper.toDto(session)).thenReturn(sessionDto);

        mockMvc.perform(post("/api/session")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sessionDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Yoga Session"));

        verify(sessionMapper, times(1)).toEntity(any(SessionDto.class));
        verify(sessionService, times(1)).create(any(Session.class));
        verify(sessionMapper, times(1)).toDto(session);
    }


    @Test
    @DisplayName("PUT /api/session/{id} - Devrait mettre à jour une session")
    @WithMockUser
    void testUpdate_Success() throws Exception {
        SessionDto updatedDto = new SessionDto();
        updatedDto.setName("Updated Yoga Session");
        updatedDto.setDate(new Date());
        updatedDto.setDescription("Updated description");
        updatedDto.setTeacher_id(1L);

        Session updatedSession = Session.builder()
                .id(1L)
                .name("Updated Yoga Session")
                .build();

        when(sessionMapper.toEntity(any(SessionDto.class))).thenReturn(updatedSession);
        when(sessionService.update(anyLong(), any(Session.class))).thenReturn(updatedSession);
        when(sessionMapper.toDto(updatedSession)).thenReturn(updatedDto);

        mockMvc.perform(put("/api/session/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Yoga Session"));

        verify(sessionService, times(1)).update(eq(1L), any(Session.class));
    }

    @Test
    @DisplayName("PUT /api/session/{id} - Devrait retourner 400 pour un ID invalide")
    @WithMockUser
    void testUpdate_InvalidId() throws Exception {
        mockMvc.perform(put("/api/session/invalid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sessionDto)))
                .andExpect(status().isBadRequest());

        verify(sessionService, never()).update(anyLong(), any());
    }


    @Test
    @DisplayName("DELETE /api/session/{id} - Devrait supprimer une session")
    @WithMockUser
    void testDelete_Success() throws Exception {
        when(sessionService.getById(1L)).thenReturn(session);
        doNothing().when(sessionService).delete(1L);

        mockMvc.perform(delete("/api/session/1"))
                .andExpect(status().isOk());

        verify(sessionService, times(1)).getById(1L);
        verify(sessionService, times(1)).delete(1L);
    }

    @Test
    @DisplayName("DELETE /api/session/{id} - Devrait retourner 404 si session non trouvée")
    @WithMockUser
    void testDelete_NotFound() throws Exception {
        when(sessionService.getById(999L)).thenReturn(null);

        mockMvc.perform(delete("/api/session/999"))
                .andExpect(status().isNotFound());

        verify(sessionService, times(1)).getById(999L);
        verify(sessionService, never()).delete(anyLong());
    }

    @Test
    @DisplayName("DELETE /api/session/{id} - Devrait retourner 400 pour un ID invalide")
    @WithMockUser
    void testDelete_InvalidId() throws Exception {
        mockMvc.perform(delete("/api/session/invalid"))
                .andExpect(status().isBadRequest());

        verify(sessionService, never()).delete(anyLong());
    }


    @Test
    @DisplayName("POST /api/session/{id}/participate/{userId} - Devrait permettre la participation")
    @WithMockUser
    void testParticipate_Success() throws Exception {
        doNothing().when(sessionService).participate(1L, 1L);

        mockMvc.perform(post("/api/session/1/participate/1"))
                .andExpect(status().isOk());

        verify(sessionService, times(1)).participate(1L, 1L);
    }

    @Test
    @DisplayName("POST /api/session/{id}/participate/{userId} - Devrait retourner 400 pour ID invalide")
    @WithMockUser
    void testParticipate_InvalidId() throws Exception {
        mockMvc.perform(post("/api/session/invalid/participate/1"))
                .andExpect(status().isBadRequest());

        verify(sessionService, never()).participate(anyLong(), anyLong());
    }


    @Test
    @DisplayName("DELETE /api/session/{id}/participate/{userId} - Devrait retirer un participant")
    @WithMockUser
    void testNoLongerParticipate_Success() throws Exception {
        doNothing().when(sessionService).noLongerParticipate(1L, 1L);

        mockMvc.perform(delete("/api/session/1/participate/1"))
                .andExpect(status().isOk());

        verify(sessionService, times(1)).noLongerParticipate(1L, 1L);
    }

    @Test
    @DisplayName("DELETE /api/session/{id}/participate/{userId} - Devrait retourner 400 pour ID invalide")
    @WithMockUser
    void testNoLongerParticipate_InvalidId() throws Exception {
        mockMvc.perform(delete("/api/session/invalid/participate/1"))
                .andExpect(status().isBadRequest());

        verify(sessionService, never()).noLongerParticipate(anyLong(), anyLong());
    }
}
