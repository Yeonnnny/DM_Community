package com.example.community.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import com.example.community.dto.ReplyDTO;
import com.example.community.service.ReplyService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;



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

    // ================== 댓글 목록 ==================
    // list로 보내지 않고 RequestMapping으로 보내야겠다 ㅎㅎ 
    // 원래 html을 보면서 같이 해야 하는데.. 지금 프로젝트 실행이 안되므로 .... 허허
    // 일단 감으로 해보겠습니다.
    
    /**
     * ajax - 게시글에 대한 댓글 목록 요청
     * @param boardId
     * @param memberId
     * @param model
     * @return
     */
    @RequestMapping(value = "/reply/getList", method=RequestMethod.GET)
    public String getList(@RequestParam(name = "boardId") Long boardId, @RequestParam(name = "memberId") String memberId, Model model) {
        
        List<ReplyDTO> replyDTOs = new ArrayList<>();
        
        replyDTOs = replyService.getList(boardId, memberId);
        
        model.addAttribute("list", replyDTOs);
        
        return "/board/detail::#result";
    }
    
    // ================== 댓글 등록 ==================
    
    /**
     * ajax - 댓글 등록 처리 요청
     * @param boardId
     * @param memberId
     * @param content
     * @return
     */
    @ResponseBody
    @GetMapping("/reply/create")
    public String create(@RequestParam(name = "boardId") Long boardId,
                        @RequestParam(name = "memberId") String memberId, 
                        @RequestParam(name = "content")String content) {
        ReplyDTO replyDTO = ReplyDTO.builder()
                            .boardId(boardId)
                            .parentReplyId(null)
                            .memberId(memberId)
                            .content(content)
                            .createDate(LocalDateTime.now())
                            .updateDate(null)
                            .likeCount(0)
                            .build();
        replyService.createOne(replyDTO);
        
        return ""; // 뭘 반환해여햐지? -> 댓글 목록 요청 함수를 호출하면 되지 않을까?
    }
    


}
