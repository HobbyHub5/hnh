package com.example.hnh.comment;

import com.example.hnh.comment.dto.ReplyResponseDto;
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
public class ReplyService {

    private final ReplyRepository replyRepository;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public ReplyResponseDto addReply(Long userId, Long commentId, String reply) {
        Comment comment = commentRepository.findByCommentIdOrElseThrow(commentId);
        Member member = memberRepository.findByUserIdAndGroupIdOrElseThrow(userId, comment.getBoard().getGroup().getId());

        Reply newReply = new Reply(reply, member.getId(), comment);
        Reply savedReply = replyRepository.save(newReply);

        return new ReplyResponseDto(savedReply.getId(), savedReply.getMemberId(), savedReply.getReply());
    }

    @Transactional
    public ReplyResponseDto updateReply(Long userId, Long replyId, String reply) {
        Reply updateReply = replyRepository.findByReplyIdOrElseThrow(replyId);
        Member member = memberRepository.findByMemberId(updateReply.getMemberId());

        if(!member.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_AUTHOR);
        }

        updateReply.updateReply(reply);

        return new ReplyResponseDto(updateReply.getId(), updateReply.getMemberId(), updateReply.getReply());
    }

    @Transactional
    public MessageResponseDto deleteReply(Long userId, Long replyId) {
        Reply deleteReply = replyRepository.findByReplyIdOrElseThrow(replyId);
        Member member = memberRepository.findByMemberId(deleteReply.getMemberId());

        if(!member.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_AUTHOR);
        }

        deleteReply.setStatus("deleted");

        return new MessageResponseDto("삭제 완료되었습니다.");
    }
}