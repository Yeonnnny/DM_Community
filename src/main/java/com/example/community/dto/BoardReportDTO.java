package com.example.community.dto;

import java.time.LocalDateTime;

import com.example.community.dto.check.ReportCategory;
import com.example.community.entity.BoardReportEntity;

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
public class BoardReportDTO {
    private Long reportId;
    private Long boardId;
    private String memberId;
    private ReportCategory category;
    private String reason;
    private LocalDateTime reportDate;

    public static BoardReportDTO toDTO (BoardReportEntity entity, Long boardId){
        return BoardReportDTO.builder()
            .reportId(entity.getReportId())
            .boardId(boardId)
            .memberId(entity.getMemberId())
            .category(entity.getCategory())
            .reason(entity.getReason())
            .reportDate(entity.getReportDate())
            .build();
    }
}
