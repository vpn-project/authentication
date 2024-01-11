package com.example.authentication.mapper;

import com.example.authentication.dto.RegisterDto;
import com.example.authentication.entity.Role;
import com.example.authentication.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserDtoMapperTest {

    private UserDtoMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new UserDtoMapper();
    }

    @Test
    void dtoToEntityTest() {
        RegisterDto dto = new RegisterDto("test", "new", "test@mail.ru");
        User expected = new User("test", "new", "test@mail.ru", Role.USER);
        User result = mapper.registerDtoToEntity(dto);
        assertEquals(expected, result);
    }

}
