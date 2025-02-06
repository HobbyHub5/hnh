package com.example.hnh.comment;

import com.example.hnh.board.Board;
import com.example.hnh.board.BoardRepository;
import com.example.hnh.comment.dto.CommentResponseDto;
import com.example.hnh.global.MessageResponseDto;
import com.example.hnh.global.error.errorcode.ErrorCode;
import com.example.hnh.global.error.exception.CustomException;
import com.example.hnh.member.Member;
import com.example.hnh.member.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;

    @Transactional
    public CommentResponseDto addComment(Long userId, Long boardId, String comment) {
        Board board = boardRepository.findByBoardIdOrElseThrow(boardId);
        Member member = memberRepository.findByUserIdAndGroupIdOrElseThrow(userId, board.getGroup().getId());

        Comment newComment = new Comment(member.getId(), comment, board);
        Comment savedComment = commentRepository.save(newComment);

        return new CommentResponseDto(savedComment.getId(), savedComment.getMemberId(), savedComment.getComment());
    }

    @Transactional
    public CommentResponseDto updateComment(Long userId, Long commentId, String comment) {
        Comment updateComment = commentRepository.findByCommentIdOrElseThrow(commentId);
        Member member = memberRepository.findByMemberId(updateComment.getMemberId());

        if(!member.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_AUTHOR);
        }

        updateComment.updateComment(comment);

        return new CommentResponseDto(updateComment.getId(), updateComment.getMemberId(), updateComment.getComment());
    }

    @Transactional
    public MessageResponseDto deleteComment(Long userId, Long commentId) {
        Comment deleteComment = commentRepository.findByCommentIdOrElseThrow(commentId);
        Member member = memberRepository.findByMemberId(deleteComment.getMemberId());

        if(!member.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_AUTHOR);
        }

        deleteComment.setStatus("deleted");

        return new MessageResponseDto("삭제가 완료되었습니다.");
    }
}
