package com.pofo.backend.domain.skill.controller;

import com.pofo.backend.common.rsData.RsData;
import com.pofo.backend.domain.skill.dto.SkillProjection;
import com.pofo.backend.domain.skill.service.SkillService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user/resume/skills")
public class SkillController {

    private final SkillService skillService;

    @GetMapping("")
    public ResponseEntity<RsData<List<SkillProjection>>> getSkills() {
        return ResponseEntity.ok(new RsData<>("200", "스킬 조회가 완료되었습니다.", skillService.getAllSkills()));
    }
}
