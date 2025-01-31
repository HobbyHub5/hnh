package com.example.hnh.global.util;

import com.example.hnh.board.Board;
import com.example.hnh.board.BoardRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class SyncScheduler {

    private final BoardRepository boardRepository;
    private final RedisTemplate<String, String> redisTemplate;

    //좋아요 동기화(24시간마다 동기화)
    @Transactional
    @Scheduled(fixedRate = 86_400_000)
    public void syncLikesToDatabase() {
        Set<String> keys = redisTemplate.keys("board:like:*");

        if(keys != null) {
            for(String redisKey : keys) {
                Long boardId = Long.parseLong(redisKey.split(":")[2]);
                String likeCount = redisTemplate.opsForValue().get(redisKey);

                if(likeCount != null) {
                    Board board = boardRepository.findByBoardIdOrElseThrow(boardId);
                    board.setLikeCount(Long.parseLong(likeCount));
                    boardRepository.save(board);
                }
            }

            redisTemplate.delete(keys);
        }
    }

    //조회수 동기화(24시간마다 동기화)
    @Transactional
    @Scheduled(fixedRate = 86_400_000)
    public void syncViewToDatabase() {
        Set<String> keys = redisTemplate.keys("board:view:*");

        if(keys != null){
            for(String redisKey : keys){
                Long boardId = Long.parseLong(redisKey.split(":")[2]);
                String viewCount = redisTemplate.opsForValue().get(redisKey);

                if(viewCount != null) {
                    Board board = boardRepository.findByBoardIdOrElseThrow(boardId);
                    board.setView(Long.parseLong(viewCount));
                    boardRepository.save(board);
                }
            }

            redisTemplate.delete(keys);
        }
    }
}
