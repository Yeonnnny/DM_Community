package com.example.community.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.community.entity.BoardEntity;

public interface BoardRepository extends JpaRepository<BoardEntity, Long> {
    
}
