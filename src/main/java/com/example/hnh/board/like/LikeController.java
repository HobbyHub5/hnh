package com.example.hnh.board.like;

import com.example.hnh.global.config.auth.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/groups/{groupId}/boards/{boardId}/like")
public class LikeController {

    private final LikeService likeService;

    @PatchMapping
    public ResponseEntity<LikeResponseDto> toggleLike(
            @PathVariable("boardId") Long boardId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Long userId = userDetails.getUser().getId();

        LikeResponseDto likeResponseDto = likeService.toggleLike(userId, boardId);

        return new ResponseEntity<>(likeResponseDto, HttpStatus.OK);
    }
}
