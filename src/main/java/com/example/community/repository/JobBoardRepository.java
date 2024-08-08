package com.example.community.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.community.dto.check.BoardCategory;
import com.example.community.dto.combine.BoardListDTO;
import com.example.community.entity.JobBoardEntity;

public interface JobBoardRepository extends JpaRepository<JobBoardEntity, Long>{

    // 카테고리에 해당하는 (신고당하지 않은) JobBoardEntities 반환 (최신순)
    @Query("SELECT j FROM JobBoardEntity j " +
            "JOIN j.boardEntity b " +
            "WHERE b.category = :category " +
            "AND LOWER(b.title) LIKE LOWER(CONCAT('%', :searchWord, '%')) " +
            "AND b.reported = false " +
            "ORDER BY b.createDate DESC")
    Page<JobBoardEntity> findByCategoryAndTitleContainingAndReportedIsFalse(@Param("category") BoardCategory category, 
                                                                            @Param("searchWord") String searchWord, 
                                                                            Pageable pageable); 
    
    // 카테고리에 해당하는 (신고당하지 않은) BoardListDTOs 반환 (최신순)
    @Query("SELECT new com.example.community.dto.display.BoardListDTO(b.boardId, b.memberEntity.memberId, b.memberGroup, b.title, b.hitCount, b.likeCount, b.createDate, j.deadline, j.limitNumber, j.currentNumber) " +
            "FROM JobBoardEntity j " +
            "JOIN j.boardEntity b " +
            "WHERE b.category = :category " +
            "AND LOWER(b.title) LIKE LOWER(CONCAT('%', :searchWord, '%')) " +
            "AND b.reported = false " +
            "ORDER BY b.createDate DESC")
    Page<BoardListDTO> findBoardListByCategoryAndTitleContainingAndReportedIsFalse(@Param("category") BoardCategory category, 
                                                                                    @Param("searchWord") String searchWord, 
                                                                                    Pageable pageable);

    
}
