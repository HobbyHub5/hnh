package com.example.hnh.user;

import com.example.hnh.global.error.errorcode.ErrorCode;
import com.example.hnh.global.error.exception.CustomException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {


    default User findByIdOrElseThrow(Long id){
        return findById(id).
                orElseThrow(()->
                        new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    default User findByEmailOrElseThrow(String email){
        return findByEmail(email).
                orElseThrow(()->
                        new CustomException(ErrorCode.USER_NOT_FOUND));
    }


    Optional<User> findByEmail(String email);


    // 주어진 ID 리스트(memberIds)에 해당하는 사용자 이름(name)을 조회
    @Query("SELECT u.name FROM User u WHERE u.id IN :memberIds")
    List<String> findNamesByIds(@Param("memberIds") List<Long> memberIds);
}
