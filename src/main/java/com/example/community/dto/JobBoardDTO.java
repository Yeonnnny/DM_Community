package com.example.community.dto;

import java.time.LocalDateTime;

import com.example.community.dto.check.BoardCategory;
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
    private String memberId;
    private BoardCategory category;
    private String title;
    private String content;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
    private int hitCount;
    private int likeCount;
    private String originalFileName;
    private String savedFileName;
    private LocalDateTime deadline;
    private int limitNumber;
    private int currentNumber;
    private int reported;

    public static JobBoardDTO toDTO(JobBoardEntity entity, String memberId){
        return JobBoardDTO.builder()
                .boardId(entity.getBoardId())
                .memberId(memberId)
                .category(entity.getCategory())
                .title(entity.getTitle())
                .content(entity.getContent())
                .createDate(entity.getCreateDate())
                .updateDate(entity.getUpdateDate())
                .hitCount(entity.getHitCount())
                .likeCount(entity.getLikeCount())
                .originalFileName(entity.getOriginalFileName())
                .savedFileName(entity.getSavedFileName())
                .deadline(entity.getDeadline())
                .limitNumber(entity.getLimitNumber())
                .currentNumber(entity.getCurrentNumber())
                .reported(entity.getReported())
                .build();

    }
    
}
