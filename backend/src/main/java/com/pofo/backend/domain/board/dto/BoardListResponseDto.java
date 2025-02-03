package com.pofo.backend.domain.board.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

@Getter
@AllArgsConstructor
public class BoardListResponseDto {
    private int currentPage;
    private int totalPages;
    private long totalItems;
    private List<BoardResponseDto> boards;
}
