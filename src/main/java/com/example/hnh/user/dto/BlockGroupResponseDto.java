package com.example.hnh.user.dto;

import lombok.Getter;

@Getter
public class BlockGroupResponseDto {

    private final Long groupId;

    private final String status;

    public BlockGroupResponseDto(Long groupId, String status) {
        this.groupId = groupId;
        this.status = status;
    }
}
