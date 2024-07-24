package com.example.community.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.example.community.dto.JobBoardReportedDTO;
import com.example.community.dto.check.ReportCategory;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "job_board_reported")
public class JobBoardReportedEntity {
    @Id
    @Column(name = "report_id")
    private Long reportId;
    
    // FK
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private JobBoardEntity jobBoardEntity;

    @Column(name = "member_id", nullable = false)
    private String memberId;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private ReportCategory category;
    
    private String reason;

    @Column(name = "report_date")
    @CreationTimestamp
    private LocalDateTime reportDate;

    public static JobBoardReportedEntity toEntity (JobBoardReportedDTO dto, JobBoardEntity jobBoardEntity){
        return JobBoardReportedEntity.builder()
            .reportId(dto.getReportId())
            .jobBoardEntity(jobBoardEntity)
            .memberId(dto.getMemberId())
            .category(dto.getCategory())
            .reason(dto.getReason())
            .reportDate(dto.getReportDate())
            .build();
    }
}
