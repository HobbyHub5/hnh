package com.example.hnh.user.dto;

import lombok.Getter;
import software.amazon.awssdk.annotations.NotNull;

@Getter
public class BlockGroupRequestDto {

    @NotNull
    private Long groupId;

    @NotNull
    private String status;

    public BlockGroupRequestDto(Long groupId, String status) {
        this.groupId = groupId;
        this.status = status;
    }
}
