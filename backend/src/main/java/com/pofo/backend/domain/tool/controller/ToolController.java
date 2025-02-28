package com.pofo.backend.domain.tool.controller;

import com.pofo.backend.common.rsData.RsData;
import com.pofo.backend.domain.tool.dto.ToolProjection;
import com.pofo.backend.domain.tool.service.ToolService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user/resume/tools")
public class ToolController {

    private final ToolService toolService;

    @GetMapping("")
    public ResponseEntity<RsData<List<ToolProjection>>> getTools() {
        return ResponseEntity.ok(new RsData<>("200", "툴 조회가 완료되었습니다.", toolService.getAllTools()));
    }
}
