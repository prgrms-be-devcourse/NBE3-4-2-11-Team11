package com.pofo.backend.domain.board.controller;

import com.pofo.backend.common.rsData.RsData;
import com.pofo.backend.domain.board.dto.*;
import com.pofo.backend.domain.board.service.BoardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    // 게시글 수정 (PATCH /api/v1/user/boards/{id})
    @PatchMapping("/{id}")
    public ResponseEntity<RsData<BoardResponseDto>> updatePost(
            @PathVariable Long id,
            @Valid @RequestBody BoardRequestDto requestDto) {
        return ResponseEntity.ok(boardService.updatePost(id, requestDto));
    }

    // 게시글 삭제 (DELETE /api/v1/user/boards/{id})
    @DeleteMapping("/{id}")
    public ResponseEntity<RsData<BoardDeleteResponseDto>> deletePost(@PathVariable Long id) {
        return ResponseEntity.ok(boardService.deletePost(id));
    }

//    // ✅ 요청 바디(`userId`)를 포함하여 처리하는 방식
//    @DeleteMapping("/{id}")
//    public ResponseEntity<RsData<BoardDeleteResponseDto>> deletePost(
//            @PathVariable Long id,
//            @RequestBody BoardDeleteRequestDto requestDto // ✅ 요청 바디 추가
//    ) {
//        return ResponseEntity.ok(boardService.deletePost(id, requestDto));
//    }

}
