package com.example.community.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.community.entity.BoardReportEntity;

public interface BoardReportRepository extends JpaRepository<BoardReportEntity,Long> {
    
}
