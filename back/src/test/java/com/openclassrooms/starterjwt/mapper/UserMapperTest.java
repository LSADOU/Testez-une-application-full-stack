package com.openclassrooms.starterjwt.mapper;

import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.models.User;
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
@DisplayName("UserMapper - Tests unitaires")
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();

        user = User.builder()
                .id(1L)
                .email("test@example.com")
                .firstName("Loic")
                .lastName("Sadou")
                .password("encodedPassword")
                .admin(false)
                .createdAt(now)
                .updatedAt(now)
                .build();

        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setEmail("test@example.com");
        userDto.setFirstName("Loic");
        userDto.setLastName("Sadou");
        userDto.setPassword("encodedPassword");
        userDto.setAdmin(false);
        userDto.setCreatedAt(now);
        userDto.setUpdatedAt(now);
    }


    @Test
    @DisplayName("toEntity() - Devrait convertir un UserDto en User")
    void testToEntity_Success() {
        User result = userMapper.toEntity(userDto);

        assertNotNull(result);
        assertEquals(userDto.getId(), result.getId());
        assertEquals(userDto.getEmail(), result.getEmail());
        assertEquals(userDto.getFirstName(), result.getFirstName());
        assertEquals(userDto.getLastName(), result.getLastName());
        assertEquals(userDto.isAdmin(), result.isAdmin());
    }

    @Test
    @DisplayName("toEntity() - Devrait retourner null pour un DTO null")
    void testToEntity_NullDto() {
        User result = userMapper.toEntity((UserDto) null);

        assertNull(result);
    }

    @Test
    @DisplayName("toEntity() - Devrait convertir une liste de DTOs en liste d'entités")
    void testToEntity_List() {
        UserDto userDto2 = new UserDto();
        userDto2.setId(2L);
        userDto2.setEmail("user2@example.com");
        userDto2.setFirstName("Jane");
        userDto2.setLastName("Smith");
        userDto2.setPassword("password456");
        userDto2.setAdmin(true);

        List<UserDto> userDtos = Arrays.asList(userDto, userDto2);

        List<User> result = userMapper.toEntity(userDtos);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("test@example.com", result.get(0).getEmail());
        assertFalse(result.get(0).isAdmin());
        assertEquals("user2@example.com", result.get(1).getEmail());
        assertTrue(result.get(1).isAdmin());
    }

    @Test
    @DisplayName("toEntity() - Devrait mapper un utilisateur admin")
    void testToEntity_AdminUser() {
        UserDto adminDto = new UserDto();
        adminDto.setId(2L);
        adminDto.setEmail("admin@example.com");
        adminDto.setFirstName("Admin");
        adminDto.setLastName("User");
        adminDto.setPassword("adminpass");
        adminDto.setAdmin(true);

        User result = userMapper.toEntity(adminDto);

        assertNotNull(result);
        assertTrue(result.isAdmin());
        assertEquals("admin@example.com", result.getEmail());
    }


    @Test
    @DisplayName("toDto() - Devrait convertir un User en UserDto")
    void testToDto_Success() {
        UserDto result = userMapper.toDto(user);

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getFirstName(), result.getFirstName());
        assertEquals(user.getLastName(), result.getLastName());
        assertEquals(user.isAdmin(), result.isAdmin());
        assertEquals(user.getCreatedAt(), result.getCreatedAt());
        assertEquals(user.getUpdatedAt(), result.getUpdatedAt());
    }

    @Test
    @DisplayName("toDto() - Devrait retourner null pour une entité null")
    void testToDto_NullEntity() {
        UserDto result = userMapper.toDto((User) null);

        assertNull(result);
    }

    @Test
    @DisplayName("toDto() - Devrait convertir une liste d'entités en liste de DTOs")
    void testToDto_List() {
        User user2 = User.builder()
                .id(2L)
                .email("user2@example.com")
                .firstName("Jane")
                .lastName("Smith")
                .password("password")
                .admin(true)
                .build();

        List<User> users = Arrays.asList(user, user2);

        List<UserDto> result = userMapper.toDto(users);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("test@example.com", result.get(0).getEmail());
        assertFalse(result.get(0).isAdmin());
        assertEquals("user2@example.com", result.get(1).getEmail());
        assertTrue(result.get(1).isAdmin());
    }

    @Test
    @DisplayName("toDto() - Devrait mapper un utilisateur admin")
    void testToDto_AdminUser() {
        User adminUser = User.builder()
                .id(3L)
                .email("admin@example.com")
                .firstName("Admin")
                .lastName("User")
                .password("adminpass")
                .admin(true)
                .build();

        UserDto result = userMapper.toDto(adminUser);

        assertNotNull(result);
        assertTrue(result.isAdmin());
        assertEquals("admin@example.com", result.getEmail());
        assertEquals("Admin", result.getFirstName());
    }

    @Test
    @DisplayName("toDto() - Devrait conserver les timestamps")
    void testToDto_PreservesTimestamps() {
        LocalDateTime specificTime = LocalDateTime.of(2024, 1, 15, 10, 30);
        User userWithTime = User.builder()
                .id(4L)
                .email("time@example.com")
                .firstName("Time")
                .lastName("User")
                .password("pass")
                .admin(false)
                .createdAt(specificTime)
                .updatedAt(specificTime)
                .build();

        UserDto result = userMapper.toDto(userWithTime);

        assertNotNull(result);
        assertEquals(specificTime, result.getCreatedAt());
        assertEquals(specificTime, result.getUpdatedAt());
    }

    @Test
    @DisplayName("toEntity() - Devrait mapper tous les champs correctement")
    void testToEntity_AllFields() {
        LocalDateTime specificTime = LocalDateTime.of(2024, 1, 15, 10, 30);
        UserDto completeDto = new UserDto();
        completeDto.setId(5L);
        completeDto.setEmail("complete@example.com");
        completeDto.setFirstName("Complete");
        completeDto.setLastName("User");
        completeDto.setPassword("password123");
        completeDto.setAdmin(true);
        completeDto.setCreatedAt(specificTime);
        completeDto.setUpdatedAt(specificTime);

        User result = userMapper.toEntity(completeDto);

        assertNotNull(result);
        assertEquals(5L, result.getId());
        assertEquals("complete@example.com", result.getEmail());
        assertEquals("Complete", result.getFirstName());
        assertEquals("User", result.getLastName());
        assertTrue(result.isAdmin());
    }
}
