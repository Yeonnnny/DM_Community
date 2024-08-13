package com.example.community.dto;

import java.time.LocalDateTime;

import com.example.community.entity.JobBoardEntity;

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
public class JobBoardDTO {
    private Long boardId;
    private LocalDateTime deadline;
    private int limitNumber;
    private int currentNumber;

    public static JobBoardDTO toDTO(JobBoardEntity entity){
        return JobBoardDTO.builder()
                .boardId(entity.getBoardId())
                .deadline(entity.getDeadline())
                .limitNumber(entity.getLimitNumber())
                .currentNumber(entity.getCurrentNumber())
                .build();
    }
    
}
