package com.example.community.dto;

import com.example.community.entity.JobBoardRecruitEntity;

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
public class JobBoardRecruitDTO {
    private Long recruitId;
    private Long boardId;
    private String memberId;
    private String memberGroup;
    private String memberPhone;
    private String memberEmail;

    public static JobBoardRecruitDTO toDTO (JobBoardRecruitEntity entity, Long boardId){
        return JobBoardRecruitDTO.builder()
            .recruitId(entity.getRecruitId())
            .boardId(boardId)
            .memberId(entity.getMemberId())
            .memberGroup(entity.getMemberGroup())
            .memberEmail(entity.getMemberEmail())
            .build();
    }
}
