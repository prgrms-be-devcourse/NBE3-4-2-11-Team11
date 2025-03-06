package com.pofo.backend.domain.board.controller;

import com.pofo.backend.common.rsData.RsData;
import com.pofo.backend.domain.board.dto.*;
import com.pofo.backend.domain.board.service.BoardService;
import com.pofo.backend.domain.user.join.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*") // 모든 도메인에서 요청 허용 (Next.js 연동 가능)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user/boards")
public class BoardController {
    private final BoardService boardService;

    // 게시글 목록 조회 (GET /api/v1/user/boards)
    @GetMapping
    public ResponseEntity<RsData<BoardListResponseDto>> getAllPosts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(boardService.getAllPosts(page, size));
    }

    // 게시글 상세 조회 (GET /api/v1/user/boards/{id})
    @GetMapping("/{id}")
    public ResponseEntity<RsData<BoardResponseDto>> getPostById(@PathVariable Long id) {
        return ResponseEntity.ok(boardService.getPostById(id));
    }

    // 게시글 작성 (POST /api/v1/user/boards)
    @PostMapping
    public ResponseEntity<RsData<BoardResponseDto>> createPost(@Valid @RequestBody BoardRequestDto requestDto)  {
        return ResponseEntity.status(201).body(boardService.createPost(requestDto));
    }

//    @PostMapping
//    public ResponseEntity<RsData<BoardResponseDto>> createPost(
//            @Valid @RequestBody BoardRequestDto requestDto,
//            @AuthenticationPrincipal User user // 🔥 로그인한 사용자 정보 자동 주입
//    ) {
//        // ✅ 디버깅: user가 null인지 확인
//        System.out.println("📢 게시글 작성 요청 도착");
//        System.out.println("🔹 로그인된 사용자: " + (user != null ? user.getEmail() : "null"));
//
//        if (user == null) { // 🔥 로그인하지 않은 경우
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                    .body(new RsData<>("401", "로그인이 필요합니다.", null));
//        }
//
//        return ResponseEntity.status(HttpStatus.CREATED)
//                .body(boardService.createPost(requestDto, user)); // 🔥 user 객체를 서비스로 전달
//    }




    // 게시글 수정 (PATCH /api/v1/user/boards/{id})
    @PatchMapping("/{id}")
    public ResponseEntity<RsData<BoardResponseDto>> updatePost(
            @PathVariable Long id,
            @Valid @RequestBody BoardRequestDto requestDto) {
        return ResponseEntity.ok(boardService.updatePost(id, requestDto));
    }

    // 게시글 삭제 (DELETE /api/v1/user/boards/{id})
    @DeleteMapping("/{id}")
    public ResponseEntity<RsData<BoardDeleteResponseDto>> deletePost(
            @PathVariable("id") Long boardId,  // ✅ 게시글 ID (Path Variable)
            @RequestBody BoardDeleteRequestDto requestDto // ✅ 유저 ID (Request Body)
    ) {
        return ResponseEntity.ok(boardService.deletePost(boardId, requestDto.getUserId()));
    }

}
