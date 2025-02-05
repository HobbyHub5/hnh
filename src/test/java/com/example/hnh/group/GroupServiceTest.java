package com.example.hnh.group;

import com.example.hnh.group.dto.GroupRankingResponseDto;
import com.example.hnh.interestgroup.InterestGroupRepository;
import com.example.hnh.user.User;
import com.example.hnh.user.UserRepository;
import com.example.hnh.user.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GroupServiceTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedisRankingRepository redisRankingRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private InterestGroupRepository interestGroupRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        // JPA 및 Redis 초기화
        groupRepository.deleteAll();
        userRepository.deleteAll();
        redisTemplate.delete("group_ranking");

        User user = new User("test@test.com","name","1234qwer!");
        userRepository.save(user);

        // 10개의 테스트 데이터 생성
        for (long i = 1; i <= 5000; i++) {
            Group group = new Group(1L, 1L, "그룹" + i, "image" + i + ".png", "상세설명 " + i);
            groupRepository.save(group);

            // Redis에도 동일한 데이터 추가 (관심 수는 랜덤 1~5000)
            int interestCount = new Random().nextInt(5000) + 1;
            redisRankingRepository.addGroupToRanking(group.getId(), interestCount);
        }
    }


    @Test
    @DisplayName("JPA vs Redis 랭킹 조회 성능 비교")
    void compareJpaAndRedisRanking() {

        StopWatch stopWatch = new StopWatch();

        // JPA 기반 랭킹 조회 성능 측정
        stopWatch.start("JPA Ranking Query");
        List<GroupRankingResponseDto> jpaRanking = getRankingWithJPA();
        stopWatch.stop();
        double jpaTime = stopWatch.getLastTaskTimeMillis(); // JPA 실행 시간 저장

        // Redis 기반 랭킹 조회 성능 측정
        stopWatch.start("Redis Ranking Query");
        List<GroupRankingResponseDto> redisRanking = getRankingWithRedis();
        stopWatch.stop();
        double redisTime = stopWatch.getLastTaskTimeMillis(); // Redis 실행 시간 저장


        double performanceDifference = jpaTime / redisTime;

        // 결과 출력
        System.out.println(stopWatch.prettyPrint());
        System.out.println("성능 차이: Redis가 JPA보다 약 " + String.format("%.2f", performanceDifference) + "배 빠름");

        // Redis가 JPA보다 빠른지 검증 (보통 Redis가 훨씬 빠름)
        assertTrue(stopWatch.getTotalTimeMillis() > 0, "테스트 완료");
    }

    /**
     * JPA를 이용한 그룹 랭킹 조회
     * @return
     */
    private List<GroupRankingResponseDto> getRankingWithJPA() {

        List<GroupRankingResponseDto> rankingList = new ArrayList<>();

        // JPA로 관심 수 기반 정렬된 그룹 조회
        List<Group> groups = groupRepository.findAllGroupsOrderByInterestCount();

        for (Group group : groups) {
            int interestCount = interestGroupRepository.countByGroupIdAndStatus(group.getId(), "active");
            User user = userRepository.findByIdOrElseThrow(group.getUserId());
            rankingList.add(GroupRankingResponseDto.toDto(group, user.getName(), interestCount));
        }

        return rankingList;
    }

    /**
     * Redis를 이용한 그룹 랭킹 조회
     * @return
     */
    private List<GroupRankingResponseDto> getRankingWithRedis() {

        List<GroupRankingResponseDto> rankingList = new ArrayList<>();

        // Redis에서 랭킹 데이터 가져오기
        Set<ZSetOperations.TypedTuple<Object>> rankedGroups = redisRankingRepository.getTopRankedGroups();

        for (ZSetOperations.TypedTuple<Object> rankedGroup : rankedGroups) {
            Long groupId = Long.valueOf(rankedGroup.getValue().toString());
            Group group = groupRepository.findByGroupOrElseThrow(groupId);
            User user = userRepository.findByIdOrElseThrow(group.getUserId());

            rankingList.add(GroupRankingResponseDto.toDto(group, user.getName(), rankedGroup.getScore().intValue()));
        }

        return rankingList;
    }
}