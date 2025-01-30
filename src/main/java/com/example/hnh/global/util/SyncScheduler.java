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

    @Scheduled(fixedRate = 86_400_000)   //24시간마다 레디스와 DB 동기화(이후 레디스 키 삭제)
    @Transactional
    public void syncLikesToDB() {
        Set<String> keys = redisTemplate.keys("board:like:*");

        if(keys.isEmpty()){
            return;
        }

        for(String redisKey : keys){
            Long boardId = boardIdFromKey(redisKey);

            Long likeCount = Long.parseLong(redisTemplate.opsForValue().get(redisKey));
            if(likeCount == null){
                likeCount = 0L;
            }

            Board board = boardRepository.findByBoardIdOrElseThrow(boardId);
            board.setLikeCount(likeCount);
            boardRepository.save(board);

            redisTemplate.delete(redisKey);
        }
    }


    private Long boardIdFromKey(String redisKey) {
        return Long.parseLong(redisKey.split(":")[2]);
    }
}
