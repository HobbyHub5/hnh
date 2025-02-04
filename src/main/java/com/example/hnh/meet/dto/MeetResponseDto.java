package com.example.hnh.meet.dto;

import com.example.hnh.meet.Meet;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MeetResponseDto {

    private Long groupId; // 그룹 식별자 ID

    private Long meetId; // 모임 식별자 ID

    private Long memberId; // 모임 생성자 ID

    private String meetTitle; // 모임 이름

    private String detail; // 모임 설명

    private LocalDateTime dueAt; // 모임 신청 마감일

    private LocalDateTime createdAt; // 모임 생성일



    public MeetResponseDto(Long groupId, Long meetId, Long memberId, String meetTitle, String detail,
                           LocalDateTime dueAt, LocalDateTime createdAt) {
        this.groupId = groupId;
        this.meetId = meetId;
        this.memberId = memberId;
        this.meetTitle = meetTitle;
        this.detail = detail;
        this.dueAt = dueAt;
        this.createdAt = createdAt;
    }

    public static MeetResponseDto toDto(Meet meet)  {
        return new MeetResponseDto(
                meet.getGroup().getId(),
                meet.getId(),
                meet.getMemberId(),
                meet.getTitle(),
                meet.getDetail(),
                meet.getDueAt(),
                meet.getCreatedAt()
        );
    }
}
