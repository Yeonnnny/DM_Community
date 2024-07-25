package com.example.community.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.community.dto.check.BoardCategory;
import com.example.community.entity.JobBoardEntity;

public interface JobBoardRepository extends JpaRepository<JobBoardEntity, Long>{
    
    @Query("SELECT j FROM JobBoardEntity j WHERE " +
    "j.category =: category AND " +
    "j.reported = false "+
    "ORDER BY j.createDate DESC")
    List<JobBoardEntity> findByCategoryOrderByCreateDateDesc(@Param("category") BoardCategory category);
    
    // 카테고리에 해당하는 (신고당하지 않은) 게시글 리스트 반환 (최신순)
    @Query("SELECT j FROM JobBoardEntity j WHERE " +
    "j.category = :category AND " +
    "j.reported = false AND " +
    "LOWER(j.title) LIKE LOWER(CONCAT('%', :searchWord, '%'))")
    Page<JobBoardEntity> findByCategoryTitleContainingAndReportedIsFalse(@Param("category")BoardCategory category, 
        @Param("searchWord") String searchWord, Pageable pageRequest);
}
