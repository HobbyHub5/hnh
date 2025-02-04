package com.example.hnh.meet.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MeetUpdateRequestDto {

    private String meetTitle;

    private String detail;

    private LocalDateTime dueAt;



    public MeetUpdateRequestDto(String meetTitle, String detail, LocalDateTime dueAt) {
        this.meetTitle = meetTitle;
        this.detail = detail;
        this.dueAt = dueAt;
    }
}
