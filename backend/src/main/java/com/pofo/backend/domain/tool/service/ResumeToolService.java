package com.pofo.backend.domain.tool.service;

import com.pofo.backend.domain.resume.resume.entity.Resume;
import com.pofo.backend.domain.resume.resume.exception.ResumeCreationException;
import com.pofo.backend.domain.resume.resume.repository.ResumeRepository;
import com.pofo.backend.domain.tool.entity.ResumeTool;
import com.pofo.backend.domain.tool.entity.Tool;
import com.pofo.backend.domain.tool.repository.ResumeToolRepository;
import com.pofo.backend.domain.tool.repository.ToolRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ResumeToolService {

    private final ResumeRepository resumeRepository;
    private final ToolRepository toolRepository;
    private final ResumeToolRepository resumeToolRepository;

    public void updateTools(Long resumeId, List<Long> tools) {
        resumeToolRepository.deleteByResumeId(resumeId);
        addTools(resumeId, tools);
    }

    public void addTools(Long resumeId, List<Long> toolIds) {
        Resume resume = resumeRepository.findById(resumeId)
            .orElseThrow(() -> new ResumeCreationException("이력서를 찾을 수 없습니다."));
        List<Tool> tools = toolRepository.findAllById(toolIds);

        List<ResumeTool> resumeTools = tools.stream()
            .map(tool -> ResumeTool.builder()
                .resume(resume)
                .tool(tool)
                .build())
            .collect(Collectors.toList());

        resumeToolRepository.saveAll(resumeTools);
    }
}