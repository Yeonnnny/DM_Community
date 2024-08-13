package com.example.community.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.community.dto.BoardDTO;
import com.example.community.dto.BoardReportDTO;
import com.example.community.dto.JobBoardDTO;
import com.example.community.dto.check.BoardCategory;
import com.example.community.dto.combine.BoardListDTO;
import com.example.community.entity.BoardEntity;
import com.example.community.entity.BoardReportEntity;
import com.example.community.entity.JobBoardEntity;
import com.example.community.entity.LikeEntity;
import com.example.community.entity.MemberEntity;
import com.example.community.repository.BoardReportRepository;
import com.example.community.repository.BoardRepository;
import com.example.community.repository.JobBoardRecruitRepository;
import com.example.community.repository.JobBoardRepository;
import com.example.community.repository.LikeRepository;
import com.example.community.repository.MemberRepository;
import com.example.community.util.FileService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.description.ByteCodeElement.Member;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final JobBoardRepository jobBoardRepository;
    private final JobBoardRecruitRepository jobBoardRecruitRepository;
    private final BoardReportRepository boardReportedRepository;
    private final LikeRepository likeRepository;


    // 첨부 파일 경로 요청
    @Value("${spring.servlet.multipart.location}")
    String uploadPath;


    // 페이지 당 글의 개수
    @Value("${user.board.pageLimit}")
    int pageLimit; // 한 페이지 당 게시글 개수


    // ======================== 기본 CRUD ========================


    /**
     * 수정된 게시글 DTO를 받아서 수정해서 DB에 저장하는 함수
     * @param jobBoardDTO
     */
    @Transactional
    public void updateOne(BoardDTO boardDTO){
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
        Optional<BoardEntity> boardEntity = boardRepository.findById(boardDTO.getBoardId());
        if (boardEntity.isPresent()) {
            BoardEntity entity = boardEntity.get();
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
            // 나머지 정보 update (제목, 내용) 
            entity.setTitle(boardDTO.getTitle());
            entity.setContent(boardDTO.getContent());
            // entity.setDeadline(boardDTO.getDeadline());
            // entity.setLimitNumber(boardDTO.getLimitNumber());
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

    // ======================== 게시글 삭제 ========================

    /**
     * 전달받은 boardId에 해당하는 BoardEntity의 존재여부 확인하는 함수
     */
    private boolean isExist(Long boardId){
        return boardRepository.existsById(boardId);
    }

    /**
     * 전달받은 boardId에 해당하는 BoardEntity를 반환하는 함수
     */
    private BoardEntity selectBoardEntity(Long boardId){
        return boardRepository.findById(boardId).get();
    }

    /**
     * 전달받은 BoardEntity에 첨부파일이 있는지 확인하는 함수
     */
    private boolean isExistFile(BoardEntity boardEntity){
        return boardEntity.getSavedFileName() != null ? true:false;
    }

    /**
     * 전달받은 BoardEntity의 첨부파일 삭제하는 함수
     */
    private void deleteFile(BoardEntity boardEntity){
        String fullPath = uploadPath+"/"+boardEntity.getSavedFileName();
        FileService.deleteFile(fullPath);
    }

    /**
     * 전달받은 BoardEntity 삭제하는 함수
     */
    private void deleteBoardEntity(BoardEntity boardEntity){
        boardRepository.delete(boardEntity);
    }

    /**
     *  전달받은 boardId에 해당하는 게시글 삭제하는 함수
     * @param boardId
     */
    public void deleteOne(Long boardId) {
        // 해당 게시글 존재 여부 확인
        if (isExist(boardId)) {
            // 해당 게시글 가져오기
            BoardEntity entity = selectBoardEntity(boardId); 
            // 첨부파일 있는 경우 삭제
            if (isExistFile(entity))  deleteFile(entity);
            // 해당 게시글 삭제
            deleteBoardEntity(entity);
        }
    }
    

    // ======================== 게시글 생성 ========================
    
    /**
     * 전달받은 memberId에 해당하는 게시글 작성자 MemberEntity 반환하는 함수
     * @param memberId
     * @return
     */
    private MemberEntity findMemberEntity(String memberId){
        return memberRepository.findById(memberId).get();
    }

    /**
     * 전달받은 게시글 DTO의 첨부파일 여부 확인하는 함수 
     */
    private boolean uploadFileisExist(BoardDTO dto){
        return !dto.getUploadFile().isEmpty();
    }
    
    /**
     * 전달받은 게시글 DTO에 첨부파일이 존재하여 첨부파일 저장 및 파일명을 세팅하는 함수
     */
    private void saveFile(BoardDTO dto){
        String originalFileName = dto.getUploadFile().getOriginalFilename();
        String savedFileName = FileService.saveFile(dto.getUploadFile(), uploadPath);

        dto.setOriginalFileName(originalFileName);
        dto.setSavedFileName(savedFileName);
    }

    /**
     * 전달받은 게시글 DTO를 Entity로 변환 후 Board DB에 저장하는 함수
     */
    public void insertBoard(BoardDTO dto) {

        MemberEntity memberEntity = findMemberEntity(dto.getMemberId()); // 작성자 Entity

        // 첨부파일이 있으면 파일저장 및 파일명 세팅
        if (uploadFileisExist(dto)) {
            saveFile(dto);
        }

        // 게시글 DTO -> 엔티티 변환 후 DB 저장
        boardRepository.save(BoardEntity.toEntity(dto, memberEntity));
    }

    /**
     * 전달받은 JobBoardDTO를 Entity로 변환 후 JobBoard DB에 저장하고, 저장여부 반환하는 함수
     * @param dto
     * @return
     */
    public void insertJobBoard(JobBoardDTO dto) {
        BoardEntity boardEntity = selectBoardEntity(dto.getBoardId()); // 게시글 Entity 
        jobBoardRepository.save(JobBoardEntity.toEntity(dto, boardEntity)); // JobBoard에 저장 
    }


    
    // ======================== 게시글 조회 ========================

    /**
     * 전달받은 boardId에 해당하는 JobBoardEntity 반환하는 함수
     * @param boardId
     * @return
     */
    private JobBoardEntity selectJobBoardEntity (Long boardId){
        return jobBoardRepository.findById(boardId).get();
    }

    /**
     * 전달받은 boardId에 해당하는 게시글 DTO반환하는 함수 (job에 관련된 정보가 있는 경우는 해당 정보도 포함해 반환함)
     * @param boardId
     * @return
     */
    public BoardDTO selectOne(Long boardId) {
        BoardEntity boardEntity = selectBoardEntity(boardId); // BoardEntity
        
        // Entity -> DTO로 변환
        BoardDTO boardDTO = BoardDTO.toDTO(boardEntity, boardEntity.getMemberEntity().getMemberId());

        // activity/recruit 게시글인 경우, JobBoardEntity 값을 가져와서 BoardDTO에서 관련 속성값을 세팅
        if (boardDTO.getCategory()==BoardCategory.activity || boardDTO.getCategory()==BoardCategory.recruit) {
            JobBoardEntity jobBoardEntity = selectJobBoardEntity(boardId); // JobBoardEntity
            // deadline, limitNumber, currentNumber 값 세팅
            boardDTO.setDeadline(jobBoardEntity.getDeadline());
            boardDTO.setLimitNumber(jobBoardEntity.getLimitNumber());
            boardDTO.setCurrentNumber(jobBoardEntity.getCurrentNumber());
        }

        return boardDTO;
    }

    /**
     * 전달받은 게시글의 조회수 증가시키는 함수
     * @param boardId
     */
    public void increaseHitCount(Long boardId) {
        BoardEntity boardEntity = selectBoardEntity(boardId); // boardEntity
        boardEntity.setHitCount(boardEntity.getHitCount()+1); // 1 증가
    }


    /**
     * 전달받은 boardId에 해당하는 JobBoardEntity의 deadline과 현재시간을 비교해, deadline이 현재 시간보다 이전이면 true, 반대의 경우는 false를 반환하는 함수
     * @param boardId
     * @return
     */
    public boolean isDeadline(Long boardId) {
        JobBoardEntity jobBoardEntity = selectJobBoardEntity(boardId); // 해당 jobBoardEntity
        return jobBoardEntity.getDeadline().isAfter(LocalDateTime.now()) ? false : true; // deadline이 현재 시간보다 이전이면 true 반환
    }
    
    
    /**
     * 전달받은 boardId에 해당하는 JobBoardEntity의 limitNumber와 currentNumber를 비교해 limit수가 current수보다 작거나 같은 경우 true 반환 (큰 경우는 false 반환)하는 함수
     * @param boardId
     * @return
     */
    public boolean isExceededLimitNumber(Long boardId) {
        JobBoardEntity jobBoardEntity = selectJobBoardEntity(boardId); // 해당 jobBoardEntity
        return jobBoardEntity.getLimitNumber()<=jobBoardEntity.getCurrentNumber() ? true : false; // limit 수가 current 수보가 작나 같으면 true 반환
    }

    /**
     * JobBoardRecruit DB에 전달받은 정보에 대한 데이터 존재여부 반환하는 함수
     * @param boardId
     * @param memberId
     * @return 참여 O → true / 참여 X → false
     */
    public boolean isRecruited(Long boardId, String memberId) {
        BoardEntity boardEntity = selectBoardEntity(boardId);       // boardEntity
        MemberEntity memberEntity = selectMemberEntity(memberId);   // memberEntity
        return jobBoardRecruitRepository.findByBoardAndMember(boardEntity, memberEntity).isPresent();
    }
    

    // ======================== 게시글 좋아요 ========================
    
    /**
     * 전달받은 boardId에 해당하는 게시글의 좋아요수 반환하는 함수
     * @param boardId
     * @return
     */
    public long getLikeCount(Long boardId) {
        BoardEntity boardEntity = selectBoardEntity(boardId); // boardEntity
        return likeRepository.countByBoardEntity(boardEntity);
    }

    /**
     * 전달받은 memberId에 해당하는 MemberEntity 반환하는 함수
     * @param memberId
     * @return
     */
    private MemberEntity selectMemberEntity(String memberId){
        return memberRepository.findById(memberId).get();
    }

    /**
     * 전달받은 memberId에 해당하는 회원이 전달받은 boardId에 해당하는 게시글에 좋아요 눌렀는지 여부 반환하는 함수
     * @param boardId
     * @param memberId
     * @return 좋아요 설정된 상태 → true / 좋아요 해제된 상태 → false
     */
    public boolean isBoardLikedByMember(Long boardId, String memberId) {
        MemberEntity memberEntity = selectMemberEntity(memberId);
        BoardEntity boardEntity = selectBoardEntity(boardId);
        return likeRepository.findByMemberAndBoard(memberEntity, boardEntity).isPresent();
    }

    
    /**
     * member가 board에 대해 이미 좋아요를 눌렀던 상태라면 좋아요 해제하고, 좋아요가 해제된 상태라면 좋아요 설정하는 함수 
     * @param boardId
     * @param memberId
     * @return 좋아요 설정 → true / 좋아요 해제 → false
     */
    public boolean likeBoard(Long boardId, String memberId) {
        BoardEntity boardEntity = selectBoardEntity(boardId); // boardEntity
        MemberEntity memberEntity = selectMemberEntity(memberId); // memberEntity

        Optional<LikeEntity> likeEntity = likeRepository.findByMemberAndBoard(memberEntity, boardEntity);
        
        if (likeEntity.isPresent()) { // 좋아요가 이미 설정된 상태
            likeRepository.delete(likeEntity.get()); // 해당 데이터 삭제
            return false; 
        } else{ // 좋아요가 해제된 상태 
            LikeEntity like = new LikeEntity(); // LikeEntity 생성
            // LikeEntity 속성값 세팅
            like.setBoardEntity(boardEntity);
            like.setMemberEntity(memberEntity);
            // Like DB에 저장
            likeRepository.save(like);
            return true;
        }
    }

    
    // ======================== 게시글 신고 ========================

    /**
     * 게시글 신고 내용이 담긴 DTO를 Entity로 변환 후 DB에 저장하는 함수 
     * @param dto
     */
    public void insertJobBoardReported(BoardReportDTO dto) {
        BoardEntity boardEntity = selectBoardEntity(dto.getBoardId()); // boardEntity
        // 게시글 신고 DTO -> Entity 변환
        BoardReportEntity entity = BoardReportEntity.toEntity(dto, boardEntity);
        // BoardReported DB에 저장
        boardReportedRepository.save(entity);
    }

    /**
     * 해당 boardId에 해당하는 게시글의 reported 값을 true로 변환하는 함수
     * @param boardId
     */
    @Transactional
    public void updateRportedCount(Long boardId){
        BoardEntity boardEntity = selectBoardEntity(boardId);        
        //reported 값 true로 변경
        boardEntity.setReported(true);
    }




    




    


    


    





















    
    
}
