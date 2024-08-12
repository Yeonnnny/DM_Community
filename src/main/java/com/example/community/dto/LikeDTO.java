package com.example.community.dto;

import com.example.community.entity.LikeEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@Builder
public class LikeDTO {
    private Long likeId;
    private String memberId;
    private Long boardId;
    private Long replyId;

    public static LikeDTO toDTO (LikeEntity entity, String memberId, Long boardId, Long replyId){
        return builder()
                .likeId(entity.getLikeId())
                .memberId(memberId)
                .boardId(boardId)
                .replyId(replyId)
                .build();
    }
}
