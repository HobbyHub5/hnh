package com.example.hnh.interestgroup;

import com.example.hnh.group.Group;
import com.example.hnh.group.GroupRepository;
import com.example.hnh.group.RedisRankingRepository;
import com.example.hnh.interestgroup.dto.InterestGroupResponseDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class InterestGroupService {

    private final InterestGroupRepository interestGroupRepository;
    private final GroupRepository groupRepository;
    private final RedisRankingRepository redisRankingRepository;

    public InterestGroupService(InterestGroupRepository interestGroupRepository,
                                GroupRepository groupRepository, RedisRankingRepository redisRankingRepository) {
        this.interestGroupRepository = interestGroupRepository;
        this.groupRepository = groupRepository;
        this.redisRankingRepository = redisRankingRepository;
    }

    /**
     * 관심 그룹 찜하기 API
     * @param groupId
     * @param userId
     * @return
     */
    public String toggleInterest(Long groupId, Long userId) {

        // 그룹 존재 여부 확인
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 그룹입니다."));

        // 관심 그룹 조회
        Optional<InterestGroup> existingInterest = interestGroupRepository.findByGroupIdAndUserId(groupId, userId);

        if (existingInterest.isEmpty()) {
            // 관심 그룹이 없으면 새로 생성
            InterestGroup interestGroup = new InterestGroup();
            interestGroup.setGroup(group);
            interestGroup.setUserId(userId);
            interestGroupRepository.save(interestGroup);
            redisRankingRepository.incrementGroupInterest(groupId, 1);
            return "관심 그룹으로 등록하셨습니다.";
        }

        // 관심 그룹이 존재하면 상태값 토글
        InterestGroup interestGroup = existingInterest.get();

        String newStatus = "";

        if ("active".equals(interestGroup.getStatus())) {
            // 상태를 "deleted"로 변경
            newStatus = "deleted";
            redisRankingRepository.incrementGroupInterest(groupId, -1);
        }

        if ("deleted".equals(interestGroup.getStatus())) {
            // 상태를 "active"로 변경
            newStatus = "active";
            redisRankingRepository.incrementGroupInterest(groupId, 1);
        }

        interestGroup.setStatus(newStatus);
        interestGroupRepository.save(interestGroup);

        return "active".equals(newStatus) ? "관심 그룹으로 등록하셨습니다." : "관심 그룹을 취소하셨습니다.";

    }

    /**
     * 관심 그룹 목록 조회 API
     * @param userId
     * @return
     */
    public List<InterestGroupResponseDto> getUserInterestGroups(Long userId) {
        // 좋아요 누른 그룹 목록 조회
        List<InterestGroup> interestGroups = interestGroupRepository.findByUserIdAndStatus(userId, "active");

        // DTO 리스트 생성 및 변환
        List<InterestGroupResponseDto> responseDtoList = new ArrayList<>();
        for (InterestGroup interestGroup : interestGroups) {
            Group group = interestGroup.getGroup();
            InterestGroupResponseDto dto = InterestGroupResponseDto.toDto(group);
            responseDtoList.add(dto);
        }

        return responseDtoList;
    }
}
