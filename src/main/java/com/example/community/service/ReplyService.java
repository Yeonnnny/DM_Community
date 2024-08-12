package com.example.community.service;

import org.springframework.stereotype.Service;

import com.example.community.entity.BoardEntity;
import com.example.community.repository.BoardRepository;
import com.example.community.repository.ReplyRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReplyService {
    private final ReplyRepository replyRepository;
    private final BoardRepository boardRepository;

    // ====================== 게시글 조회 ==========================

    /**
     * 전달받은 boardId에 해당하는 BoardEntity를 반환하는 함수
     */
    private BoardEntity selectBoardEntity(Long boardId){
        return boardRepository.findById(boardId).get();
    }

    /**
     * 전달받은 boardId에 해당하는 게시글의 총 댓글 수를 반환하는 함수
     * @param boarId
     * @return
     */
    public long getBoardReplyCount(Long boarId) {
        BoardEntity boardEntity = selectBoardEntity(boarId);
        return replyRepository.countByBoardEntity(boardEntity);
    }
}
