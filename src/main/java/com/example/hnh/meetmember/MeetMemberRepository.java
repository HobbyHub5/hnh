package com.example.hnh.meetmember;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MeetMemberRepository extends JpaRepository<MeetMember, Long> {

    Optional<MeetMember> findByMemberIdAndMeetId(Long id, Long meetId);

    // 특정 모임의 활성화된 멤버 ID 조회
    @Query("SELECT m.memberId FROM MeetMember m WHERE m.meet.id = :meetId AND m.status = 'active'")
    List<Long> findActiveMemberIdsByMeetId(@Param("meetId") Long meetId);

}
