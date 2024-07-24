package com.example.community.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import com.example.community.dto.JobBoardDTO;
import com.example.community.dto.JobBoardReportedDTO;
import com.example.community.dto.check.BoardCategory;
import com.example.community.service.JobBoardService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.PostMapping;



@Controller
@RequiredArgsConstructor
public class JobBoardController {
    private final JobBoardService jobBoardService;

    // ===================== 게시글 목록 =====================
    /**
     * 게시글 목록 요청
     * @param ctgr
     * @param model
     * @return
     */
    @GetMapping("job/boardList")
    public String boardList(@RequestParam(name = "category") String ctgr, Model model) {
        
        BoardCategory category = BoardCategory.valueOf(ctgr); // 카테고리 String -> enum 타입으로 변환

        // category에 해당하는 게시글들 List 형태로 가져오기
        List<JobBoardDTO> list = jobBoardService.getBoardList(category);

        model.addAttribute("list", list);

        return "job/list";
    }
    
    
    // ===================== 게시글 삭제 =====================
    
    /**
     * 게시글 삭제 요청
     * @param boardId
     * @param category
     * @return
     */
    @GetMapping("job/boardDelete")
    public String boardDelete(@RequestParam(name = "boardId") Long boardId,
                                @RequestParam(name = "category") BoardCategory category) {

        // boardId에 해당하는 게시글 삭제 
        jobBoardService.delete(boardId);

        return "job/list"; // 게시글 목록 화면으로 
    }
    

    // ===================== 게시글 좋아요 ===================

    /**
     * Ajax - 게시글 좋아요 요청 및 해제
     * @param boardId
     * @return
     */
    @ResponseBody
    @GetMapping("job/board/likeUpdate")
    public String boardLikeUpdate(@RequestParam(name = "boardId") Long boardId, @RequestParam(name = "memberId") String memberId) {
        return new String();
    }

    /**
     * Ajax - 게시글 좋아요 수 요청
     * @param boardId
     * @return
     */
    @ResponseBody
    @GetMapping("job/board/getLike")
    public String getBoardLikeCount(@RequestParam(name = "boardId") Long boardId) {
        return new String();
    }

    /**
     * Ajax - 게시글에 대한 현재 로그인한 사용자 좋아요 여부 
     * @param boardId
     * @return
     */
    @ResponseBody
    @GetMapping("job/board/isLikedByUser")
    public String boardIsLikedByUser(@RequestParam(name = "boardId") Long boardId, @RequestParam(name = "memberId") String memberId) {
        return new String();
    }
    

    // ===================== 게시글 신고 =====================
    
    /**
     * 사용자의 게시글 신고 요청
     * @param entity
     * @return
     */
    @PostMapping("job/boardReport")
    public String postMethodName(@ModelAttribute JobBoardReportedDTO dto, Model model) {

        // JobBoardReport DB에 저장
        jobBoardService.insertJobBoardReported(dto);

        // 신고 당한 게시글 DTO 
        JobBoardDTO boardDTO = jobBoardService.getBoard(dto.getBoardId());

        model.addAttribute("dto", boardDTO);
        
        return ""; // 해당 게시글 화면
    }
    
    




    // ================== 코드 공유 게시판 ===================




    // ================ 지난 프로젝트 게시판 =================




    // ================== 대외 활동 게시판 ===================




    // ============= 공모전/프로젝트 구인 게시판 =============



    
}
