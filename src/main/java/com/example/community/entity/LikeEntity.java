package com.example.community.entity;

import com.example.community.dto.LikeDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
@Entity
@Table(name = "like")
public class LikeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id")
    private Long likeId;
    
    // FK
    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = "member_id")
    private MemberEntity memberEntity;
    
    // FK
    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = "board_id")
    private BoardEntity boardEntity;
    
    // FK
    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = "reply_id")
    private ReplyEntity replyEntity;

    public static LikeEntity toEntity(LikeDTO dto, MemberEntity memberEntity, BoardEntity boardEntity, ReplyEntity replyEntity){
        return builder()
                .likeId(dto.getReplyId())
                .memberEntity(memberEntity)
                .boardEntity(boardEntity)
                .replyEntity(replyEntity)
                .build();
    }
}
