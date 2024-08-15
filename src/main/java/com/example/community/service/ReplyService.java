package com.example.community.service;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.community.dto.ReplyDTO;
import com.example.community.entity.BoardEntity;
import com.example.community.entity.MemberEntity;
import com.example.community.entity.ReplyEntity;
import com.example.community.repository.BoardRepository;
import com.example.community.repository.LikeRepository;
import com.example.community.repository.MemberRepository;
import com.example.community.repository.ReplyRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReplyService {
    private final ReplyRepository replyRepository;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final LikeRepository likeRepository;

    // ====================== select 함수 ======================
    
    /**
     * 전달받은 boardId에 해당하는 BoardEntity를 반환하는 함수
     */
    private BoardEntity selectBoardEntity(Long boardId){
        return boardRepository.findById(boardId).orElseThrow(() -> new EntityNotFoundException("Board not found with ID: " + boardId));
    }

    /**
     * 전달받은 memberId에 해당하는 MemberEntity를 반환하는 함수
     */
    private MemberEntity selectMemberEntity(String memberId){
        return memberRepository.findById(memberId).orElseThrow(() -> new EntityNotFoundException("Board not found with ID: " + memberId));
    }

    // ====================== 게시글 조회 ==========================


    /**
     * 전달받은 boardId에 해당하는 게시글의 총 댓글 수를 반환하는 함수
     * @param boarId
     * @return
     */
    public long getBoardReplyCount(Long boarId) {
        BoardEntity boardEntity = selectBoardEntity(boarId);
        return replyRepository.countByBoardEntity(boardEntity);
    }

    // ====================== 댓글 목록 =====================
    
    /**
     * boardId에 대한 댓글DTO 목록 반환 
     * @param boardId
     * @param memberId
     * @return
     */
    public List<ReplyDTO> getList(Long boardId, String memberId) {
        BoardEntity boardEntity = selectBoardEntity(boardId); // boardEntity
        List<ReplyEntity> replyEntities = replyRepository.findByBoardEntity(boardEntity); // boardEntity의 댓글 목록 가져옴

        return replyEntities.stream().map(reply ->{
            boolean isLikeByUser = likeRepository.existsByReplyIdAndMemberId(reply.getReplyId(), memberId); // user의 좋아요 여부 
            return ReplyDTO.builder()
                .replyId(reply.getReplyId())
                .boardId(reply.getBoardEntity().getBoardId())
                .parentReplyId((reply.getParentReplyId()))
                .memberId(reply.getMemberEntity().getMemberId())
                .content(reply.getContent())
                .createDate(reply.getCreateDate())
                .updateDate(reply.getUpdateDate())
                .likeCount(reply.getLikeCount())
                .likeByUser(isLikeByUser)
                .build();
        }).collect(Collectors.toList());

    }

    // ====================== 댓글 등록 =====================

    /**
     * 해당 댓글 DTO를 Entity로 변환 후 DB에 저장하는 함수
     * @param replyDTO
     */
    public void createOne(ReplyDTO replyDTO) {
        BoardEntity boardEntity = selectBoardEntity(replyDTO.getBoardId());  // boardEntity
        MemberEntity memberEntity = selectMemberEntity(replyDTO.getMemberId()); // memberEntity
        ReplyEntity replyEntity = ReplyEntity.toEntity(replyDTO, boardEntity, memberEntity); // DTO -> Entity 변환
        replyRepository.save(replyEntity); // save to Reply
    }
}
