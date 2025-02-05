package com.example.hnh.member;

import com.example.hnh.global.error.errorcode.ErrorCode;
import com.example.hnh.global.error.exception.CustomException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    //그룹내 모든 유저를 찾는 쿼리
    @Query("SELECT m FROM Member m WHERE m.group.id = :groupId")
    List<Member> findByGroupId(Long groupId);

    //유저 id와 그룹 id로 멤버를 찾는 쿼리
    @Query("SELECT m FROM Member m JOIN FETCH m.user JOIN FETCH m.group WHERE m.user.id = :userId AND m.group.id = :groupId")
    Optional<Member> findByUserIdAndGroupId(Long userId, Long groupId);

    //유저 id와 그룹 id로 멤버를 찾는 쿼리(status가 active인 유저)
    @Query("SELECT m FROM Member m JOIN FETCH m.user JOIN FETCH m.group WHERE m.user.id = :userId AND m.group.id = :groupId AND m.status = 'active'")
    Optional<Member> findByUserIdAndGroupIdWithStatus(Long userId, Long groupId);

    default Member findByUserIdAndGroupIdOrElseThrow(Long userId, Long groupId) {
        System.out.println("userId : " + userId + "  /group : " + groupId );
        return findByUserIdAndGroupIdWithStatus(userId, groupId).orElseThrow(
                () -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    }

    default Member findByMemberId(Long memberId) {
        return findById(memberId).orElseThrow(
                () -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    }
}
