package com.example.community.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.community.entity.BoardEntity;
import com.example.community.entity.LikeEntity;
import com.example.community.entity.MemberEntity;

public interface LikeRepository extends JpaRepository<LikeEntity, Long>{

    // boardId에 해당하는 좋아요 수를 조회하는 쿼리
    @Query("SELECT COUNT(l) FROM LikeEntity l WHERE l.boardEntity = :boardEntity")
    long countByBoardEntity(@Param("boardEntity") BoardEntity boardEntity);

    // 특정 게시글에서 특정 회원이 좋아요를 눌렀는지 확인하는 쿼리
    @Query("SELECT l FROM LikeEntity l "+
            "WHERE l.memberEntity = :memberEntity AND l.boardEntity = :boardEntity")
    Optional<LikeEntity> findByMemberAndBoard(@Param("memberEntity") MemberEntity memberEntity, @Param("boardEntity") BoardEntity boardEntity);


}
