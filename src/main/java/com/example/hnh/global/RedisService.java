package com.example.hnh.global;

import com.example.hnh.user.dto.DashboardResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final StringRedisTemplate redisTemplate;

    /**
     * 그룹 통계 저장 로직
     *
     * @param stats     그룹 통계 정보
     * @param startDate 조회 기간 시작 날짜
     * @param endDate   조회 기간 마지막 날짜
     */
    public void saveGroupStats(
            DashboardResponseDto stats, String startDate, String endDate
    ) {

        // 그룹별 통계 키
        String key = "group:" + stats.getGroupName() + ":stats:" + startDate + ":" + endDate;

        HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();

        // 필드와 값 설정
        hashOps.put(key, "memberCount", String.valueOf(stats.getMemberCount()));
        hashOps.put(key, "boardCount", String.valueOf(stats.getBoardCount()));
        hashOps.put(key, "interestCount", String.valueOf(stats.getInterestCount()));
        hashOps.put(key, "meetCount", String.valueOf(stats.getMeetCount()));

        // 만료시간 24시간 설정
        redisTemplate.expire(key, 24, TimeUnit.HOURS);
    }

    /**
     * 그룹 통계 조회 로직
     *
     * @param groupName 그룹 이름
     * @param startDate 조회 기간 시작 날짜
     * @param endDate   조회 기간 마지막 날짜
     * @return
     */
    public DashboardResponseDto findGroupStats(String groupName, String startDate, String endDate) {

        // 그룹별 통계 키
        String key = "group:" + groupName + ":stats:" + startDate + ":" + endDate;

        HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();

        // Redis 에 해당 데이터가 없으면 null 반환
        if (!redisTemplate.hasKey(key)) {
            return null;
        }

        // Redis 에서 통계값을 가져옴
        String memberCount = hashOps.get(key, "memberCount");
        String boardCount = hashOps.get(key, "boardCount");
        String interestCount = hashOps.get(key, "interestCount");
        String meetCount = hashOps.get(key, "meetCount");

        // DashboardResponseDto 로 반환
        return new DashboardResponseDto(
                Long.parseLong(memberCount),
                Long.parseLong(boardCount),
                Long.parseLong(interestCount),
                Long.parseLong(meetCount),
                groupName
        );
    }
}
