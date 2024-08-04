package com.example.community.controller;

import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;

import com.example.community.dto.JobBoardDTO;
import com.example.community.dto.BoardDTO;
import com.example.community.dto.BoardReportDTO;
import com.example.community.dto.check.BoardCategory;
import com.example.community.dto.display.BoardListDTO;
import com.example.community.service.BoardService;
import com.example.community.util.PageNavigator;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
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
    @GetMapping("job/boardList")
    public String boardList(@RequestParam(name = "category") String ctgr,
                            @RequestParam(name = "userGroup") String userGroup, // 로그인한 사용자의 기수
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

        return "job/list";
    }
    
    
    // ======================== 게시글 삭제 ========================
    
    /**
     * 게시글 삭제 요청
     * @param boardId
     * @param category
     * @return
     */
    @GetMapping("job/boardDelete")
    public String boardDelete(@RequestParam(name = "boardId") Long boardId, 
                            @RequestParam(name = "category") BoardCategory category,
                            @RequestParam(name = "searchWord", defaultValue = "") String searchWord,
                            RedirectAttributes rttr) {

        // boardId에 해당하는 게시글 삭제 
        boardService.deleteOne(boardId);

        // 카테고리, 검색어 (페이지?)
        rttr.addAttribute("category", category);
        rttr.addAttribute("searchWord", searchWord);

        return "redirect:/job/list"; // 게시글 목록 화면으로 
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
    public String postMethodName(@ModelAttribute BoardReportDTO dto, 
                            @RequestParam(name = "category") BoardCategory category,
                            @RequestParam(name = "searchWord", defaultValue = "") String searchWord,
                            RedirectAttributes rttr) {

        // 신고 당한 게시글 블라인드 처리 후  
        // JobBoardReport DB에 저장
        boardService.insertJobBoardReported(dto);

        // 카테고리, 검색어, 페이지
        rttr.addAttribute("category", category);
        rttr.addAttribute("searchWord", searchWord);
        
        return "redirect:/job/list"; // 해당 게시글 화면
    }
    
    // ================= 첨부파일 다운로드 ===================

    @GetMapping("/download")
    public String download(@RequestParam(name = "boardId") Long boardId, HttpServletResponse response) {

        BoardDTO boardDTO= boardService.selectOne(boardId);

        String originalFileName = boardDTO.getOriginalFileName();
        String savedFileName = boardDTO.getSavedFileName();

        try {
            String tempName = URLEncoder.encode(originalFileName,StandardCharsets.UTF_8.toString());
            response.setHeader("Content-Disposition", "attachment;filname="+tempName);
            // 위 코드가 없을 경우 브라우저가 실행 가능한 파일(이미지 파일이 대표적인 예임)인 경우 브라우저 자체에서 오픈함
            // 즉, 위 코드는 브라우저 자체에서 실행되도록 하지 않고 다운받게 하기 위한 코드임 
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String fullPath = uploadPath+"/"+savedFileName;

        // 스트림 설정 (실제 다운로드)
        FileInputStream filein = null;
        ServletOutputStream fileout = null; // 원격지의 장소에 데이터를 쏘기 위함

        // Local에 있는 파일을 메모리로 끌어와야 함
        try {
            filein = new FileInputStream(fullPath); // 하드디스크->메모리에 올림(서버 입장에서의 로컬이기 때문에 input 작업임)
            fileout = response.getOutputStream();   // 웹에서 원격지의 데이터를 쏴주는 것. 로컬에서 벗어난 다른 쪽으로 데이터를 쏘는 역할

            FileCopyUtils.copy(filein, fileout);    // copy(원본, 내보낼 객체) : 원본을 읽어서 내보냄
            
            // 연 순서의 반대로 닫아야 함
            fileout.close();
            filein.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    
    

    // ================== 코드 공유 게시판 ===================
    /**
     * 코드 게시글 작성 화면 요청
     * @return
     */
    @GetMapping("job/code/codeWrite")
    public String codeWrite() {
        return "job/code/write";
    }

    /**
     * 
     * @param dto
     * @param model
     * @return
     */
    @PostMapping("job/code/codeWrite")
    public String codeWrite(@ModelAttribute JobBoardDTO dto, Model model) {

        // 전달받은 DTO를 DB에 저장
        
        // 게시글 DTO를 담아서 화면 조회 요청
        model.addAttribute("board", dto);
        return "job/code/detail";
    }
    

    
    



    // ================ 지난 프로젝트 게시판 =================




    // ================== 대외 활동 게시판 ===================




    // ============= 공모전/프로젝트 구인 게시판 =============



    
}
