package com.example.hnh.group;


import com.example.hnh.global.config.annotation.AccessibleMember;
import com.example.hnh.global.config.auth.UserDetailsImpl;
import com.example.hnh.group.dto.GroupDetailResponseDto;
import com.example.hnh.group.dto.GroupRankingResponseDto;
import com.example.hnh.group.dto.GroupRequestDto;
import com.example.hnh.group.dto.GroupResponseDto;
import com.example.hnh.member.Member;
import com.example.hnh.member.MemberRepository;
import com.example.hnh.member.MemberRole;
import com.example.hnh.user.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/groups")
public class GroupController {

    private final GroupService groupService;
    private final MemberRepository memberRepository;

    public GroupController(GroupService groupService, MemberRepository memberRepository) {
        this.groupService = groupService;
        this.memberRepository = memberRepository;
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
    @AccessibleMember(requiredRoles = MemberRole.GROUP_ADMIN)
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
    @AccessibleMember(requiredRoles = {MemberRole.GROUP_ADMIN} )
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


    /**
     * 그룹 선택 API
     * @param groudId
     * @param authentication
     * @param servletRequest
     * @return
     */
    @PostMapping("/{groudId}/choice")
    public ResponseEntity<String> selectGroup (
            @PathVariable Long groudId,
            Authentication authentication,
            HttpServletRequest servletRequest
    ) {
        // 인증 정보 내의 유저 정보 가져오기
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User loginUser = userDetails.getUser();

        Member member = memberRepository.findByUserIdAndGroupIdOrElseThrow(loginUser.getId(), groudId);

        HttpSession session = servletRequest.getSession();
        session.setAttribute("member", member);

        return ResponseEntity.ok("그룹 선택 완료");
    }
}
