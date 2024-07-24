package com.example.community.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.community.dto.check.BoardCategory;
import com.example.community.entity.JobBoardEntity;

public interface JobBoardRepository extends JpaRepository<JobBoardEntity, Long>{
    
    // 카테고리에 해당하는 게시글 리스트 반환 (최신순)
    @Query("SELECT j FROM JobBoardEntity j WHERE " +
                        "j.category =: category " +
                        "ORDER BY j.createDate DESC")
    List<JobBoardEntity> findByCategoryOrderByCreateDateDesc(@Param("category") BoardCategory category);
}
