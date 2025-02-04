package com.pofo.backend.domain.project.controller;

import com.pofo.backend.common.rsData.RsData;
import com.pofo.backend.domain.project.dto.request.ProjectCreateRequest;
import com.pofo.backend.domain.project.dto.response.ProjectCreateResponse;
import com.pofo.backend.domain.project.exception.ProjectCreationException;
import com.pofo.backend.domain.project.service.ProjectService;
import com.pofo.backend.domain.user.entity.User;
import com.pofo.backend.domain.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private final UserRepository userRepository;
    //프로젝트 등록
    @PostMapping("/project")
    public ResponseEntity<RsData<ProjectCreateResponse>> createProject(@Valid @RequestBody ProjectCreateRequest projectRequest, @AuthenticationPrincipal User user){

        //인증로직이 없어서 임시조치
        User u = userRepository.findById(null).orElseThrow(()->new ProjectCreationException("404",""));
        ProjectCreateResponse response = projectService.createProject(projectRequest, u);

        return ResponseEntity.status(HttpStatus.CREATED).body(new RsData<>("201", "프로젝트 등록이 완료되었습니다.", response));
    }


}
