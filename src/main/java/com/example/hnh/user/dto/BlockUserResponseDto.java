package com.example.hnh.user.dto;

import lombok.Getter;

@Getter
public class BlockUserResponseDto {

    private final Long userId;

    private final String status;

    public BlockUserResponseDto(Long userId, String status) {
        this.userId = userId;
        this.status = status;
    }
}
