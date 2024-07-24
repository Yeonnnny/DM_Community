package com.example.community.dto;

import java.time.LocalDateTime;

import com.example.community.dto.check.ReportCategory;
import com.example.community.entity.JobBoardReportedEntity;

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
public class JobBoardReportedDTO {
    private Long reportId;
    private Long boardId;
    private String memberId;
    private ReportCategory category;
    private String reason;
    private LocalDateTime reportDate;

    public static JobBoardReportedDTO toDTO (JobBoardReportedEntity entity, Long boardId){
        return JobBoardReportedDTO.builder()
            .reportId(entity.getReportId())
            .boardId(boardId)
            .memberId(entity.getMemberId())
            .category(entity.getCategory())
            .reason(entity.getReason())
            .reportDate(entity.getReportDate())
            .build();
    }
}
