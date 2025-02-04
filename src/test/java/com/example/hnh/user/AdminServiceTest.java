package com.example.hnh.user;

import com.example.hnh.global.RedisService;
import com.example.hnh.global.error.errorcode.ErrorCode;
import com.example.hnh.global.error.exception.CustomException;
import com.example.hnh.group.GroupRepository;
import com.example.hnh.user.dto.AdminCreateRequestDto;
import com.example.hnh.user.dto.AdminResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@DisplayName("관리자 기능 테스트")
class AdminServiceTest {

    @InjectMocks
    private AdminService adminService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private RedisService redisService;

    private AdminCreateRequestDto adminCreateRequestDto;
    private User admin;

    @BeforeEach
    void setUp() {
        adminCreateRequestDto = new AdminCreateRequestDto("admin@example.com", "password", "testAdmin");
        admin = new User("admin@example.com", "testAdmin", "encodedPassword");
    }

    @Test
    void createAdmin_ShouldCreateAdmin_WhenEmailIsUnique() {
        when(userRepository.findByEmail(adminCreateRequestDto.getEmail())).thenReturn(Optional.empty());
        when(bCryptPasswordEncoder.encode(adminCreateRequestDto.getPassword())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(admin);

        AdminResponseDto response = adminService.createAdmin(adminCreateRequestDto);

        assertNotNull(response);
        assertEquals(admin.getEmail(), response.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void createAdmin_ShouldThrowException_WhenEmailIsDuplicated() {
        when(userRepository.findByEmail(adminCreateRequestDto.getEmail())).thenReturn(Optional.of(admin));

        CustomException exception = assertThrows(CustomException.class, () -> {
            adminService.createAdmin(adminCreateRequestDto);
        });

        assertEquals(ErrorCode.DUPLICATE_RESOURCE, exception.getErrorCode());
    }
}
