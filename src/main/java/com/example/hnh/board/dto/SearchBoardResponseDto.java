package com.example.hnh.board.dto;

import com.example.hnh.board.Board;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class SearchBoardResponseDto {

    private final Long boardId;

    private final Long memberId;

    private final String title;

    private final String detail;

    private final String imagePath;

    private final Long view;

    private final Long likeCount;

    private final LocalDateTime createdAt;

    private final LocalDateTime modifiedAt;

    private final List<CommentDto> comments;

    public SearchBoardResponseDto(Board board, Long viewCount) {
        this.boardId = board.getId();
        this.memberId = board.getMember().getId();
        this.title = board.getTitle();
        this.detail = board.getDetail();
        this.imagePath = board.getImagePath();
        this.view = viewCount;
        this.likeCount = board.getLikeCount();
        this.createdAt = board.getCreatedAt();
        this.modifiedAt = board.getModifiedAt();
        this.comments = board.getComments().stream()
                .filter(comment -> "active".equals(comment.getStatus()))
                .map(CommentDto::new).collect(Collectors.toList());
    }
}
