package com.openclassrooms.starterjwt.mapper;

import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.models.Teacher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("TeacherMapper - Tests unitaires")
class TeacherMapperTest {

    @Autowired
    private TeacherMapper teacherMapper;

    private Teacher teacher;
    private TeacherDto teacherDto;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();

        teacher = Teacher.builder()
                .id(1L)
                .firstName("Marie")
                .lastName("Dupont")
                .createdAt(now)
                .updatedAt(now)
                .build();

        teacherDto = new TeacherDto();
        teacherDto.setId(1L);
        teacherDto.setFirstName("Marie");
        teacherDto.setLastName("Dupont");
        teacherDto.setCreatedAt(now);
        teacherDto.setUpdatedAt(now);
    }

    @Test
    @DisplayName("toEntity() - Devrait convertir un TeacherDto en Teacher")
    void testToEntity_Success() {
        Teacher result = teacherMapper.toEntity(teacherDto);

        assertNotNull(result);
        assertEquals(teacherDto.getId(), result.getId());
        assertEquals(teacherDto.getFirstName(), result.getFirstName());
        assertEquals(teacherDto.getLastName(), result.getLastName());
    }

    @Test
    @DisplayName("toEntity() - Devrait retourner null pour un DTO null")
    void testToEntity_NullDto() {
        Teacher result = teacherMapper.toEntity((TeacherDto) null);

        assertNull(result);
    }

    @Test
    @DisplayName("toEntity() - Devrait convertir une liste de DTOs en liste d'entités")
    void testToEntity_List() {
        TeacherDto teacherDto2 = new TeacherDto();
        teacherDto2.setId(2L);
        teacherDto2.setFirstName("Jean");
        teacherDto2.setLastName("Martin");

        List<TeacherDto> teacherDtos = Arrays.asList(teacherDto, teacherDto2);

        List<Teacher> result = teacherMapper.toEntity(teacherDtos);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Marie", result.get(0).getFirstName());
        assertEquals("Dupont", result.get(0).getLastName());
        assertEquals("Jean", result.get(1).getFirstName());
        assertEquals("Martin", result.get(1).getLastName());
    }

    @Test
    @DisplayName("toDto() - Devrait convertir un Teacher en TeacherDto")
    void testToDto_Success() {
        TeacherDto result = teacherMapper.toDto(teacher);

        assertNotNull(result);
        assertEquals(teacher.getId(), result.getId());
        assertEquals(teacher.getFirstName(), result.getFirstName());
        assertEquals(teacher.getLastName(), result.getLastName());
        assertEquals(teacher.getCreatedAt(), result.getCreatedAt());
        assertEquals(teacher.getUpdatedAt(), result.getUpdatedAt());
    }

    @Test
    @DisplayName("toDto() - Devrait retourner null pour une entité null")
    void testToDto_NullEntity() {
        TeacherDto result = teacherMapper.toDto((Teacher) null);

        assertNull(result);
    }

    @Test
    @DisplayName("toDto() - Devrait convertir une liste d'entités en liste de DTOs")
    void testToDto_List() {
        Teacher teacher2 = Teacher.builder()
                .id(2L)
                .firstName("Jean")
                .lastName("Martin")
                .build();

        List<Teacher> teachers = Arrays.asList(teacher, teacher2);

        List<TeacherDto> result = teacherMapper.toDto(teachers);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Marie", result.get(0).getFirstName());
        assertEquals("Dupont", result.get(0).getLastName());
        assertEquals("Jean", result.get(1).getFirstName());
        assertEquals("Martin", result.get(1).getLastName());
    }

    @Test
    @DisplayName("toDto() - Devrait conserver les timestamps")
    void testToDto_PreservesTimestamps() {
        LocalDateTime specificTime = LocalDateTime.of(2024, 1, 15, 10, 30);
        Teacher teacherWithTime = Teacher.builder()
                .id(3L)
                .firstName("Test")
                .lastName("User")
                .createdAt(specificTime)
                .updatedAt(specificTime)
                .build();

        TeacherDto result = teacherMapper.toDto(teacherWithTime);

        assertNotNull(result);
        assertEquals(specificTime, result.getCreatedAt());
        assertEquals(specificTime, result.getUpdatedAt());
    }

    @Test
    @DisplayName("toEntity() - Devrait mapper tous les champs correctement")
    void testToEntity_AllFields() {
        LocalDateTime specificTime = LocalDateTime.of(2024, 1, 15, 10, 30);
        TeacherDto completeDto = new TeacherDto();
        completeDto.setId(5L);
        completeDto.setFirstName("Complete");
        completeDto.setLastName("Teacher");
        completeDto.setCreatedAt(specificTime);
        completeDto.setUpdatedAt(specificTime);

        Teacher result = teacherMapper.toEntity(completeDto);

        assertNotNull(result);
        assertEquals(5L, result.getId());
        assertEquals("Complete", result.getFirstName());
        assertEquals("Teacher", result.getLastName());
    }
}
