package com.example.hnh.board.like;

import lombok.Getter;

@Getter
public class LikeResponseDto {

    private final Long likeCount;

    public LikeResponseDto(Long likeCount) {
        this.likeCount = likeCount;
    }
}
