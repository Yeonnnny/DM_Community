package com.example.community.dto;

import java.time.LocalDateTime;

import org.springframework.web.multipart.MultipartFile;

import com.example.community.dto.check.BoardCategory;
import com.example.community.entity.BoardEntity;

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
public class BoardDTO {
    private Long boardId;
    private String memberId;
    private BoardCategory category;
    private String title;
    private String content;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
    private int hitCount;
    private int likeCount;
    private MultipartFile uploadFile;
    private String originalFileName;
    private String savedFileName;
    private boolean reported;
    
    // Job board specific fields
    private LocalDateTime deadline;
    private int limitNumber;
    private int currentNumber;


    public static BoardDTO toDTO(BoardEntity entity, String memberId){
        return BoardDTO.builder()
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
                .reported(entity.isReported())
                .build();
    }
}
