package com.example.hnh.comment;

import com.example.hnh.comment.dto.CommentRequestDto;
import com.example.hnh.comment.dto.CommentResponseDto;
import com.example.hnh.global.MessageResponseDto;
import com.example.hnh.global.config.auth.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponseDto> addComment(
            @RequestBody CommentRequestDto dto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Long userId = userDetails.getUser().getId();

        CommentResponseDto commentResponseDto = commentService.addComment(userId, dto.getBoardId(), dto.getComment());

        return new ResponseEntity<>(commentResponseDto, HttpStatus.CREATED);
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentResponseDto> updateComment(
            @PathVariable Long commentId,
            @RequestBody CommentRequestDto dto,
            @AuthenticationPrincipal UserDetailsImpl userDetails){

        Long userId = userDetails.getUser().getId();

        CommentResponseDto commentResponseDto = commentService.updateComment(userId, commentId, dto.getComment());

        return new ResponseEntity<>(commentResponseDto, HttpStatus.OK);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<MessageResponseDto> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Long userId = userDetails.getUser().getId();

        MessageResponseDto messageResponseDto = commentService.deleteComment(userId, commentId);

        return new ResponseEntity<>(messageResponseDto, HttpStatus.OK);
    }
}
