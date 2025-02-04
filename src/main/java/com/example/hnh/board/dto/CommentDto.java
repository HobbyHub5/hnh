package com.example.hnh.board.dto;

import com.example.hnh.comment.Comment;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class CommentDto {

    private final Long authorId;

    private final String comment;

    private final List<ReplyDto> replies;

    public CommentDto(Comment comment) {
        authorId = comment.getMemberId();
        this.comment = comment.getComment();
        this.replies = comment.getReplies().stream()
                .filter(reply -> "active".equals(reply.getStatus()))
                .map(ReplyDto::new).collect(Collectors.toList());

    }
}
