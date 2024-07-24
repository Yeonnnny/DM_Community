package com.example.community.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.community.entity.JobBoardRecruitEntity;

public interface JobBoardRecruitRepository extends JpaRepository<JobBoardRecruitEntity,Long> {
    
}
