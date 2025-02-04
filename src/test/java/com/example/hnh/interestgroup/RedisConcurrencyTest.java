package com.example.hnh.interestgroup;

import com.example.hnh.group.RedisRankingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Redis 동시성 제어 테스트")
class RedisConcurrencyTest {

    @Autowired
    private RedisRankingRepository redisRankingRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private final Long testGroupId = 1L; // 테스트용 그룹 ID

    @BeforeEach
    void setup() {
        // Redis에서 모든 관련 키 초기화
        redisTemplate.delete("group_ranking");
        // 테스트 전 Redis 초기화
        redisRankingRepository.addGroupToRanking(testGroupId, 0); // 초기 관심 수를 0으로 설정
    }


    @Test
    @DisplayName("Redis 동시성 테스트 - 1000개의 증가 요청")
    void redisConcurrencyTest() {
        System.out.println("[Redis 동시성 테스트 시작]");

        // 1000개의 병렬 요청을 실행
        IntStream.range(0, 1000).parallel().forEach(i ->
                redisRankingRepository.incrementGroupInterest(testGroupId, 1)
        );

        // 최종 관심 수 확인
        Double finalInterestCount = redisRankingRepository.getTopRankedGroups().stream()
                .filter(tuple -> Long.valueOf(tuple.getValue().toString()).equals(testGroupId))
                .map(ZSetOperations.TypedTuple::getScore)
                .findFirst()
                .orElse(0.0);

        // 결과 출력
        System.out.println("최종 관심 수: " + finalInterestCount);

        // 기대값 검증
        assertEquals(1000.0, finalInterestCount, "최종 관심 수가 1000이 아니면 실패");
    }
}
