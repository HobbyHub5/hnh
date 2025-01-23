package com.example.hnh.interestgroup;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InterestGroupRepository extends JpaRepository<InterestGroup ,Long> {

    Optional<InterestGroup> findByGroupIdAndUserId(Long groupId, Long userId);

    List<InterestGroup> findByUserIdAndStatus(Long userId, String active);

    // 특정 그룹의 활성 상태(active)인 관심 그룹 수를 카운트
    int countByGroupIdAndStatus(Long groupId, String status);
}
