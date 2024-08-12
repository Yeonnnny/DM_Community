package com.example.community.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.community.entity.BoardEntity;
import com.example.community.entity.ReplyEntity;

public interface ReplyRepository extends JpaRepository<ReplyEntity,Long>{

    // boardEntity에 해당하는 댓글의 개수를 반환하는 메서드
    @Query("SELECT COUNT(r) FROM ReplyEntity r WHERE r.boardEntity = :boardEntity")
    long countByBoardEntity(@Param("boardEntity") BoardEntity boardEntity);

}
