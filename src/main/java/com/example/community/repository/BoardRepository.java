package com.example.community.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.community.dto.check.BoardCategory;
import com.example.community.dto.combine.BoardListDTO;
import com.example.community.entity.BoardEntity;

public interface BoardRepository extends JpaRepository<BoardEntity, Long> {

        // 카테고리가 group이고, 전달받은 memberGroup에 해당하는 (신고당하지 않은) 게시글 리스트 반환 (최신순)
        @Query("SELECT b FROM BoardEntity b WHERE " +
                "b.category = 'group' AND " +
                "b.memberGroup =: userGroup AND " +
                "b.reported = false AND " +
                "LOWER(b.title) LIKE LOWER(CONCAT('%', :searchWord, '%'))")
        Page<BoardEntity> findByMemberGroupAndNotReportedAndTitleContaining(@Param("userGroup") String userGroup, @Param("searchWord") String searchWord, Pageable pageRequest);

        // 기수별 게시판 목록 (반환 타입 : BoardListDTO)
        @Query("SELECT new com.example.community.dto.display.BoardListDTO(" +
                "b.boardId, " +
                "b.memberEntity.memberId, " +
                "b.memberGroup, " +
                "b.title, " +
                "b.hitCount, " +
                "b.likeCount, " +
                "b.createDate, " +
                "NULL, " +  // deadline
                "0, " +      // limitNumber
                "0) " +      // currentNumber
                "FROM BoardEntity b " +
                "WHERE b.category = 'group' " +
                "AND b.memberGroup = :userGroup " +
                "AND b.reported = false " +
                "AND LOWER(b.title) LIKE LOWER(CONCAT('%', :searchWord, '%')) " +
                "ORDER BY b.createDate DESC")
        Page<BoardListDTO> findBoardListByMemberGroupAndTitleContaining(@Param("userGroup") String userGroup, 
                                                                        @Param("searchWord") String searchWord, 
                                                                        Pageable pageRequest);

        // 카테고리에 해당하는 (신고당하지 않은) 게시글 리스트 반환 (최신순)
        @Query("SELECT b FROM BoardEntity b WHERE " +
                "b.category = :category AND " +
                "b.reported = false AND " +
                "LOWER(b.title) LIKE LOWER(CONCAT('%', :searchWord, '%'))")
        Page<BoardEntity> findByCategoryTitleContainingAndReportedIsFalse(@Param("category") BoardCategory category, 
                @Param("searchWord") String searchWord, Pageable pageRequest);

        // code/project/free/info 게시판 목록(반환 타입 : BoardListDTO)
        @Query("SELECT new com.example.community.dto.display.BoardListDTO(" +
                "b.boardId, " +
                "b.memberEntity.memberId, " +
                "b.memberGroup, " +
                "b.title, " +
                "b.hitCount, " +
                "b.likeCount, " +
                "b.createDate, " +
                "NULL, " +  // deadline
                "0, " +      // limitNumber
                "0) " +      // currentNumber
                "FROM BoardEntity b " +
                "WHERE b.category = :category " +
                "AND b.reported = false " +
                "AND LOWER(b.title) LIKE LOWER(CONCAT('%', :searchWord, '%')) " +
                "ORDER BY b.createDate DESC")
        Page<BoardListDTO> findBoardListByCategoryAndTitleContaining(@Param("category") BoardCategory category, 
                                                                        @Param("searchWord") String searchWord, 
                                                                        Pageable pageRequest);

        // boardId에 해당하는 Entity의 likeCount를 1 증가시킴
        @Modifying
        @Query("UPDATE BoardEntity b SET b.likeCount = b.likeCount + 1 WHERE b.boardId = :boardId")
        void incrementLikeCount(Long boardId);
        
        // boardId에 해당하는 Entity의 likeCount를 1 감소시킴 (연산 시 likeCount가 0보다 작은 경우는 0으로 세팅)
        @Modifying
        @Query("UPDATE BoardEntity b " +
                "SET b.likeCount = CASE WHEN b.likeCount - 1 < 0 THEN 0 ELSE b.likeCount - 1 END " +
                "WHERE b.boardId = :boardId")
        void decrementLikeCount(Long boardId);


}
