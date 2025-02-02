package com.pofo.backend.domain.project.controller;

import com.pofo.backend.domain.project.dto.request.ProjectCreateRequest;
import com.pofo.backend.domain.project.dto.response.ProjectCreateResponse;
import com.pofo.backend.domain.project.service.ProjectService;
import com.pofo.backend.domain.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class ProjectController {

    private final ProjectService projectService;

    //프로젝트 등록
    @PostMapping("/project")
    public ProjectCreateResponse createProject(@Valid @RequestBody ProjectCreateRequest projectRequest, @AuthenticationPrincipal User user){
        return projectService.createProject(projectRequest, user);
    }

}
