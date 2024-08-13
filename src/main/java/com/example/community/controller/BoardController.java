package com.example.community.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import com.example.community.dto.JobBoardDTO;
import com.example.community.dto.BoardDTO;
import com.example.community.dto.BoardReportDTO;
import com.example.community.dto.check.BoardCategory;
import com.example.community.dto.combine.BoardListDTO;
import com.example.community.service.BoardService;
import com.example.community.util.PageNavigator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.PostMapping;




@Slf4j
@Controller
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;

    // 첨부 파일 경로 요청
    @Value("${spring.servlet.multipart.location}")
    String uploadPath;


    // 페이지 당 글의 개수
    @Value("${user.board.pageLimit}")
    int pageLimit; // 한 페이지 당 게시글 개수 (9개)


    // ======================== 게시글 목록 ========================

    /**
     * 게시글 목록 요청(카테고리, 페이지, 검색어) 
     * 1 : 메인화면(index)에서 넘어 올 경우에 searchWord가 없으므로 기본값 세팅, 1페이지 요청
     * 2 : 목록에서 검색하여 넘어올 경우 searchWord가 있으므로 그 값 사용, 1페이지 요청
     * 3 : 목록의 하단에서 페이지 선택한 경우 선택한 페이지 값 사용, searchWord가 있는 경우는 그 값 사용
     * @param ctgr (카테고리)
     * @param pageable (페이징 객체)
     * @param searchWord (검색어)
     * @param model
     * @return
     */
    @GetMapping("/board/list")
    public String boardList(@RequestParam(name = "category") String ctgr,
                            @RequestParam(name = "userGroup", defaultValue = "0") String userGroup, // 로그인한 사용자의 기수 (기본값 0)
                            @PageableDefault(page=1) Pageable pageable, // 페이징 해주는 객체, 요청한 페이지가 없으면 1로 세팅
                            @RequestParam(name = "searchWord", defaultValue = "") String searchWord ,
                            Model model) {
        // 카테고리 String -> enum 타입으로 변환
        BoardCategory category = BoardCategory.valueOf(ctgr); 
        
        // Pageination
        // category에 따른 게시글 DTO를 List 형태로 가져오기
        Page<BoardListDTO> list;
        if (category == BoardCategory.activity || category == BoardCategory.recruit) {
            list = boardService.selectActivityOrRecruitBoards(category, pageable, searchWord);
        }else if(category == BoardCategory.group){
            list = boardService.selectGroupBoards(userGroup, pageable, searchWord);
        }else{
            list =  boardService.selectOtherCategoryBoards(category, pageable,searchWord);
        }

        int totalPages = (int)list.getTotalPages();
        int page = pageable.getPageNumber();

        PageNavigator navi = new PageNavigator(pageLimit, page, totalPages);

        model.addAttribute("list", list);
        model.addAttribute("category", ctgr);
        model.addAttribute("searchWord", searchWord);
        model.addAttribute("navi", navi);

        return "board/list";
    }
    
    
    // ======================== 게시글 삭제 ========================
    
    /**
     * 게시글 삭제 요청
     * @param boardId
     * @param category
     * @return
     */
    @GetMapping("/board/delete")
    public String boardDelete(@RequestParam(name = "boardId") Long boardId, 
                            @RequestParam(name = "category") BoardCategory category,
                            @RequestParam(name = "searchWord", defaultValue = "") String searchWord,
                            RedirectAttributes rttr) {

        // boardId에 해당하는 게시글 삭제 
        boardService.deleteOne(boardId);

        // 카테고리, 검색어 (페이지?)
        rttr.addAttribute("category", category);
        rttr.addAttribute("searchWord", searchWord);

        return "redirect:/board/list"; // 게시글 목록 화면으로 
    }



    // ================== 게시글 생성 ===================
    
    /**
     * 게시글 작성 화면 요청 - activity or recruit 외 카테고리 
     * @param param
     * @return
     */
    @GetMapping("/board/write")
    public String writeBoard() {
        return "board/write";
    }


    /**
     * 게시글 작성 화면 요청 - activity or recruit
     * @param param
     * @return
     */
    @GetMapping("/board/writeActivityOrRecruit")
    public String writeJobBoard() {
        return "board/writeActivityOrRecruit";
    }
    
    
    /**
     * 게시글 작성 요청 (Board 테이블 삽입)
     * @param dto
     * @return
     */
    @PostMapping("/board/write")
    public String writeBoard(@ModelAttribute BoardDTO dto, Model model) {
        
        // 전달받은 게시글 DTO를 Board 테이블에 삽입
        boardService.insertBoard(dto);
        
        // activity or recruit -> JobBoard 테이블에 정보 삽입 
        if (dto.getCategory()==BoardCategory.activity || dto.getCategory()==BoardCategory.recruit) {
            JobBoardDTO jobBoardDTO = new JobBoardDTO(dto.getBoardId(), dto.getDeadline(), dto.getLimitNumber(), dto.getCurrentNumber());
            boardService.insertJobBoard(jobBoardDTO);
        }
        
        // 게시글 목록에 필요한 파라미터 값 세팅 후 model에 담기
        model.addAttribute("category", dto.getCategory());
        model.addAttribute("userGroup", dto.getMemberGroup());

        return "board/list";
    }
    

    // =================== 게시글 조회 =====================

    /**
     * 게시글 조회 화면 요청
     * @param boardId
     * @param category
     * @param searchWord
     * @param model
     * @return
     */
    @GetMapping("/board/detail")
    public String boardDetail(@RequestParam(name = "boardId") Long boardId, 
                            @RequestParam(name = "category") BoardCategory category,
                            @RequestParam(name = "searchWord", defaultValue = "") String searchWord,
                            Model model) {
        
        // boardId에 해당하는 게시글 DTO 
        BoardDTO board = boardService.selectOne(boardId);
        // 조회수 증가
        boardService.increaseHitCount(boardId);

        model.addAttribute("board", board);
        model.addAttribute("searchWord", searchWord);
        model.addAttribute("category", category);

        return "board/detail";
    }

    /**
     * ajax - activity/recruit 게시글인 경우 해당 게시글의 마감 여부 확인 요청
     * @param param
     * @return
     */
    @ResponseBody
    @GetMapping("/board/isDead")
    public boolean jobBoardIsDead(@RequestParam(name = "boardId") Long boardId) {
        boolean deadline = boardService.isDeadline(boardId);
        boolean exceededLimitNumber = boardService.isExceededLimitNumber(boardId);
        return deadline||exceededLimitNumber ? true  : false ; // deadline이 넘었거나 제한 인원을 초과했으면 true 반환
    }

    /**
     * ajax - recruit 게시글인 경우 로그인한 사용자의 참여 여부 요청
     * @param boardId
     * @param memberId
     * @return
     */
    @ResponseBody
    @GetMapping("/board/isRecruited")
    public boolean jobBoardRecruitIsRecruited(@RequestParam(name = "boardId") Long boardId, @RequestParam(name = "memberId")String memberId){
        return boardService.isRecruited(boardId, memberId);
    }
    

    // ===================== 게시글 좋아요 ===================

    /**
     * ajax - 게시글 좋아요수 요청
     * @param param
     * @return
     */
    @ResponseBody
    @GetMapping("/board/getLikeCount")
    public long getBoardLikeCount(@RequestParam(name = "boardId")Long boardId) {
        return boardService.getLikeCount(boardId);
    }

    /**
     * ajax - 전달받은 memberId가 해당 게시글 좋아요 눌렀는지 확인을 위한 요청
     * @param boardId
     * @return 좋아요 설정된 상태 → true / 좋아요 해제된 상태 → false
     */
    @ResponseBody
    @GetMapping("/board/isLikeCount")
    public boolean boardIsLikeCount(@RequestParam(name = "boardId")Long boardId, @RequestParam(name = "memberId") String memberId) {
        return boardService.isBoardLikedByMember(boardId,memberId);
    }
    
    /**
     * ajax - 게시글 좋아요 요청 및 해제
     * @param boardId
     * @return 좋아요 설정 → true / 좋아요 해제 → false
     */
    @ResponseBody
    @GetMapping("/board/likeUpdate")
    public boolean boardLikeUpdate(@RequestParam(name = "boardId") Long boardId, @RequestParam(name = "memberId") String memberId) {
        return boardService.likeBoard(boardId,memberId);
    }

    

    // ===================== 게시글 신고 =====================
    
    /**
     * 사용자의 게시글 신고 요청
     * @param entity
     * @return
     */
    @PostMapping("/board/report")
    public String postMethodName(@ModelAttribute BoardReportDTO dto, 
                                @RequestParam(name = "category") BoardCategory category,
                                @RequestParam(name = "searchWord", defaultValue = "") String searchWord,
                                RedirectAttributes rttr) {
        
        // JobBoardReport DB에 저장
        boardService.insertJobBoardReported(dto);
        
        // Board의 report 컬럼 값 수정
        boardService.updateRportedCount(dto.getBoardId());
        
        // 카테고리, 검색어, 페이지
        rttr.addAttribute("category", category);
        rttr.addAttribute("searchWord", searchWord);
        
        return "redirect:/board/list"; // 게시글 목록으로 이동
    }
    
    // ===================== recruit 참여 =====================


    
} 