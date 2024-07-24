package com.example.community.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "member")
public class MemberEntity {
    private String memberId;    
}
