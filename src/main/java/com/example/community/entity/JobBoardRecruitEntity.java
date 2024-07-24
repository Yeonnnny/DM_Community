package com.example.community.entity;


import com.example.community.dto.JobBoardRecruitDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@RequiredArgsConstructor
@Setter
@Getter
@ToString
@Builder
@Entity
@Table(name = "job_board_recruit")
public class JobBoardRecruitEntity {
    @Id
    @Column(name = "recruit_id")
    private Long recruitId;

    // FK
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private JobBoardEntity jobBoardEntity;

    @Column(name = "member_id", nullable = false)
    private String memberId;

    @Column(name = "member_group", nullable = false)
    private String memberGroup;

    @Column(name = "member_phone")
    private String memberPhone;
    
    @Column(name = "member_email")
    private String memberEmail;

    public static JobBoardRecruitEntity toEntity (JobBoardRecruitDTO dto, JobBoardEntity jobBoardEntity){
        return JobBoardRecruitEntity.builder()
            .recruitId(dto.getRecruitId())
            .jobBoardEntity(jobBoardEntity)
            .memberId(dto.getMemberId())
            .memberGroup(dto.getMemberGroup())
            .memberEmail(dto.getMemberEmail())
            .build();
    }
}
