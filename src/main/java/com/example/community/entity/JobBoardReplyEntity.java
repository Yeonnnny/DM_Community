package com.example.community.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CurrentTimestamp;

import com.example.community.dto.JobBoardReplyDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "job_board_reply")
public class JobBoardReplyEntity {
    @Id
    @Column(name="reply_id")
    private Long replyId;

    //FK
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private JobBoardEntity jobBoardEntity;

    @Column(name = "parent_reply_id")
    private Long parentReplyId;

    @Column(name = "member_id", nullable = false)
    private String memberId;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "create_date")
    @CurrentTimestamp
    private LocalDateTime createDate;

    @Column(name = "update_time")
    private LocalDateTime updateDate;

    @Column(name = "like_count")
    private int likeCount;

    public static JobBoardReplyEntity toEntity(JobBoardReplyDTO dto, JobBoardEntity jobBoardEntity){
        return JobBoardReplyEntity.builder()
            .replyId(dto.getReplyId())
            .jobBoardEntity(jobBoardEntity)
            .parentReplyId(dto.getParentReplyId())
            .memberId(dto.getMemberId())
            .content(dto.getContent())
            .createDate(dto.getCreateDate())
            .updateDate(dto.getUpdateDate())
            .likeCount(dto.getLikeCount())
            .build();
    }

}
