package com.example.hnh.meetmember.dto;

import lombok.Getter;

@Getter
public class MeetMemberResponseDto {

    private Long meetMemberId;

    private String status;


    public MeetMemberResponseDto(Long meetMemberId, String status) {
        this.meetMemberId = meetMemberId;
        this.status = status;
    }
}
