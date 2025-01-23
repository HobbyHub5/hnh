package com.example.hnh.group;


import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public class RedisRankingRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisRankingRepository(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // 그룹을 랭킹에 추가
    public void addGroupToRanking(Long groupId, int interestCount) {
        redisTemplate.opsForZSet().add("group_ranking", groupId, interestCount);
    }

    // 그룹의 관심 수 증가 또는 감소
    public void incrementGroupInterest(Long groupId, int delta) {
        redisTemplate.opsForZSet().incrementScore("group_ranking", groupId, delta);
    }

    // 관심 수가 많은 순으로 그룹 ID 조회
    public Set<ZSetOperations.TypedTuple<Object>> getTopRankedGroups() {
        return redisTemplate.opsForZSet().reverseRangeWithScores("group_ranking", 0, -1);
    }
}
