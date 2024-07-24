package com.example.community.entity;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.example.community.dto.JobBoardDTO;
import com.example.community.dto.check.BoardCategory;

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
@Table(name="job_board")
public class JobBoardEntity {
    
    @Id
    @Column(name = "board_id")
    private Long boardId;

    // FK
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private MemberEntity memberEntity;

    @Column(name = "category", nullable = false)
    @Enumerated(EnumType.STRING)
    private BoardCategory category;
    
    @Column(name = "title", nullable = false)
    private String title;
    
    @Column(name = "content", nullable = false)
    private String content;
    
    @Column(name = "create_date")
    @CreationTimestamp
    private LocalDateTime createDate;

    @Column(name = "update_date")
    private LocalDateTime updateDate;

    @Column(name = "hit_count")
    private int hitCount;

    @Column(name = "like_count")
    private int likeCount;

    @Column(name = "original_file_name")
    private String originalFileName;

    @Column(name = "saved_file_name")
    private String savedFileName;

    private LocalDateTime deadline;
    
    @Column(name = "limit_number")
    private int limitNumber;
    
    @Column(name = "current_number")
    private int currentNumber;
    
    @Column(name = "reported")
    private int reported;


    public static JobBoardEntity toEntity(JobBoardDTO dto, MemberEntity memberEntity){
        return JobBoardEntity.builder()
                .boardId(dto.getBoardId())
                .memberEntity(memberEntity)
                .category(dto.getCategory())
                .title(dto.getTitle())
                .content(dto.getContent())
                .createDate(dto.getCreateDate())
                .updateDate(dto.getUpdateDate())
                .hitCount(dto.getHitCount())
                .likeCount(dto.getLikeCount())
                .originalFileName(dto.getOriginalFileName())
                .savedFileName(dto.getSavedFileName())
                .deadline(dto.getDeadline())
                .limitNumber(dto.getLimitNumber())
                .currentNumber(dto.getCurrentNumber())
                .reported(dto.getReported())
                .build();

    }
    
}
