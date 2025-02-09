package com.pofo.backend.domain.board.repository;

import com.pofo.backend.domain.board.entity.Board;
// import com.pofo.backend.domain.user.join.entity.Users;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


// import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
//     특정 사용자의 게시글을 페이징하여 조회 (상세조회시 필요)
//    Page<Board> findByUser_Id(Long id, Pageable pageable);
}