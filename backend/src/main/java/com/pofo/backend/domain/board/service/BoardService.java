package com.pofo.backend.domain.board.service;

import com.pofo.backend.common.rsData.RsData;
import com.pofo.backend.domain.board.dto.*;
import com.pofo.backend.domain.board.entity.Board;
import com.pofo.backend.domain.board.repository.BoardRepository;
import com.pofo.backend.domain.user.join.entity.Users;
import com.pofo.backend.domain.user.join.repository.UsersRepository;
//import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final UsersRepository usersRepository;

    // 게시글 목록 조회 (페이징)
    public RsData<BoardListResponseDto> getAllPosts(int page, int size) {
        int currentPage = Math.max(page, 1) - 1; // 최소 1페이지 보장
        Pageable pageable = PageRequest.of(currentPage, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Board> boardPage = boardRepository.findAll(pageable);

        List<BoardResponseDto> boardResponses = boardPage.getContent().stream()
                .map(BoardResponseDto::new)
                .collect(Collectors.toList());

        return new RsData<>("200", "게시글 목록 조회 성공", new BoardListResponseDto(
                boardPage.getNumber() + 1,
                boardPage.getTotalPages(),
                boardPage.getTotalElements(),
                boardResponses
        ));
    }

    // 게시글 작성
    @Transactional
    public RsData<BoardResponseDto> createPost(BoardRequestDto requestDto) {
        Users user = usersRepository.findById(requestDto.getId())
                .orElse(null);

        if (user == null) {
            return new RsData<>("400", "사용자를 찾을 수 없습니다.");
        }
        // Board 엔티티 생성 및 저장
        Board board = Board.builder()
                .user(user)
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .build();

        boardRepository.save(board);
        return new RsData<>("200", "게시글 작성 성공", new BoardResponseDto(board));
    }

    // 게시글 수정
    @Transactional
    public RsData<BoardResponseDto> updatePost(Long id, BoardRequestDto requestDto) {
        Board board = boardRepository.findById(id)
                .orElse(null);

        if (board == null) {
            return new RsData<>("400", "게시글을 찾을 수 없습니다.");
        }

        board.setTitle(requestDto.getTitle());
        board.setContent(requestDto.getContent());

        boardRepository.save(board); // 수정 후 저장

        return new RsData<>("200", "게시글 수정 성공", new BoardResponseDto(board));
    }

    // 게시글 삭제
    @Transactional
    public RsData<BoardDeleteResponseDto> deletePost(Long id) {
        Board board = boardRepository.findById(id)
                .orElse(null);

        if (board == null) {
            return new RsData<>("400", "게시글을 찾을 수 없습니다.");
        }

        boardRepository.delete(board);
        return new RsData<>("200", "게시글 삭제 성공", new BoardDeleteResponseDto("게시글이 삭제되었습니다."));
    }
}

