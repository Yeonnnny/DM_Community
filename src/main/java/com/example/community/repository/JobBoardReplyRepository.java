package com.example.community.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.community.entity.JobBoardReplyEntity;

public interface JobBoardReplyRepository extends JpaRepository<JobBoardReplyEntity,Long>{
    
}
