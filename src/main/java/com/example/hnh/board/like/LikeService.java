package com.example.hnh.board.like;

import com.example.hnh.board.Board;
import com.example.hnh.board.BoardRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final BoardRepository boardRepository;
    private final RedisTemplate<String, String> redisTemplate;

    //좋아요 및 취소 기능
    @Transactional
    public LikeResponseDto toggleLike(Long userId, Long boardId) {

        Optional<Like> likeOptional = likeRepository.findByUserIdAndBoardIdWithStatus(userId, boardId);
        String redisKey = getRedisKey(boardId);

        String likeCount = redisTemplate.opsForValue().get(redisKey);
        if(likeCount == null) {
            Board board = boardRepository.findByBoardIdOrElseThrow(boardId);
            likeCount = board.getLikeCount().toString();
            redisTemplate.opsForValue().set(redisKey, likeCount); // Redis에 초기화
        }

        if(likeOptional.isEmpty()) {
            Like newLike = new Like(userId, boardId);
            likeRepository.save(newLike);

            redisTemplate.opsForValue().increment(redisKey, 1);
        }else{
            Like like = likeOptional.get();
            if(like.getStatus().equals("deleted")) {
                like.setStatus("active");
                likeRepository.save(like);

                redisTemplate.opsForValue().increment(redisKey, 1);
            }else{
                like.setStatus("deleted");
                likeRepository.save(like);

                redisTemplate.opsForValue().decrement(redisKey, 1);
            }
        }

        Long finalLikeCount = Long.parseLong(redisTemplate.opsForValue().get(redisKey));
        return new LikeResponseDto(finalLikeCount);
    }


    private String getRedisKey(Long boardId) {
        return "board:like:" + boardId;
    }

    private String getBoardUserRedisKey(Long boardId, Long userId) {
        return String.format("board:%d:user:%d:like", boardId, userId);
    }
}
