//package com.pofo.backend.domain.board.service;
//
//import com.pofo.backend.common.rsData.RsData;
//import com.pofo.backend.domain.board.dto.BoardDeleteResponseDto;
//import com.pofo.backend.domain.board.dto.BoardListResponseDto;
//import com.pofo.backend.domain.board.dto.BoardRequestDto;
//import com.pofo.backend.domain.board.dto.BoardResponseDto;
//import com.pofo.backend.domain.board.entity.Board;
//import com.pofo.backend.domain.board.repository.BoardRepository;
//import com.pofo.backend.domain.user.join.entity.User;
//import com.pofo.backend.domain.user.join.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.*;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//public class BoardService {
//    private final BoardRepository boardRepository;
//    private final UserRepository usersRepository;
//
//    // 게시글 목록 조회 (페이징)
//    public RsData<BoardListResponseDto> getAllPosts(int page, int size) {
//        Pageable pageable = PageRequest.of(Math.max(page, 1) - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
//        Page<Board> boardPage = boardRepository.findAll(pageable);
//
//        List<BoardResponseDto> boardResponses = boardPage.getContent().stream()
//                .map(BoardResponseDto::new)
//                .collect(Collectors.toList());
//
//        return new RsData<>("200", "게시글 목록 조회 성공", new BoardListResponseDto(
//                boardPage.getNumber() + 1,
//                boardPage.getTotalPages(),
//                boardPage.getTotalElements(),
//                boardResponses
//        ));
//    }
//
//    // 게시글 상세 조회
//    public RsData<BoardResponseDto> getPostById(Long id) {
//        Board board = findEntityOrThrow(boardRepository.findById(id), "게시글을 찾을 수 없습니다.");
//        return new RsData<>("200", "게시글 조회 성공", new BoardResponseDto(board));
//    }
//
//    // 게시글 작성
//    @Transactional
//    public RsData<BoardResponseDto> createPost(BoardRequestDto requestDto) {
//
//        // 게시글 생성
//        Board board = Board.builder()
//                .user(user)
//                .title(requestDto.getTitle())
//                .content(requestDto.getContent())
//                .build();
//
//        boardRepository.save(board);
//        return new RsData<>("201", "게시글 작성 성공", new BoardResponseDto(board));
//    }
//
////    @Transactional
////    public RsData<BoardResponseDto> createPost(BoardRequestDto requestDto) {
////        // 현재 SecurityContext에서 인증된 사용자의 이메일을 추출합니다.
////        String email = getCurrentUserEmail();
////        User user = findEntityOrThrow(usersRepository.findByEmail(email), "사용자를 찾을 수 없습니다.");
////
////        // 게시글 생성
////        Board board = Board.builder()
////                .user(user)
////                .title(requestDto.getTitle())
////                .content(requestDto.getContent())
////                .build();
////
////        boardRepository.save(board);
////        return new RsData<>("201", "게시글 작성 성공", new BoardResponseDto(board));
////    }
//
//    // 게시글 수정
//    @Transactional
//    public RsData<BoardResponseDto> updatePost(Long id, BoardRequestDto requestDto) {
//        Board board = findEntityOrThrow(boardRepository.findById(id), "게시글을 찾을 수 없습니다.");
//
//        board.setTitle(requestDto.getTitle());
//        board.setContent(requestDto.getContent());
//
//        boardRepository.save(board);
//        return new RsData<>("200", "게시글 수정 성공", new BoardResponseDto(board));
//    }
//
//    // 게시글 삭제
//    @Transactional
//    public RsData<BoardDeleteResponseDto> deletePost(Long id) {
//        Board board = findEntityOrThrow(boardRepository.findById(id), "게시글을 찾을 수 없습니다.");
//
//        boardRepository.delete(board);
//        return new RsData<>("200", "게시글 삭제 성공", new BoardDeleteResponseDto("게시글이 삭제되었습니다."));
//    }
//
//    // 현재 인증된 사용자의 이메일을 SecurityContextHolder를 통해 추출합니다.
//    private String getCurrentUserEmail() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
//            return ((UserDetails) authentication.getPrincipal()).getUsername();
//        }
//        throw new IllegalStateException("현재 인증된 사용자를 찾을 수 없습니다.");
//    }
//
//
//    // 공통 메서드: 엔티티 조회 & 예외 처리 (중복 코드 제거) 에러 400으로 고정
//    private <T> T findEntityOrThrow(Optional<T> entity, String errorMessage) {
//        return entity.orElseThrow(() -> new RuntimeException("400: " + errorMessage));
//    }
//}



//package com.pofo.backend.domain.board.service;
//
//import com.pofo.backend.common.rsData.RsData;
//import com.pofo.backend.common.security.CustomUserDetails;
//import com.pofo.backend.domain.board.dto.*;
//import com.pofo.backend.domain.board.entity.Board;
//import com.pofo.backend.domain.board.repository.BoardRepository;
//import com.pofo.backend.domain.user.join.entity.User;
//import com.pofo.backend.domain.user.join.repository.UserRepository;
//import jakarta.persistence.EntityNotFoundException;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.*;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//public class BoardService {
//    private final BoardRepository boardRepository;
//    private final UserRepository usersRepository;
//
//    // 게시글 목록 조회 (페이징)
//    public RsData<BoardListResponseDto> getAllPosts(int page, int size) {
//        size = Math.max(size, 1); // 최소값 보장
//        Pageable pageable = PageRequest.of(Math.max(page, 1) - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
//        Page<Board> boardPage = boardRepository.findAll(pageable);
//
//        List<BoardResponseDto> boardResponses = boardPage.getContent().stream()
//                .map(BoardResponseDto::new)
//                .collect(Collectors.toList());
//
//        return new RsData<>("200", "게시글 목록 조회 성공", new BoardListResponseDto(
//                boardPage.getNumber() + 1,
//                boardPage.getTotalPages(),
//                boardPage.getTotalElements(),
//                boardResponses
//        ));
//    }
//
//    // 게시글 상세 조회
//    public RsData<BoardResponseDto> getPostById(Long id) {
//        Board board = findEntityOrThrow(boardRepository.findById(id), "게시글을 찾을 수 없습니다.");
//        return new RsData<>("200", "게시글 조회 성공", new BoardResponseDto(board));
//    }
//
//    // 게시글 작성 (사용자 ID 기반 검증)
//    @Transactional
//    public RsData<BoardResponseDto> createPost(BoardRequestDto requestDto) {
//        User user = getCurrentUser();
//
//        Board board = Board.builder()
//                .user(user)  // 작성자 ID 포함
//                .title(requestDto.getTitle())
//                .content(requestDto.getContent())
//                .build();
//
//        boardRepository.save(board);
//        return new RsData<>("201", "게시글 작성 성공", new BoardResponseDto(board));
//    }
//
//    // 게시글 수정 (ID 기반 검증 추가)
//    @Transactional
//    public RsData<BoardResponseDto> updatePost(Long id, BoardRequestDto requestDto) {
//        Long currentUserId = getCurrentUserId();
//        Board board = findEntityOrThrow(boardRepository.findById(id), "게시글을 찾을 수 없습니다.");
//
//        if (!board.getUser().getId().equals(currentUserId)) {  // ID 비교
//            throw new RuntimeException("403: 해당 게시글을 수정할 권한이 없습니다.");
//        }
//
//        board.setTitle(requestDto.getTitle());
//        board.setContent(requestDto.getContent());
//        boardRepository.save(board);
//        return new RsData<>("200", "게시글 수정 성공", new BoardResponseDto(board));
//    }
//
//    // 게시글 삭제 (ID 기반 검증 추가)
//    @Transactional
//    public RsData<BoardDeleteResponseDto> deletePost(Long id) {
//        Long currentUserId = getCurrentUserId();
//        Board board = findEntityOrThrow(boardRepository.findById(id), "게시글을 찾을 수 없습니다.");
//
//        if (!board.getUser().getId().equals(currentUserId)) {  // ID 비교
//            throw new RuntimeException("403: 해당 게시글을 삭제할 권한이 없습니다.");
//        }
//
//        boardRepository.delete(board);
//        return new RsData<>("200", "게시글 삭제 성공", new BoardDeleteResponseDto("게시글이 삭제되었습니다."));
//    }
//
//    // 현재 로그인한 사용자의 ID 가져오기
//    private Long getCurrentUserId() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication != null && authentication.getPrincipal() instanceof String) {
//            return Long.parseLong((String) authentication.getPrincipal()); // ✅ userId 가져오기
//        }
//        throw new IllegalStateException("현재 인증된 사용자를 찾을 수 없습니다.");
//    }
//
//    // 현재 로그인한 사용자 가져오기 (UserRepository 기반 조회)
//    private User getCurrentUser() {
//        Long userId = getCurrentUserId();
//        return usersRepository.findById(userId)
//                .orElseThrow(() -> new IllegalStateException("현재 사용자를 찾을 수 없습니다."));
//    }
//
//    private <T> T findEntityOrThrow(Optional<T> entity, String errorMessage) {
//        return entity.orElseThrow(() -> new IllegalArgumentException("404: " + errorMessage));
//    }
//}


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

    @Transactional
    public RsData<BoardDeleteResponseDto> deletePost(Long id) {
        Long currentUserId = getCurrentUserId();
        Board board = findEntityOrThrow(boardRepository.findById(id), "게시글을 찾을 수 없습니다.");

        if (!board.getUser().getId().equals(currentUserId)) {
            throw new RuntimeException("403: 본인이 작성한 게시글만 삭제할 수 있습니다.");
        }

        boardRepository.delete(board);
        return new RsData<>("200", "게시글 삭제 성공", new BoardDeleteResponseDto("게시글이 삭제되었습니다."));
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
