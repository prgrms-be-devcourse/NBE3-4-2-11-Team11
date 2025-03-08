package com.pofo.backend.domain.board.service;

import com.pofo.backend.common.rsData.RsData;
import com.pofo.backend.domain.board.dto.*;
import com.pofo.backend.domain.board.entity.Board;
import com.pofo.backend.domain.board.repository.BoardRepository;

import com.pofo.backend.domain.user.join.entity.User;
import com.pofo.backend.domain.user.join.repository.UserRepository;

import com.pofo.backend.common.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.data.domain.*;

import lombok.RequiredArgsConstructor;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import jakarta.persistence.EntityNotFoundException;


@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final UserRepository usersRepository;

    public RsData<BoardListResponseDto> getAllPosts(int page, int size) {
        size = Math.max(size, 1);
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

    public RsData<BoardResponseDto> getPostById(Long id) {
        Board board = findEntityOrThrow(boardRepository.findById(id), "게시글을 찾을 수 없습니다.");
        return new RsData<>("200", "게시글 조회 성공", new BoardResponseDto(board));
    }

    @Transactional
    public RsData<BoardResponseDto> createPost(BoardRequestDto requestDto) {
        User user = getCurrentUser();

        Board board = Board.builder()
                .user(user)
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .build();

        boardRepository.save(board);
        return new RsData<>("201", "게시글 작성 성공", new BoardResponseDto(board));
    }

    @Transactional
    public RsData<BoardResponseDto> updatePost(Long id, BoardRequestDto requestDto) {
        Long currentUserId = getCurrentUserId();
        Board board = findEntityOrThrow(boardRepository.findById(id), "게시글을 찾을 수 없습니다.");

        if (!board.getUser().getId().equals(currentUserId)) {
            throw new RuntimeException("403: 본인이 작성한 게시글만 수정할 수 있습니다.");
        }

        board.setTitle(requestDto.getTitle());
        board.setContent(requestDto.getContent());
        boardRepository.save(board);
        return new RsData<>("200", "게시글 수정 성공", new BoardResponseDto(board));
    }

//    @Transactional
//    public RsData<BoardDeleteResponseDto> deletePost(Long id) {
//        Long currentUserId = getCurrentUserId();
//        Board board = findEntityOrThrow(boardRepository.findById(id), "게시글을 찾을 수 없습니다.");
//
//        if (!board.getUser().getId().equals(currentUserId)) {
//            throw new RuntimeException("403: 본인이 작성한 게시글만 삭제할 수 있습니다.");
//        }
//
//        boardRepository.delete(board);
//        return new RsData<>("200", "게시글 삭제 성공", new BoardDeleteResponseDto("게시글이 삭제되었습니다."));
//    }


//@Transactional
//public RsData<BoardDeleteResponseDto> deletePost(Long id, BoardDeleteRequestDto requestDto) {
//    Board board = findEntityOrThrow(boardRepository.findById(id), "게시글을 찾을 수 없습니다.");
//
//    // ✅ 요청 바디에서 받은 userId와 게시글 작성자의 userId 비교
//    if (!board.getUser().getId().equals(requestDto.getUserId())) {
//        throw new RuntimeException("403: 본인이 작성한 게시글만 삭제할 수 있습니다.");
//    }
//
//    boardRepository.delete(board);
//    return new RsData<>("200", "게시글 삭제 성공", new BoardDeleteResponseDto("게시글이 삭제되었습니다."));
//}

    @Transactional
    public RsData<BoardDeleteResponseDto> deletePost(Long id) {
        Long currentUserId = getCurrentUserId(); // 현재 로그인한 사용자의 ID 가져오기
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("404: 게시글을 찾을 수 없습니다.")); // 게시글 존재 여부 확인

        // 작성자 검증: 로그인한 사용자 ID와 게시글 작성자의 ID 비교
        if (!board.getUser().getId().equals(currentUserId)) {
            throw new SecurityException("403: 본인이 작성한 게시글만 삭제할 수 있습니다."); // 예외 타입 변경 (보안 예외)
        }

        boardRepository.delete(board); // 게시글 삭제

        // 삭제 성공 메시지 반환
        return new RsData<>("200", "게시글 삭제 성공", new BoardDeleteResponseDto("게시글이 성공적으로 삭제되었습니다."));
    }




    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalStateException("현재 인증된 사용자를 찾을 수 없습니다.");
        }

        if (authentication.getPrincipal() instanceof CustomUserDetails customUserDetails) {
            return customUserDetails.getUser().getId();
        }

        throw new IllegalStateException("사용자 ID를 가져올 수 없습니다.");
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalStateException("현재 인증된 사용자를 찾을 수 없습니다.");
        }

        if (authentication.getPrincipal() instanceof CustomUserDetails customUserDetails) {
            return customUserDetails.getUser();
        }

        Long userId = getCurrentUserId();
        return usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("현재 사용자를 찾을 수 없습니다."));
    }

    private <T> T findEntityOrThrow(Optional<T> entity, String errorMessage) {
        return entity.orElseThrow(() -> new EntityNotFoundException("404: " + errorMessage));
    }
}
