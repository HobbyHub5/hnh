package com.example.hnh.group;

import com.example.hnh.global.error.errorcode.ErrorCode;
import com.example.hnh.global.error.exception.CustomException;
import com.example.hnh.user.dto.DashboardResponseDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface GroupRepository extends JpaRepository<Group, Long> {

    boolean existsByName(String name);

    default Group findByGroupOrElseThrow(Long groupId) {
        return findById(groupId).orElseThrow(
                () -> new CustomException(ErrorCode.GROUP_NOT_FOUND)
        );
    }

    /**
     * 관리자 통계 쿼리
     *
     * @param start     조회 기간 시작 날짜
     * @param end       조회 기간 마지막 날짜
     * @param groupName 조회 그룹 이름
     * @return
     */
    @Query(value = "SELECT " +
            "COUNT(m.id) AS memberCount, "+
            "COUNT(b.id) AS boardCount, "+
            "COUNT(i.id) As interestCount," +
            "COUNT(meet.id) AS meetCount," +
            "g.name As groupName " +
            "FROM `group` g " +
            "LEFT JOIN board b ON g.id = b.group_id " +
            "LEFT JOIN interest_group i ON g.id = i.group_id " +
            "LEFT JOIN meet meet ON g.id = meet.group_id " +
            "LEFT JOIN member m ON g.id = m.group_id " +
            "WHERE g.name = :groupName AND g.created_at BETWEEN :startDate AND :endDate", nativeQuery = true)
    DashboardResponseDto findStatsByName(@Param("startDate") LocalDateTime start,
                                         @Param("endDate") LocalDateTime end,
                                         @Param("groupName") String groupName);


    // 그룹 정보와 관련된 데이터 조회 (User와 조인)
    @Query("SELECT g FROM Group g JOIN User u ON g.userId = u.id")
    List<Group> findAllGroupsWithUser();


    @Query("SELECT g FROM Group g " +
            "LEFT JOIN InterestGroup ig ON g.id = ig.group.id " +
            "WHERE g.status = 'active' " +
            "GROUP BY g.id " +
            "ORDER BY COUNT(ig.id) DESC")
    List<Group> findAllGroupsOrderByInterestCount();

}
