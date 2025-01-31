package com.example.hnh.meet;

import com.example.hnh.global.config.annotation.AccessibleMember;
import com.example.hnh.global.config.auth.UserDetailsImpl;
import com.example.hnh.meet.dto.MeetRequestDto;
import com.example.hnh.meet.dto.MeetResponseDto;
import com.example.hnh.meet.dto.MeetUpdateRequestDto;
import com.example.hnh.member.MemberRole;
import com.example.hnh.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/groups")
public class MeetController {

    private final MeetService meetService;

    public MeetController(MeetService meetService) {
        this.meetService = meetService;
    }

    /**
     * 모임 생성 API
     * @param groupId
     * @param requestDto
     * @param userDetails
     * @return
     */
    @PostMapping("/{groupId}/meets")
    public ResponseEntity<MeetResponseDto> createMeet(
            @PathVariable Long groupId,
            @RequestBody MeetRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        User loginUser = userDetails.getUser();
        MeetResponseDto responseDto = meetService.createMeet(groupId, loginUser, requestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    /**
     * 해당 그룹 모든 모임 조회 API
     * @param groupId
     * @return
     */
    @GetMapping("/{groupId}/meets")
    public ResponseEntity<List<MeetResponseDto>> findMeets(@PathVariable Long groupId) {
        List<MeetResponseDto> meets = meetService.findMeets(groupId);
        return ResponseEntity.ok(meets);
    }

    /**
     * 모임 수정 API
     * @param groupId
     * @param meetId
     * @param requestDto
     * @param userDetails
     * @return
     */
    @AccessibleMember(requiredRoles = {MemberRole.GROUP_ADMIN, MemberRole.MEMBER})
    @PatchMapping("/{groupId}/meets/{meetId}")
    public ResponseEntity<MeetResponseDto> updateMeet(
            @PathVariable Long groupId,
            @PathVariable Long meetId,
            @RequestBody MeetUpdateRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        User loginUser = userDetails.getUser();
        MeetResponseDto responseDto = meetService.updateMeet(groupId, meetId, loginUser, requestDto);

        return ResponseEntity.ok(responseDto);
    }

    /**
     * 모임 삭제 API
     * @param groupId
     * @param meetId
     * @param userDetails
     * @return
     */
    @AccessibleMember(requiredRoles = {MemberRole.GROUP_ADMIN, MemberRole.MEMBER})
    @DeleteMapping("/{groupId}/meets/{meetId}")
    public ResponseEntity<String> deleteMeet(@PathVariable Long groupId,
                                             @PathVariable Long meetId,
                                             @AuthenticationPrincipal UserDetailsImpl userDetails) {

        User loginUser = userDetails.getUser();

        meetService.deleteMeet(groupId, meetId, loginUser);

        return new ResponseEntity<>("모임이 삭제되었습니다.", HttpStatus.OK);
    }
}
