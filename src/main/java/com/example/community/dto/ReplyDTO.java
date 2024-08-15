package com.example.community.dto;

import java.time.LocalDateTime;

import com.example.community.entity.ReplyEntity;

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
public class ReplyDTO {
    private Long replyId;
    private Long boardId;
    private Long parentReplyId;
    private String memberId;
    private String content;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
    private int likeCount;

    // 로그인한 사용자의 댓글 좋아요 여부 정보를 담기 위한 변수
    private boolean likeByUser;

    public static ReplyDTO toDTO(ReplyEntity entity, Long boardId, String memberId){
        return ReplyDTO.builder()
            .replyId(entity.getReplyId())
            .boardId(boardId)
            .parentReplyId(entity.getParentReplyId())
            .memberId(memberId)
            .content(entity.getContent())
            .createDate(entity.getCreateDate())
            .updateDate(entity.getUpdateDate())
            .likeCount(entity.getLikeCount())
            .build();
    }

}
