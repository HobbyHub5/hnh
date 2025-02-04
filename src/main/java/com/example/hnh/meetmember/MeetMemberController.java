package com.example.hnh.meetmember;

import com.example.hnh.global.config.auth.UserDetailsImpl;
import com.example.hnh.meetmember.dto.MeetDetailsResponseDto;
import com.example.hnh.meetmember.dto.MeetMemberResponseDto;
import com.example.hnh.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/groups")
public class MeetMemberController {

    private final MeetMemberService meetMemberService;

    public MeetMemberController (MeetMemberService meetMemberService) {
        this.meetMemberService = meetMemberService;
    }

    /**
     * 모임 참여 API
     * @param groupId
     * @param meetId
     * @param userDetails
     * @return
     */
    @PostMapping("/{groupId}/meets/{meetId}/join")
    public ResponseEntity<MeetMemberResponseDto> joinMeet(
            @PathVariable Long groupId,
            @PathVariable Long meetId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        User loginUser = userDetails.getUser();
        MeetMemberResponseDto responseDto = meetMemberService.joinMeet(groupId, meetId, loginUser);

        return ResponseEntity.ok(responseDto);
    }

    /**
     * 모임 멤버 조회 API
     * @param groupId
     * @param meetId
     * @return
     */
    @GetMapping("/{groupId}/meets/{meetId}")
    public ResponseEntity<MeetDetailsResponseDto> findMeetMember(
            @PathVariable Long groupId,
            @PathVariable Long meetId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        User loginUser = userDetails.getUser();

        MeetDetailsResponseDto responseDto = meetMemberService.findMeetMember(groupId, loginUser ,meetId);
        return ResponseEntity.ok(responseDto);
    }

    /**
     * 모임 참여 변경 API
     * @param groupId
     * @param meetId
     * @param userDetails
     * @return
     */
    @PatchMapping("/{groupId}/meets/{meetId}/participants")
    public ResponseEntity<String> changeMeetMemberStatus (
            @PathVariable Long groupId,
            @PathVariable Long meetId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        User loginUser = userDetails.getUser();
        String message = meetMemberService.changeMeetMemberStatus(groupId, meetId, loginUser);

        return ResponseEntity.ok(message);
    }
}
