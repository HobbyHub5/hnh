package com.example.hnh.meetmember.dto;

import com.example.hnh.meet.Meet;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class MeetDetailsResponseDto {

    private Long meetId;

    private String meetTitle;

    private String detail;

    private List<String> meetMemberNames;

    private LocalDateTime dueAt;

    private LocalDateTime createdAt;

    private String status;



    public MeetDetailsResponseDto(Long meetId, String meetTitle, String detail, List<String> meetMemberNames,
                                  LocalDateTime dueAt, LocalDateTime createdAt, String status) {
        this.meetId = meetId;
        this.meetTitle = meetTitle;
        this.detail = detail;
        this.meetMemberNames = meetMemberNames;
        this.dueAt = dueAt;
        this.createdAt = createdAt;
        this.status = status;
    }

    public static MeetDetailsResponseDto toDto(Meet meet, List<String> meetMemberNames) {
        return new MeetDetailsResponseDto(
                meet.getId(),
                meet.getTitle(),
                meet.getDetail(),
                meetMemberNames,
                meet.getDueAt(),
                meet.getCreatedAt(),
                meet.getStatus()
        );
    }
}
