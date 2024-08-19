package com.example.community.service;

import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.community.dto.ReplyDTO;
import com.example.community.entity.BoardEntity;
import com.example.community.entity.LikeEntity;
import com.example.community.entity.MemberEntity;
import com.example.community.entity.ReplyEntity;
import com.example.community.repository.BoardRepository;
import com.example.community.repository.LikeRepository;
import com.example.community.repository.MemberRepository;
import com.example.community.repository.ReplyRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
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
     * @param boardId
     * @return boardEntity
     */
    private BoardEntity selectBoardEntity(Long boardId){
        return boardRepository.findById(boardId).orElseThrow(() -> new EntityNotFoundException("Board not found with ID: " + boardId));
    }

    /**
     * 전달받은 memberId에 해당하는 MemberEntity를 반환하는 함수
     * @param memberId
     * @return memberEntity
     */
    private MemberEntity selectMemberEntity(String memberId){
        return memberRepository.findById(memberId).orElseThrow(() -> new EntityNotFoundException("Member not found with ID: " + memberId));
    }

    /**
     * 전달받은 replyId에 해당하는 ReplyEntity를 반환하는 함수
     * @param replyId
     * @return replyEntity
     */
    private ReplyEntity selectReplyEntity(Long replyId){
        return replyRepository.findById(replyId).orElseThrow(() -> new EntityNotFoundException("Reply not found with ID: " + replyId));
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
     * boardId에 대한 댓글DTO 목록 반환 (특정 memberId의 댓글 좋아요 여부(likeByUser)도 포함시킴)
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


    // ====================== 댓글 수정 =====================

    /**
     * 해당 Entity의 일부 속성(content, updateDate)을 전달된 값으로 수정하는 함수
     * @param replyDTO
     */
    @Transactional
    public void updateOne(ReplyDTO replyDTO) {
        ReplyEntity replyEntity = selectReplyEntity(replyDTO.getReplyId()); // 기존 ReplyEntity
        updateReplyContent(replyEntity, replyDTO); // Reply 수정 (content, updateDate)
    }

    /**
     * Reply의 content, updateDate 수정 함수
     * @param replyEntity
     * @param replyDTO
     */
    private void updateReplyContent(ReplyEntity replyEntity, ReplyDTO replyDTO){
        replyEntity.setContent(replyDTO.getContent());
        replyEntity.setUpdateDate(replyDTO.getUpdateDate());
    }

    
    // ====================== 댓글 삭제 =====================
    
    /**
     * 전달 받은 replyId에 해당하는 댓글 데이터 삭제하는 함수
     * @param replyId
     */
    public void deleteOne(Long replyId) {
        replyRepository.deleteById(replyId);
    }
    
    
    // ====================== 댓글 좋아요 =====================

    /**
     * member가 reply에 대해 이미 좋아요를 눌렀던 상태라면 좋아요 해제하고, 좋아요가 해제된 상태라면 좋아요 설정하는 함수
     * @param replyId
     * @param memberId
     * @return 좋아요 설정 → true / 좋아요 해제 → false
     */
    @Transactional
    public boolean toggleLikeOnReply(Long replyId, String memberId) {
        ReplyEntity replyEntity = selectReplyEntity(replyId); // ReplyEntity
        MemberEntity memberEntity = selectMemberEntity(memberId); // MemberEntity

        Optional<LikeEntity> likeEntityOptional = likeRepository.findByMemberAndReply(memberEntity, replyEntity);

        if(likeEntityOptional.isPresent()){
            likeRepository.delete(likeEntityOptional.get()); // delete from Like DB
            replyEntity.setLikeCount(replyEntity.getLikeCount()-1); // likeCount - 1 
            return false; // 좋아요 해제
        }else{
            // 좋아요 데이터 생성 
            LikeEntity likeEntity = LikeEntity.builder()
                                                .replyEntity(replyEntity)
                                                .memberEntity(memberEntity)
                                                .build();
            likeRepository.save(likeEntity); // save to Like DB
            replyEntity.setLikeCount(replyEntity.getLikeCount()+1); // likeCount + 1
            return true; // 좋아요 해제
        }
    }

    // ====================== 대댓글 =====================

    /**
     * 부모 댓글 존재하는지 확인하는 함수
     * @param parentReplyId
     * @return 존재 → true 
     */
    public boolean existsParentReply(Long parentReplyId) {
        return replyRepository.existsById(parentReplyId);
    }
}
