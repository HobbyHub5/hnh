package com.example.hnh.meetmember;

import com.example.hnh.global.error.errorcode.ErrorCode;
import com.example.hnh.global.error.exception.CustomException;
import com.example.hnh.meet.Meet;
import com.example.hnh.meet.MeetRepository;
import com.example.hnh.meetmember.dto.MeetDetailsResponseDto;
import com.example.hnh.meetmember.dto.MeetMemberResponseDto;
import com.example.hnh.member.Member;
import com.example.hnh.member.MemberRepository;
import com.example.hnh.user.User;
import com.example.hnh.user.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MeetMemberService {

    private final MeetRepository meetRepository;
    private final MemberRepository memberRepository;
    private final MeetMemberRepository meetMemberRepository;
    private final UserRepository userRepository;

    public MeetMemberService(MeetRepository meetRepository, MemberRepository memberRepository, MeetMemberRepository meetMemberRepository, UserRepository userRepository) {
        this.meetRepository = meetRepository;
        this.memberRepository = memberRepository;
        this.meetMemberRepository = meetMemberRepository;
        this.userRepository = userRepository;
    }

    /**
     * 모임 참여 API
     * @param groupId
     * @param meetId
     * @param loginUser
     * @return
     */
    public MeetMemberResponseDto joinMeet(Long groupId, Long meetId, User loginUser) {
        // 모임 조회
        Meet meet = meetRepository.findByMeetOrElseThrow(meetId);

        // 모임이 그룹에 속해 있는지 확인
        if (!meet.getGroup().getId().equals(groupId)) {
            throw new CustomException(ErrorCode.RESOURCES_NOT_FOUND);
        }

        // 모임 상태 확인
        checkMeetStatus(meet);

        // 마감 시간 확인
        if (meet.getDueAt().isBefore(LocalDateTime.now())) {
            throw new CustomException(ErrorCode.MEET_NOT_FOUND);
        }

        // 사용자가 그룹에 속해 있는지 확인
        memberRepository.findByUserIdAndGroupIdOrElseThrow(loginUser.getId(), groupId);

        // 사용자가 이미 모임에 참여했는지 확인
        Optional<MeetMember> existingMember = meetMemberRepository.findByMemberIdAndMeetId(loginUser.getId(), meetId);
        if (existingMember.isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATE_MEET);
        }

        // 모임 멤버 생성 및 저장
        MeetMember meetMember = new MeetMember(loginUser.getId(), meet);
        meetMember = meetMemberRepository.save(meetMember);

        // 응답 DTO 생성
        return new MeetMemberResponseDto(meetMember.getId(), "active");
    }


    /**
     * 모임 멤버 조회 API
     * @param groupId
     * @param meetId
     * @return
     */
    public MeetDetailsResponseDto findMeetMember(Long groupId, User loginUser, Long meetId) {

        // 사용자가 그룹에 속해 있는지 확인
        memberRepository.findByUserIdAndGroupIdOrElseThrow(loginUser.getId(), groupId);

        // 모임 조회
        Meet meet = meetRepository.findByMeetOrElseThrow(meetId);

        // 그룹에 속한 모임인지 확인
        if (!meet.getGroup().getId().equals(groupId)) {
            throw new IllegalArgumentException("모임이 해당 그룹에 속하지 않습니다.");
        }

        // 활성화된 모임 멤버 ID 조회
        List<Long> meetMemberIds = meetMemberRepository.findActiveMemberIdsByMeetId(meetId);

        // 멤버 이름 조회
        List<String> meetMemberNames = userRepository.findNamesByIds(meetMemberIds);

        // DTO 생성 및 반환
        return MeetDetailsResponseDto.toDto(meet, meetMemberNames);
    }


    /**
     * 모임 참여 변경 API
     * @param groupId
     * @param meetId
     * @param loginUser
     * @return
     */
    public String changeMeetMemberStatus(Long groupId, Long meetId, User loginUser) {

        // 사용자가 그룹에 속해 있는지 확인
        memberRepository.findByUserIdAndGroupIdOrElseThrow(loginUser.getId(), groupId);

        // 모임 존재 여부 확인
        Meet meet = meetRepository.findByMeetOrElseThrow(meetId);

        // 그룹에 속한 모임인지 확인
        if (!meet.getGroup().getId().equals(groupId)) {
            throw new IllegalArgumentException("모임이 해당 그룹에 속하지 않습니다.");
        }

        // 모임 멤버 조회
        Optional<MeetMember> findMeetMember = meetMemberRepository.findByMemberIdAndMeetId(loginUser.getId(), meetId);

        if(findMeetMember.isEmpty()) {
            return "먼저 모임 참가를 해주세요.";
        }

        // 상태 토글
        MeetMember meetMember = findMeetMember.get();
        String newStatus = "active".equals(meetMember.getStatus()) ? "deleted" : "active";
        meetMember.setStatus(newStatus); // 상태 변경
        meetMemberRepository.save(meetMember);

        // 응답 메시지 반환
        return "active".equals(newStatus) ? "참여 등록되었습니다." : "참여 취소되었습니다.";
    }



    /**
     * 모임 상태 확인 메서드
     * @param meet
     */
    public void checkMeetStatus(Meet meet) {
        if ("deleted".equals(meet.getStatus())) {
            throw new IllegalArgumentException("이미 삭제된 모임입니다.");
        }
    }
}
