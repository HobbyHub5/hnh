package com.example.hnh.meet;


import com.example.hnh.global.error.errorcode.ErrorCode;
import com.example.hnh.global.error.exception.CustomException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MeetRepository extends JpaRepository<Meet, Long> {

    // 그룹 ID 기준으로 모든 모임 조회
    List<Meet> findAllByGroupId(Long groupId);


    default Meet findByMeetOrElseThrow (Long meetId) {
        return findById(meetId).orElseThrow(
                () -> new CustomException(ErrorCode.MEET_NOT_FOUND)
        );
    }

}
