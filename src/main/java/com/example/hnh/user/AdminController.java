package com.example.hnh.user;

import com.example.hnh.category.CategoryService;
import com.example.hnh.category.dto.CategoryResponseDto;
import com.example.hnh.category.dto.CreateCategoryRequestDto;
import com.example.hnh.user.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admins")
public class AdminController {

    private final AdminService adminService;
    private final CategoryService categoryService;

    /**
     * 관리자 생성 메서드
     *
     * @param adminCreateRequestDto 관리자 생성 정보
     * @return
     */
    @PostMapping("/signup")
    public ResponseEntity<AdminResponseDto> createAdmin(
            @Valid @RequestBody AdminCreateRequestDto adminCreateRequestDto
    ) {

        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createAdmin(adminCreateRequestDto));
    }

    /**
     * 카테고리 생성 메서드
     *
     * @param createCategoryRequestDto 카테고리 생성 정보
     * @return
     */
    @PostMapping("/categories")
    public ResponseEntity<CategoryResponseDto> createCategory(
            @Valid @RequestBody CreateCategoryRequestDto createCategoryRequestDto
    ) {

        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(createCategoryRequestDto));
    }

    /**
     * 관리자 통계 메서드
     *
     * @param startDate 조회 기간 시작 날짜
     * @param endDate   조회 기간 마지막 날짜
     * @param groupName 조회 그룹 이름
     * @return
     */
    @GetMapping
    public ResponseEntity<DashboardResponseDto> findStats(
            @RequestParam(required = false, defaultValue = "2000-01-01") String startDate,
            @RequestParam(required = false, defaultValue = "2099-12-31") String endDate,
            @RequestParam String groupName
            ) {

        return ResponseEntity.ok(adminService.findStats(startDate, endDate, groupName));
    }

    /**
     * 유저 리포트 메서드
     *
     * @param reportUserRequestDto 유저 리포트 정보
     * @return
     */
    @PatchMapping("/report-users")
    public ResponseEntity<ReportUserResponseDto> reportUser(
            @Valid @RequestBody ReportUserRequestDto reportUserRequestDto
    ) {

        return ResponseEntity.ok(adminService.reportUser(reportUserRequestDto));
    }

    /**
     * 그룹 리포트 메서드
     *
     * @param reportGroupRequestDto 그룹 리포트 정보
     * @return
     */
    @PatchMapping("/report-groups")
    public ResponseEntity<ReportGroupResponseDto> reportGroup(
            @Valid @RequestBody ReportGroupRequestDto reportGroupRequestDto
    ) {

        return ResponseEntity.ok(adminService.reportGroup(reportGroupRequestDto));
    }
}
