package com.example.hnh.user;

import com.example.hnh.global.RedisService;
import com.example.hnh.global.error.errorcode.ErrorCode;
import com.example.hnh.global.error.exception.CustomException;
import com.example.hnh.group.Group;
import com.example.hnh.group.GroupRepository;
import com.example.hnh.user.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
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

    // 테스트 시작할 때 기본 설정값
    @BeforeEach
    void setUp() {
        adminCreateRequestDto = new AdminCreateRequestDto("admin@example.com", "password", "testAdmin");
        admin = new User("admin@example.com", "testAdmin", "encodedPassword");
    }

    // 이메일이 중복되지 않을 경우 관리자 생성
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

    // 이메일이 중복된 경우 예외 처리
    @Test
    void createAdmin_ShouldThrowException_WhenEmailIsDuplicated() {
        when(userRepository.findByEmail(adminCreateRequestDto.getEmail())).thenReturn(Optional.of(admin));

        CustomException exception = assertThrows(CustomException.class, () -> {
            adminService.createAdmin(adminCreateRequestDto);
        });

        assertEquals(ErrorCode.DUPLICATE_RESOURCE, exception.getErrorCode());
    }

    // 유저를 블락하는 경우
    @Test
    void blockUser_ShouldBlockUser_WhenValidRequest() {
        BlockUserRequestDto requestDto = new BlockUserRequestDto(1L, "blocked");
        User user = new User("user@example.com", "testUser", "password");

        when(userRepository.findById(requestDto.getUserId())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        BlockUserResponseDto responseDto = adminService.BlockUser(requestDto);

        assertNotNull(responseDto);
        assertEquals(requestDto.getStatus(), responseDto.getStatus());
        verify(userRepository, times(1)).save(any(User.class));
    }

    // 그룹을 블락하는 경우
    @Test
    void blockGroup_ShouldBlockGroup_WhenValidRequest() {
        BlockGroupRequestDto requestDto = new BlockGroupRequestDto(1L, "blocked");
        Group group = new Group();

        when(groupRepository.findById(requestDto.getGroupId())).thenReturn(Optional.of(group));
        when(groupRepository.save(any(Group.class))).thenReturn(group);

        BlockGroupResponseDto responseDto = adminService.BlockGroup(requestDto);

        assertNotNull(responseDto);
        assertEquals(requestDto.getStatus(), responseDto.getStatus());
        verify(groupRepository, times(1)).save(any(Group.class));
    }

    // Redis 에 데이터가 존재할 경우
    @Test
    void findStats_ShouldReturnStats_WhenDataExistsInRedis() {
        String startDate = "2024-01-01";
        String endDate = "2024-01-31";
        String groupName = "testGroup";
        DashboardResponseDto mockStats = new DashboardResponseDto(1L, 2L, 3L, 4L, "testGroup");

        when(redisService.findGroupStats(groupName, startDate, endDate)).thenReturn(mockStats);

        DashboardResponseDto result = adminService.findStats(startDate, endDate, groupName);

        assertNotNull(result);
        assertEquals(mockStats, result);
        verify(redisService, times(1)).findGroupStats(groupName, startDate, endDate);
    }

    // Redis 에 데이터가 없을 경우
    @Test
    void findStats_ShouldFetchFromDb_WhenRedisIsEmpty() {
        String startDate = "2024-01-01";
        String endDate = "2024-01-31";
        String groupName = "testGroup";
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        DashboardResponseDto mockStats = new DashboardResponseDto(1L, 2L, 3L, 4L, "testGroup");

        when(redisService.findGroupStats(groupName, startDate, endDate)).thenReturn(null);
        when(groupRepository.findStatsByName(start.atStartOfDay(), end.atStartOfDay(), groupName)).thenReturn(mockStats);

        DashboardResponseDto result = adminService.findStats(startDate, endDate, groupName);

        assertNotNull(result);
        assertEquals(mockStats, result);
        verify(redisService, times(1)).findGroupStats(groupName, startDate, endDate);
    }
}
