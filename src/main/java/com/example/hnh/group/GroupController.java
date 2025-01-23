package com.example.hnh.group;


import com.example.hnh.global.config.auth.UserDetailsImpl;
import com.example.hnh.group.dto.GroupDetailResponseDto;
import com.example.hnh.group.dto.GroupRankingResponseDto;
import com.example.hnh.group.dto.GroupRequestDto;
import com.example.hnh.group.dto.GroupResponseDto;
import com.example.hnh.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/groups")
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    /**
     * 그룹 생성 API
     * @param userDetails
     * @param requestDto
     * @param image
     * @return
     * @throws IOException
     */
    @PostMapping
    public ResponseEntity<GroupResponseDto> createGroup (@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                         @RequestPart("requestDto") GroupRequestDto requestDto,
                                                         @RequestPart("image") MultipartFile image) throws IOException {
        User loginUser = userDetails.getUser();

        GroupResponseDto groupResponseDto = groupService.createGroup(
                loginUser,
                requestDto.getCategoryId(),
                requestDto.getGroupName(),
                requestDto.getDetail(),
                image);

        return new ResponseEntity<>(groupResponseDto, HttpStatus.CREATED);
    }

    /**
     * 그룹 단건 조회 API
     * @param groupId
     * @return
     */
    @GetMapping("/{groupId}")
    public ResponseEntity<GroupDetailResponseDto> findGroup(@PathVariable Long groupId) {
        // 서비스 호출로 그룹 상세 정보 조회
        GroupDetailResponseDto groupDetails = groupService.findGroup(groupId);

        // 응답 반환
        return ResponseEntity.ok(groupDetails);
    }

    /**
     * 그룹 수정 API
     * @param groupId
     * @param requestDto
     * @return
     */
    @PatchMapping("/{groupId}")
    public ResponseEntity<GroupResponseDto> updateGroup(@PathVariable Long groupId,
                                                        @RequestBody GroupRequestDto requestDto) {

        GroupResponseDto updatedGroup = groupService.updateGroup(groupId, requestDto);

        return ResponseEntity.ok(updatedGroup);
    }


    /**
     * 그룹 삭제 API
     * @param groupId
     * @param userDetails
     * @return
     */
    @DeleteMapping("/{groupId}")
    public ResponseEntity<String> deleteGruop(@PathVariable Long groupId,
                                              @AuthenticationPrincipal UserDetailsImpl userDetails) {

        User loginUser = userDetails.getUser();

        groupService.deleteGroup(groupId, loginUser);

        return new ResponseEntity<>("그룹이 삭제되었습니다.", HttpStatus.OK);
    }


    /**
     * 그룹 전체 랭킹 조회 API
     * @return
     */
    @GetMapping
    public ResponseEntity<List<GroupRankingResponseDto>> findAllGroups() {

        List<GroupRankingResponseDto> groups = groupService.findAllGroupsWithRanking();
        return ResponseEntity.ok(groups);
    }
}
