package com.example.community.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.community.dto.JobBoardDTO;
import com.example.community.dto.BoardReportDTO;
import com.example.community.dto.check.BoardCategory;
import com.example.community.dto.display.BoardListDTO;
import com.example.community.entity.JobBoardEntity;
import com.example.community.entity.BoardEntity;
import com.example.community.entity.BoardReportEntity;
import com.example.community.entity.MemberEntity;
import com.example.community.repository.BoardReportRepository;
import com.example.community.repository.BoardRepository;
import com.example.community.repository.JobBoardRepository;
import com.example.community.repository.MemberRepository;
import com.example.community.util.FileService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final JobBoardRepository jobBoardRepository;
    private final MemberRepository memberRepository;
    private final BoardReportRepository jobBoardReportedRepository;


    // 첨부 파일 경로 요청
    @Value("${spring.servlet.multipart.location}")
    String uploadPath;


    // 페이지 당 글의 개수
    @Value("${user.board.pageLimit}")
    int pageLimit; // 한 페이지 당 게시글 개수


    // ======================== 기본 CRUD ========================

    /**
     * DB에 게시글 저장하는 함수
     * @param dto
     * @return
     */
    public void insertOne(JobBoardDTO boardDTO){
        // 작성자 엔티티
        MemberEntity memberEntity = memberRepository.findById(boardDTO.getMemberId()).get();

        String originalFileName = null;
        String savedFileName = null;

        // 첨부파일이 있으면 파일명 세팅
        if (!boardDTO.getUploadFile().isEmpty()) {
            originalFileName = boardDTO.getUploadFile().getOriginalFilename();
            savedFileName = FileService.saveFile(boardDTO.getUploadFile(), uploadPath);

            boardDTO.setOriginalFileName(originalFileName);
            boardDTO.setSavedFileName(savedFileName);
        }

        // 게시글 DTO -> 엔티티 변환 후 DB 저장
        JobBoardEntity boardEntity = JobBoardEntity.toEntity(boardDTO, memberEntity);
        jobBoardRepository.save(boardEntity);
    }


    /**
     * 전달받은 boardId에 해당하는 게시글 엔티티 반환하는 함수
     * @param boardId
     * @return
     */
    public JobBoardDTO selectOne(Long boardId) {
        JobBoardDTO dto = new JobBoardDTO();
        
        Optional<JobBoardEntity> jobBoardEntity = jobBoardRepository.findById(boardId);
        // 게시글 Entity->DTO변환
        if (jobBoardEntity.isPresent()) {
            JobBoardEntity entity = jobBoardEntity.get();
        
            dto = JobBoardDTO.toDTO(entity, entity.getMemberEntity().getMemberId());
        }
        return dto;
    }


    /**
     * 수정된 게시글 DTO를 받아서 수정해서 DB에 저장하는 함수
     * @param jobBoardDTO
     */
    @Transactional
    public void updateOne(JobBoardDTO boardDTO){
        // 수정된 게시글의 첨부파일 
        MultipartFile uploadFile = boardDTO.getUploadFile();

        String originalFileName = null;
        String savedFileName = null;
        String oldSavedFileName = null;

        // 수정 작업에서 새롭게 업로드된 파일이 있는 경우
        // 파일을 저장장치에 저장하고, 이름 추출
        if (!uploadFile.isEmpty()) {
            originalFileName = uploadFile.getOriginalFilename();
            savedFileName = FileService.saveFile(uploadFile, uploadPath);
        }

        // 수정된 내용과 비교를 위해 DB에서 데이터 가져옴
        Optional<JobBoardEntity> jobBoardEntity = jobBoardRepository.findById(boardDTO.getBoardId());
        if (jobBoardEntity.isPresent()) {
            JobBoardEntity entity = jobBoardEntity.get();
            oldSavedFileName = entity.getSavedFileName();

            // 기존 파일이 있고, 업로드 파일도 있다면, 원래 저장된 파일은 삭제 & 새로운 파일은 저장
            if (oldSavedFileName!=null && !uploadFile.isEmpty()) {
                // 기존 파일 저장장치에서 삭제
                String fullPath = uploadPath+"/"+oldSavedFileName;
                FileService.deleteFile(fullPath);
                // 새로운 파일 저장
                entity.setOriginalFileName(originalFileName);
                entity.setSavedFileName(savedFileName);
                
            }
            // 기존 파일은 없고, 새롭게 업로드한 파일이 있는 경우
            else if(oldSavedFileName==null && !uploadFile.isEmpty()){
                // 새로운 파일 저장
                entity.setOriginalFileName(originalFileName);
                entity.setSavedFileName(savedFileName);
            }

            // 기존 파일도 없고, 새로운 파일도 없으면 파일 처리 과정 생략
            // 나머지 정보 update (제목, 내용, 마감날짜, 모집인원) 
            entity.setTitle(boardDTO.getTitle());
            entity.setContent(boardDTO.getContent());
            entity.setDeadline(boardDTO.getDeadline());
            entity.setLimitNumber(boardDTO.getLimitNumber());
        }

    }


    /**
     *  전달받은 boardId에 해당하는 게시글 삭제하는 함수
     * @param boardId
     */
    public void deleteOne(Long boardId) {
        // 해당 게시글 읽어옴
        Optional<JobBoardEntity> jobBoardEntity = jobBoardRepository.findById(boardId);
        if (jobBoardEntity.isPresent()) {
            JobBoardEntity boardEntity = jobBoardEntity.get();

            String savedFileName = boardEntity.getSavedFileName();
            // 첨부파일이 있는 경우 파일 삭제
            if (savedFileName!=null) {
                String fullPath = uploadPath+"/"+savedFileName;
                FileService.deleteFile(fullPath);
            }
            // DB에서 게시글 삭제
            jobBoardRepository.deleteById(boardId);
        }
    }


    // ========================= 게시글 목록 ========================

    /**
     * 페이지 설정 함수
     * @param page
     * @param pageLimit
     * @return
     */
    private Pageable createPageRequest (int page, int pageLimit){
        return PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "createDate"));
    }

    /**
     * activity/recruit 게시판 데이터 조회 (repository에서 BoardListDTO로 바로 매핑해서 가져옴)
     * @param category
     * @param searchWord
     * @param pageable
     * @return
     */
    private Page<BoardListDTO> fetchJobBoards(BoardCategory category, String searchWord, Pageable pageable) {
        return jobBoardRepository.findBoardListByCategoryAndTitleContainingAndReportedIsFalse(category, searchWord, pageable);
    }

    /**
     * category가 activity/recruit에 해당하는 게시글 목록 DTO를 리스트로 반환하는 함수
     * @param category
     * @param pageable
     * @param searchWord
     * @return
     */
    public Page<BoardListDTO> selectActivityOrRecruitBoards(BoardCategory category, Pageable pageable, String searchWord) {
        int page = pageable.getPageNumber()-1; // 사용자가 요청한 페이지 (페이지 위치값 0부터 시작하므로 -1)
        Pageable pageRequest = createPageRequest(page, pageLimit); // 페이지 설정
        
        // JobBoardEntities 중에 카테고리에 해당하는 게시글들 중 제목에 searchWord가 들어간 게시글들 BoardListDTO로 반환
        // (activity나 recruit인 게시글이 생성될 때, deadline, limitNumber, currentNumber 정보가 없어도 무조건 JobBoardEntity에 값이 추가됨)
        return fetchJobBoards(category,searchWord, pageRequest);
        
    }
    
    /**
     * group 게시판 데이터 조회 (repository에서 BoardListDTO로 바로 매핑해서 가져옴)
     * @param category
     * @param searchWord
     * @param pageable
     * @return
     */
    private Page<BoardListDTO> fetchGroupBoards(String userGroup, String searchWord, Pageable pageable) {
        return boardRepository.findBoardListByMemberGroupAndTitleContaining(userGroup, searchWord, pageable);
    }
    
    /**
     * category가 group에 해당하는 게시글 목록 DTO를 반환하는 함수
     */
    public Page<BoardListDTO> selectGroupBoards(String userGroup, Pageable pageable, String searchWord) {
        int page = pageable.getPageNumber()-1; // 사용자가 요청한 페이지 (페이지 위치값 0부터 시작하므로 -1)
        Pageable pageRequest = createPageRequest(page, pageLimit); // 페이지 설정
        
        // group 카테고리의 BoardEntities 중 
        // memberGroup이 userGroup에 해당하는 게시글들 중 
        // 제목에 searchWord가 들어간 게시글들 BoardListDTO로 반환
        return fetchGroupBoards(userGroup,searchWord, pageRequest);
        
    }


    /**
     * code/project/free/info 게시판 데이터 조회 (repository에서 BoardListDTO로 바로 매핑해서 가져옴)
     * @param category
     * @param searchWord
     * @param pageable
     * @return
     */
    private Page<BoardListDTO> fetchBoards(BoardCategory category, String searchWord, Pageable pageable) {
        return boardRepository.findBoardListByCategoryAndTitleContaining(category, searchWord, pageable);
    }

    /**
     * category가 activity/recruit/group이 아닌 카테고리에 해당하는 게시글 목록 DTO를 리스트로 반환하는 함수
     * @param category
     * @param pageable
     * @param searchWord
     * @return
     */
    public Page<BoardListDTO> selectOtherCategoryBoards(BoardCategory category, Pageable pageable, String searchWord) {
        int page = pageable.getPageNumber()-1; // 사용자가 요청한 페이지 (페이지 위치값 0부터 시작하므로 -1)
        Pageable pageRequest = createPageRequest(page, pageLimit); // 페이지 설정
        
        // 카테고리에 해당하는 BoardEntities 중 제목에 searchWord가 들어간 게시글들 BoardListDTO로 반환
        return fetchBoards(category, searchWord, pageRequest);
    }


    
    // ======================== 게시글 신고 ========================

    /**
     * 해당 boardId에 해당하는 게시글의 reported 값을 true로 변환하는 함수
     * @param boardId
     */
    @Transactional
    public void updateRportedCount(Long boardId){
        Optional<JobBoardEntity> entity = jobBoardRepository.findById(boardId);
        
        if (entity.isPresent()) {
            JobBoardEntity jobBoardEntity = entity.get();
            //reported 값 true로 변경
            jobBoardEntity.setReported(true);
        }
    }

    /**
     * 게시글 신고 내용이 담긴 DTO를 Entity로 변환 후 DB에 저장하는 함수 
     * @param dto
     */
    public void insertJobBoardReported(BoardReportDTO dto) {

        // 신고당한 게시글 엔티티
        JobBoardEntity boardEntity = jobBoardRepository.findById(dto.getBoardId()).get();

        // 게시글 신고 DTO -> Entity 변환 후 DB에 저장
        BoardReportEntity entity = BoardReportEntity.toEntity(dto, boardEntity);
        jobBoardReportedRepository.save(entity);

        // 해당 게시글의 reported 컬럼 true로 변경
        updateRportedCount(dto.getBoardId());
    }









    
    
}
