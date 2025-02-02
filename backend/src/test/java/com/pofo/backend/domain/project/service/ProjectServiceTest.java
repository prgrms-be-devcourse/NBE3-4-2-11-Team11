package com.pofo.backend.domain.project.service;

import com.pofo.backend.domain.project.dto.request.ProjectCreateRequest;
import com.pofo.backend.domain.project.dto.response.ProjectCreateResponse;
import com.pofo.backend.domain.project.exception.ProjectCreationException;
import com.pofo.backend.domain.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

public class ProjectServiceTest {

    @InjectMocks
    private ProjectService projectService;

    @Mock
    private User mockUser;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
        when(mockUser.getId()).thenReturn(1L);
    }

    private ProjectCreateRequest projectCreateRequest(){
        ProjectCreateRequest projectCreateRequest = new ProjectCreateRequest();


        projectCreateRequest.setName("PoFo : 포트폴리오 아카이빙 프로젝트");
        projectCreateRequest.setStartDate(new java.util.Date(Date.valueOf("2025-01-22").getTime()));
        projectCreateRequest.setEndDate(new java.util.Date(Date.valueOf("2025-02-13").getTime()));
        projectCreateRequest.setMemberCount(5);
        projectCreateRequest.setPosition(" 백엔드");
        projectCreateRequest.setRepositoryLink("testRepositoryLink");
        projectCreateRequest.setDescription("개발자 직무를 희망하는 사람들의 포트폴리오 및 이력서를 아카이빙할 수 있습니다.");
        projectCreateRequest.setImageUrl("sample.img");

        return projectCreateRequest;
    }

    @Test
    @DisplayName("프로젝트 둥록 성공")
    void t1(){
        ProjectCreateRequest request = projectCreateRequest();
        ProjectCreateResponse response = projectService.createProject(request, mockUser);
        assertEquals("프로젝트 등록이 완료되었습니다.", response.getMessage());
    }

    @Test
    @DisplayName("프로젝트 등록 실패 - 사용자 정보 없음")
    void t2(){
        when(mockUser.getId()).thenReturn(null);
        ProjectCreateRequest request = projectCreateRequest();
        try{
            projectService.createProject(request, mockUser);
        }catch (ProjectCreationException ex){
            assertEquals("404","사용자가 존재하지 않습니다.");
        }
    }

    @Test
    @DisplayName("프로젝트 등록 실패 - 예외 발생")
    void t3(){
        doThrow(new ProjectCreationException("400","프로젝트 등록 중 오류가 발생했습니다."))
                .when(mockUser).getId();
        ProjectCreateRequest request = projectCreateRequest();

        try{
            projectService.createProject(request, mockUser);
        }catch (RuntimeException ex){
            assertEquals("400","프로젝트 등록 중 오류가 발생했습니다.");
        }
    }

}
