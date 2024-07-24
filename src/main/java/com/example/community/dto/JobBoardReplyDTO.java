package com.example.community.dto;

import java.time.LocalDateTime;

import com.example.community.entity.JobBoardReplyEntity;

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
public class JobBoardReplyDTO {
    private Long replyId;
    private Long boardId;
    private Long parentReplyId;
    private String memberId;
    private String content;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
    private int likeCount;

    public static JobBoardReplyDTO toDTO(JobBoardReplyEntity entity, Long boardId){
        return JobBoardReplyDTO.builder()
            .replyId(entity.getReplyId())
            .boardId(boardId)
            .parentReplyId(entity.getParentReplyId())
            .memberId(entity.getMemberId())
            .content(entity.getContent())
            .createDate(entity.getCreateDate())
            .updateDate(entity.getUpdateDate())
            .likeCount(entity.getLikeCount())
            .build();
    }

}
