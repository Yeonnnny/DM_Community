package com.example.community.controller;

import org.springframework.stereotype.Controller;

import com.example.community.service.ReplyService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequiredArgsConstructor
public class ReplyController {
    private final ReplyService replyService;

    /**
     * ajax - 게시글의 댓글 수 요청
     * @param param
     * @return
     */
    @ResponseBody
    @GetMapping("/reply/getReplyCount")
    public long getReplyCount(@RequestParam(name = "boardId")Long boarId) {
        return replyService.getBoardReplyCount(boarId);
    }
    
}
