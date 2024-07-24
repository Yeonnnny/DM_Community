package com.example.community.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.community.entity.JobBoardReportedEntity;

public interface JobBoardReportedRepository extends JpaRepository<JobBoardReportedEntity,Long> {
    
}
