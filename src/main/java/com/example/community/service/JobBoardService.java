package com.example.community.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.community.dto.JobBoardDTO;
import com.example.community.dto.JobBoardReportedDTO;
import com.example.community.dto.check.BoardCategory;
import com.example.community.entity.JobBoardEntity;
import com.example.community.entity.JobBoardReportedEntity;
import com.example.community.repository.JobBoardReportedRepository;
import com.example.community.repository.JobBoardRepository;
import com.example.community.repository.MemberRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JobBoardService {
    private final JobBoardRepository jobBoardRepository;
    private final MemberRepository memberRepository;
    private final JobBoardReportedRepository jobBoardReportedRepository;
    
    // ================== 기본 CRUD 함수 ==================

    // Create



    // Read
    /**
     * 전달받은 boardId에 해당하는 게시글 엔티티 반환하는 함수
     * @param boardId
     * @return
     */
    public JobBoardDTO getBoard(Long boardId) {
        JobBoardDTO dto = new JobBoardDTO();
        
        Optional<JobBoardEntity> jobBoardEntity = jobBoardRepository.findById(boardId);
        // 게시글 Entity->DTO변환
        if (jobBoardEntity.isPresent()) {
            JobBoardEntity entity = jobBoardEntity.get();
        
            dto = JobBoardDTO.toDTO(entity, entity.getMemberEntity().getMemberId());
        }
        return dto;
    }


    // Update
    /**
     * 수정된 게시글 DTO를 받아서 수정해서 DB에 저장하는 함수
     * @param jobBoardDTO
     */
    @Transactional
    public void update(JobBoardDTO jobBoardDTO){

        Optional<JobBoardEntity> jobBoardEntity = jobBoardRepository.findById(jobBoardDTO.getBoardId());
        if (jobBoardEntity.isPresent()) {
            JobBoardEntity entity = jobBoardEntity.get();

            // update (제목, 내용, 첨부파일 ,마감날짜, 모집인원)
            entity.setTitle(jobBoardDTO.getTitle());
            entity.setContent(jobBoardDTO.getContent());
            // 첨부파일
            entity.setDeadline(jobBoardDTO.getDeadline());
            entity.setLimitNumber(jobBoardDTO.getLimitNumber());
        }

    }


    // Delete
    /**
     *  전달받은 boardId에 해당하는 게시글 삭제하는 함수
     * @param boardId
     */
    public void delete(Long boardId) {
        jobBoardRepository.deleteById(boardId);
    }


    //======================================================


    // ===================== 게시글 목록 =====================
    
    /**
     * category에 해당하는 게시글을 List로 반환하는 함수
     * @param category
     * @return
     */
    public List<JobBoardDTO> getBoardList(BoardCategory category) {

        // 카테고리에 따른 게시글 엔티티 리스트 가져옴
        List<JobBoardEntity> entities = jobBoardRepository.findByCategoryOrderByCreateDateDesc(category);
        
        // DTO 리스트로 변환
        List<JobBoardDTO> dtos = new ArrayList<>();

        for (JobBoardEntity entity : entities) {
            dtos.add(JobBoardDTO.toDTO(entity, entity.getMemberEntity().getMemberId()));
        }

        return dtos;
    }

    
    // ===================== 게시글 신고 =====================

    /**
     * 해당 boardId에 해당하는 게시글의 reported 값을 1 증가시키는 함수
     * @param boardId
     */
    @Transactional
    public void updateRportedCount(Long boardId){
        Optional<JobBoardEntity> entity = jobBoardRepository.findById(boardId);
        
        if (entity.isPresent()) {
            JobBoardEntity jobBoardEntity = entity.get();
            //reported 값 1 증가
            jobBoardEntity.setReported(jobBoardEntity.getReported()+1);
        }
    }

    /**
     * 게시글 신고 내용이 담긴 DTO를 Entity로 변환 후 DB에 저장하는 함수 
     * @param dto
     */
    public void insertJobBoardReported(JobBoardReportedDTO dto) {
        // 신고당한 게시글 엔티티
        JobBoardEntity boardEntity = jobBoardRepository.findById(dto.getBoardId()).get();

        // 게시글 신고 DTO -> Entity 변환 후 DB에 저장
        JobBoardReportedEntity entity = JobBoardReportedEntity.toEntity(dto, boardEntity);
        jobBoardReportedRepository.save(entity);
    }

    
    
}
