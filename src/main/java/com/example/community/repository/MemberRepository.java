package com.example.community.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.community.entity.MemberEntity;

public interface MemberRepository extends JpaRepository<MemberEntity, String>{
    
}
