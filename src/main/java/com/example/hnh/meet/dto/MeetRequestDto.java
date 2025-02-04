package com.example.hnh.meet.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MeetRequestDto {

    @NotNull
    private String meetTitle;

    @NotNull
    private String detail;

    @NotNull
    private LocalDateTime dueAt;



    public MeetRequestDto(String meetTitle, String detail, LocalDateTime dueAt) {
        this.meetTitle = meetTitle;
        this.detail = detail;
        this.dueAt = dueAt;
    }
}
