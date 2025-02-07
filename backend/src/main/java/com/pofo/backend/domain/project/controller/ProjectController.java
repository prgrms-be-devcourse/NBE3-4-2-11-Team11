package com.pofo.backend.domain.project.controller;

import com.pofo.backend.common.base.Empty;
import com.pofo.backend.common.rsData.RsData;
import com.pofo.backend.domain.project.dto.request.ProjectCreateRequest;
import com.pofo.backend.domain.project.dto.request.ProjectUpdateRequest;
import com.pofo.backend.domain.project.dto.response.ProjectCreateResponse;
import com.pofo.backend.domain.project.dto.response.ProjectDetailResponse;
import com.pofo.backend.domain.project.dto.response.ProjectUpdateResponse;
import com.pofo.backend.domain.project.exception.ProjectCreationException;
import com.pofo.backend.domain.project.service.ProjectService;
import com.pofo.backend.domain.user.join.entity.User;
import com.pofo.backend.domain.user.join.repository.UserRepository;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
    public ResponseEntity<RsData<ProjectCreateResponse>> createProject(
            @Valid @RequestBody ProjectCreateRequest projectRequest, @AuthenticationPrincipal User user){

        //인증로직이 없어서 임시조치
        User u = userRepository.findById(null).orElseThrow(()->new ProjectCreationException("404",""));
        ProjectCreateResponse response = projectService.createProject(projectRequest, u);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new RsData<>("201", "프로젝트 등록이 완료되었습니다.", response));
    }

    //프로젝트 전체 조회
    @GetMapping("/projects")
    public ResponseEntity<RsData<List<ProjectDetailResponse>>> detailAllProject(@AuthenticationPrincipal User user){
        //인증로직이 없어서 임시조치
        User u = userRepository.findById(null).orElseThrow(()->new ProjectCreationException("404",""));

        List<ProjectDetailResponse> response = projectService.detailAllProject(u);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new RsData<>("200", "프로젝트 전체 조회가 완료되었습니다.", response));
    }

    //프로젝트 단건 조회
    @GetMapping("/projects/{projectId}")
    public ResponseEntity<RsData<ProjectDetailResponse>> detailProject(
            @PathVariable Long projectId,
            @AuthenticationPrincipal User user){

        User u = userRepository.findById(null).orElseThrow(()->new ProjectCreationException("404",""));
        ProjectDetailResponse response = projectService.detailProject(projectId, u);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new RsData<>("200","프로젝트 단건 조회가 완료되었습니다." , response));

    }

    //프로젝트 수정
    @PutMapping("/projects/{projectId}")
    public ResponseEntity<RsData<ProjectUpdateResponse>> updateProject(
            @PathVariable Long projectId,
            @Valid @RequestBody ProjectUpdateRequest request,
            @AuthenticationPrincipal User user
    ){
        //인증로직이 없어서 임시조치
        User u = userRepository.findById(null).orElseThrow(()->new ProjectCreationException("404",""));
        ProjectUpdateResponse response = projectService.updateProject(projectId, request, u);

        return ResponseEntity.status(HttpStatus.OK).body(new RsData<>("201", "프로젝트 수정이 완료되었습니다.", response));
    }

    //프로젝트 삭제
    @DeleteMapping("/projects/{projectId}")
    public ResponseEntity<RsData<Empty>> deleteProject(
            @PathVariable Long projectId,
            @AuthenticationPrincipal User user
    ){
        //인증로직이 없어서 임시조치
        User u = userRepository.findById(null).orElseThrow(()->new ProjectCreationException("404",""));

        projectService.deleteProject(projectId, u);

        return ResponseEntity.status(HttpStatus.OK).body(new RsData<>("200", "프로젝트 삭제가 완료되었습니다.", new Empty()));
    }

}
