package com.pofo.backend.domain.project.controller;

import com.pofo.backend.common.base.Empty;
import com.pofo.backend.common.rsData.RsData;
import com.pofo.backend.common.security.CustomUserDetails;
import com.pofo.backend.domain.project.dto.response.ProjectCreateResponse;
import com.pofo.backend.domain.project.dto.response.ProjectDetailResponse;
import com.pofo.backend.domain.project.dto.response.ProjectUpdateResponse;
import com.pofo.backend.domain.project.service.FileService;
import com.pofo.backend.domain.project.service.ProjectService;
import com.pofo.backend.domain.user.join.entity.User;
import com.pofo.backend.domain.user.join.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class ProjectController {

    private final ProjectService projectService;
    private final UserRepository userRepository;
    private final FileService fileService;

    @PostMapping(value = "/projects", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RsData<ProjectCreateResponse>> createProject(
            @RequestPart("projectRequest") String projectRequestJson,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        //log.info("📢 [createProject] 요청이 들어옴");
        //log.info("📢 [createProject] projectRequestJson 내용: {}", projectRequestJson);
        //log.info("📢 [createProject] 썸네일 파일: {}", (thumbnail != null ? thumbnail.getOriginalFilename() : "없음"));

        ProjectCreateResponse response = projectService.createProject(customUserDetails.getUser(), projectRequestJson, thumbnail);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new RsData<>("201", "프로젝트 등록이 완료되었습니다.", response)); // ✅ response 그대로 반환
    }


    //프로젝트 전체 조회
    @GetMapping("/projects")
    public ResponseEntity<RsData<List<ProjectDetailResponse>>> detailAllProject(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam(value = "keyword", required = false) String keyword) {
        //인증로직이 없어서 임시조치

        User user = customUserDetails.getUser();
        userRepository.findById(user.getId());

        List<ProjectDetailResponse> response = (keyword == null || keyword.isEmpty())
                ? projectService.detailAllProject(user)
                : projectService.searchProjectsByKeyword(user, keyword);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new RsData<>("200", "프로젝트 전체 조회가 완료되었습니다.", response));
    }

    //프로젝트 단건 조회
    @GetMapping("/projects/{projectId}")
    public ResponseEntity<RsData<ProjectDetailResponse>> detailProject(
            @PathVariable Long projectId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        User user = customUserDetails.getUser();
        userRepository.findById(user.getId());
        ProjectDetailResponse response = projectService.detailProject(projectId, user);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new RsData<>("200", "프로젝트 단건 조회가 완료되었습니다.", response));

    }

    //프로젝트 수정
    @PutMapping(value = "/projects/{projectId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RsData<ProjectUpdateResponse>> updateProject(
            @PathVariable Long projectId,
            @RequestPart(value = "projectRequest", required = false) String projectRequestJson,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail,
            @RequestPart(value = "deleteThumbnail", required = false) String deleteThumbnailStr,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        User user = customUserDetails.getUser();
        Boolean deleteThumbnail = deleteThumbnailStr != null && deleteThumbnailStr.equalsIgnoreCase("true");

        //요청 데이터 확인 로그 추가
        //System.out.println("📢 [updateProject] projectRequestJson 내용: " + projectRequestJson);
        //System.out.println("📢 [updateProject] 썸네일 파일: " + (thumbnail != null ? thumbnail.getOriginalFilename() : "없음"));
        //System.out.println("📢 [updateProject] deleteThumbnail 값: " + deleteThumbnail);

        ProjectUpdateResponse response = projectService.updateProject(projectId, projectRequestJson, user, thumbnail, deleteThumbnail);

        return ResponseEntity.status(HttpStatus.OK).body(new RsData<>("200", "✅ 프로젝트 업데이트 성공", response));
    }


    //프로젝트 삭제
    @DeleteMapping("/projects/{projectId}")
    public ResponseEntity<RsData<Empty>> deleteProject(
            @PathVariable Long projectId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        User user = customUserDetails.getUser();

        projectService.deleteProject(projectId, user);

        return ResponseEntity.status(HttpStatus.OK).body(new RsData<>("200", "프로젝트 삭제가 완료되었습니다.", new Empty()));
    }

    //휴지통으로 이동할 프로젝트 다중 선택
    @DeleteMapping("/projects")
    public ResponseEntity<RsData<Empty>> deleteMultipleProjects(
            @RequestParam List<Long> projectIds,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        User user = customUserDetails.getUser();
        projectService.moveToTrash(projectIds, user);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new RsData<>("200", "선택한 프로젝트가 휴지통으로 이동되었습니다.", new Empty()));
    }

    //휴지통 프로젝트 목록 조회
    @GetMapping("/projects/trash")
    public ResponseEntity<RsData<List<ProjectDetailResponse>>> getDeletedProjects(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        User user = customUserDetails.getUser();
        List<ProjectDetailResponse> deletedProjects = projectService.getDeletedProjects(user);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new RsData<>("200", "휴지통 조회가 완료되었습니다.", deletedProjects));
    }

    //휴지통 복원
    @PostMapping("/projects/restore")
    public ResponseEntity<RsData<String>> restoreProjects(
            @RequestParam List<Long> projectIds,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        User user = customUserDetails.getUser();
        projectService.restoreProjects(projectIds, user);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new RsData<>("200", "선택한 프로젝트가 복원되었습니다.", "success"));
    }

    //영구삭제
    @DeleteMapping("/projects/permanent")
    public ResponseEntity<RsData<String>> permanentlyDeleteProjects(
            @RequestParam List<Long> projectIds,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        User user = customUserDetails.getUser();
        projectService.permanentlyDeleteProjects(projectIds, user);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new RsData<>("200", "선택한 프로젝트가 영구 삭제되었습니다.", "success"));
    }


}
