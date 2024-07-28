package com.example.community.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.community.entity.ReplyEntity;

public interface ReplyRepository extends JpaRepository<ReplyEntity,Long>{
    
}
