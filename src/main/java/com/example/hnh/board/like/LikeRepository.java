package com.example.hnh.board.like;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    //유저 id와 게시물 id로 좋아요를 찾는 쿼리
    @Query("SELECT l FROM Like l WHERE l.userId = :userId AND l.boardId = :boardId")
    Optional<Like> findByUserIdAndBoardIdWithStatus(@Param("userId") Long userId, @Param("boardId") Long boardId);
}
