package com.pofo.backend.domain.board.service;

import com.pofo.backend.common.rsData.RsData;
import com.pofo.backend.domain.board.dto.BoardDeleteResponseDto;
import com.pofo.backend.domain.board.dto.BoardListResponseDto;
import com.pofo.backend.domain.board.dto.BoardRequestDto;
import com.pofo.backend.domain.board.dto.BoardResponseDto;
import com.pofo.backend.domain.board.entity.Board;
import com.pofo.backend.domain.board.repository.BoardRepository;
import com.pofo.backend.domain.user.join.entity.User;
import com.pofo.backend.domain.user.join.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final UserRepository usersRepository;

    // 게시글 목록 조회 (페이징)
    public RsData<BoardListResponseDto> getAllPosts(int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 1) - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
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

    // 게시글 상세 조회
    public RsData<BoardResponseDto> getPostById(Long id) {
        Board board = findEntityOrThrow(boardRepository.findById(id), "게시글을 찾을 수 없습니다.");
        return new RsData<>("200", "게시글 조회 성공", new BoardResponseDto(board));
    }

    // 게시글 작성
    @Transactional
    public RsData<BoardResponseDto> createPost(BoardRequestDto requestDto) {
        User user = findEntityOrThrow(userRepository.findById(requestDto.getId()),  "사용자를 찾을 수 없습니다.");

        Board board = Board.builder()
                .user(user)
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .build();

        boardRepository.save(board);
        return new RsData<>("201", "게시글 작성 성공", new BoardResponseDto(board));
    }

    // 게시글 수정
    @Transactional
    public RsData<BoardResponseDto> updatePost(Long id, BoardRequestDto requestDto) {
        Board board = findEntityOrThrow(boardRepository.findById(id), "게시글을 찾을 수 없습니다.");

        board.setTitle(requestDto.getTitle());
        board.setContent(requestDto.getContent());

        boardRepository.save(board);
        return new RsData<>("200", "게시글 수정 성공", new BoardResponseDto(board));
    }

    // 게시글 삭제
    @Transactional
    public RsData<BoardDeleteResponseDto> deletePost(Long id) {
        Board board = findEntityOrThrow(boardRepository.findById(id), "게시글을 찾을 수 없습니다.");

        boardRepository.delete(board);
        return new RsData<>("200", "게시글 삭제 성공", new BoardDeleteResponseDto("게시글이 삭제되었습니다."));
    }


    // 공통 메서드: 엔티티 조회 & 예외 처리 (중복 코드 제거) 에러 400으로 고정
    private <T> T findEntityOrThrow(Optional<T> entity, String errorMessage) {
        return entity.orElseThrow(() -> new RuntimeException("400: " + errorMessage));
    }
}
