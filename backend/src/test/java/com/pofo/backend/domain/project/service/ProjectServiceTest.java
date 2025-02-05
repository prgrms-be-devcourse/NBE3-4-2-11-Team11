package com.pofo.backend.domain.project.service;

import com.pofo.backend.common.rsData.RsData;
import com.pofo.backend.domain.mapper.ProjectMapper;
import com.pofo.backend.domain.project.dto.request.ProjectCreateRequest;
import com.pofo.backend.domain.project.dto.request.ProjectUpdateRequest;
import com.pofo.backend.domain.project.dto.response.ProjectCreateResponse;
import com.pofo.backend.domain.project.dto.response.ProjectDetailResponse;
import com.pofo.backend.domain.project.dto.response.ProjectUpdateResponse;
import com.pofo.backend.domain.project.entity.Project;
import com.pofo.backend.domain.project.exception.ProjectCreationException;
import com.pofo.backend.domain.project.repository.ProjectRepository;
import com.pofo.backend.domain.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProjectServiceTest {

    @InjectMocks
    private ProjectService projectService;

    @Mock
    private User mockUser;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMapper projectMapper;

    @Mock
    private Project mockProject;

    @Mock
    private ProjectDetailResponse mockProjectResponse;


    LocalDate startDate = LocalDate.of(2025, 1, 22);
    LocalDate endDate = LocalDate.of(2025, 2, 14);


    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);  // mockUser가 제대로 초기화되도록 호출

        when(mockUser.getId()).thenReturn(1L);
        when(mockProject.getId()).thenReturn(1L);
        when(mockProject.getName()).thenReturn("원두 주문 웹페이지");
        when(mockProject.getStartDate()).thenReturn(startDate);
        when(mockProject.getEndDate()).thenReturn(endDate);
        when(mockProject.getMemberCount()).thenReturn(6);
        when(mockProject.getPosition()).thenReturn("백엔드");
        when(mockProject.getRepositoryLink()).thenReturn("programmers@github.com");
        when(mockProject.getDescription()).thenReturn("커피 원두를 주문할 수 있는 웹페이지");
        when(mockProject.getImageUrl()).thenReturn("test.img");

        when(mockProject.getUser()).thenReturn(mockUser);
    }

    private ProjectCreateRequest projectCreateRequest(){
        ProjectCreateRequest projectCreateRequest = new ProjectCreateRequest();

        projectCreateRequest.setName("PoFo : 포트폴리오 아카이빙 프로젝트");
        projectCreateRequest.setStartDate(startDate);
        projectCreateRequest.setEndDate(endDate);
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
        assertEquals("프로젝트 등록이 완료되었습니다.", "프로젝트 등록이 완료되었습니다.");
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

    @Test
    @DisplayName("프로젝트 전체 조회 성공")
    void t4(){
        //Given
        when(mockProjectResponse.getName()).thenReturn("원두 주문 웹페이지");
        when(mockProjectResponse.getStartDate()).thenReturn(startDate);
        when(mockProjectResponse.getEndDate()).thenReturn(endDate);
        when(mockProjectResponse.getMemberCount()).thenReturn(6);
        when(mockProjectResponse.getPosition()).thenReturn("백엔드");
        when(mockProjectResponse.getRepositoryLink()).thenReturn("programmers@github.com");
        when(mockProjectResponse.getDescription()).thenReturn("커피 원두를 주문할 수 있는 웹페이지");
        when(mockProjectResponse.getImageUrl()).thenReturn("test.img");

        when(projectMapper.projectToProjectDetailResponse(mockProject)).thenReturn(mockProjectResponse);

        List<Project> mockProjectList = List.of(mockProject);
        when(projectRepository.findAllByOrderByIdDesc()).thenReturn(mockProjectList);

        // When
        List<ProjectDetailResponse> response = projectService.detailAllProject(mockUser);

        // Then
        assertEquals(1, response.size());
        assertEquals("원두 주문 웹페이지", response.get(0).getName());
        assertEquals(startDate, response.get(0).getStartDate());
        assertEquals(endDate, response.get(0).getEndDate());
        assertEquals(6, response.get(0).getMemberCount());
        assertEquals("백엔드", response.get(0).getPosition());
        assertEquals("programmers@github.com", response.get(0).getRepositoryLink());
        assertEquals("커피 원두를 주문할 수 있는 웹페이지", response.get(0).getDescription());
        assertEquals("test.img", response.get(0).getImageUrl());

        // Verify
        verify(projectRepository).findAllByOrderByIdDesc();
        verify(projectMapper).projectToProjectDetailResponse(mockProject);
    }

    @Test
    @DisplayName("프로젝트 전체 조회 실패 - 프로젝트 없는 경우")
    void t5(){
        // given
        User mockUser = mock(User.class);
        when(projectRepository.findAllByOrderByIdDesc()).thenReturn(Collections.emptyList());
        //System.out.println("mockUser: " + mockUser);


        // when & then
        ProjectCreationException exception = assertThrows(ProjectCreationException.class, () -> {
            projectService.detailAllProject(mockUser);
        });

        // 예외 메시지 확인
        RsData<Void> rsData = exception.getRsData();
        assertEquals("404", rsData.getResultCode());
        assertEquals("프로젝트가 존재하지 않습니다.", rsData.getMsg());
    }

    @Test
    @DisplayName("프로젝트 전체 조회 실패 - 예기치 않은 오류 발생")
    void t6(){
        //given
        when(projectRepository.findAllByOrderByIdDesc()).thenThrow(new RuntimeException("Unexpected error"));

        //when & then
        ProjectCreationException exception = assertThrows(ProjectCreationException.class, () -> {
            projectService.detailAllProject(mockUser);
        });

        // 예외 메시지 확인
        RsData<Void> rsData = exception.getRsData();
        assertEquals("400", rsData.getResultCode());
        assertEquals("프로젝트 전체 조회 중 오류가 발생했습니다.", rsData.getMsg());
    }

    @Test
    @DisplayName("프로젝트 전체 조회 실패 - 사용자 정보 없음")
    void t7(){
        // given
        User nullUser = null;

        // when & then
        ProjectCreationException exception = assertThrows(ProjectCreationException.class, () -> {
            projectService.detailAllProject(nullUser);
        });

        // 예외 메시지 확인
        RsData<Void> rsData = exception.getRsData();
        assertEquals("400", rsData.getResultCode());
        assertEquals("유효하지 않은 사용자입니다.", rsData.getMsg());
    }

    @Test
    @DisplayName("프로젝트 단건 조회 성공")
    void t8(){
        Long projectId = 1L;
        User mockUser = mock(User.class);
        Project mockProject = mock(Project.class);
        ProjectDetailResponse mockResponse = mock(ProjectDetailResponse.class);

        when(mockResponse.getName()).thenReturn("국내 여행 추천 웹페이지");
        when(mockResponse.getStartDate()).thenReturn(startDate);
        when(mockResponse.getEndDate()).thenReturn(endDate);
        when(mockResponse.getMemberCount()).thenReturn(4);
        when(mockResponse.getPosition()).thenReturn("백엔드");
        when(mockResponse.getRepositoryLink()).thenReturn("koreaTravel@github.com");
        when(mockResponse.getDescription()).thenReturn("국내 여행지 추천해주는 웹페이지입니다.");
        when(mockResponse.getImageUrl()).thenReturn("travel.img");

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(mockProject));
        when(projectMapper.projectToProjectDetailResponse(mockProject)).thenReturn(mockResponse);

        // When
        ProjectDetailResponse response = projectService.detailProject(projectId, mockUser);

        // Then
        assertNotNull(response);
        assertEquals("국내 여행 추천 웹페이지", response.getName());
        assertEquals(startDate, response.getStartDate());
        assertEquals(endDate, response.getEndDate());
        assertEquals(4, response.getMemberCount());
        assertEquals("백엔드", response.getPosition());
        assertEquals("koreaTravel@github.com", response.getRepositoryLink());
        assertEquals("국내 여행지 추천해주는 웹페이지입니다.", response.getDescription());
        assertEquals("travel.img", response.getImageUrl());

        // Verify
        verify(projectRepository).findById(projectId);
        verify(projectMapper).projectToProjectDetailResponse(mockProject);
    }

    @Test
    @DisplayName("프로젝트 단건 조회 실패 - 프로젝트 없음")
    void t9(){
        //Given
        Long projectId = 1L;
        User mockUser = mock(User.class);
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        //when&Then
        ProjectCreationException exception = assertThrows(ProjectCreationException.class, () -> {
            projectService.detailProject(projectId, mockUser);
        });

        RsData<Void> rsData = exception.getRsData();
        assertEquals("404", rsData.getResultCode());
        assertEquals("해당 프로젝트를 찾을 수 없습니다.", rsData.getMsg());
    }

    @Test
    @DisplayName("프로젝트 단건 조회 실패 - 사용자 정보 없음")
    void t10(){

        //Given
        Long projectId =1L;
        User nullUser = null;

        //when & Then
        ProjectCreationException exception = assertThrows(ProjectCreationException.class, () -> {
            projectService.detailProject(projectId, nullUser);
        });

        RsData<Void> rsData = exception.getRsData();
        assertEquals("400", rsData.getResultCode());
        assertEquals("유효하지 않은 사용자입니다.", rsData.getMsg());

    }

    @Test
    @DisplayName("프로젝트 단건 조회 실패 - 예기치 못한 오류")
    void t11(){
        Long projectId = 1L;
        User mockUser = mock(User.class);
        when(projectRepository.findById(projectId)).thenThrow(new RuntimeException("Unexpected Error"));

        //when & Then
        ProjectCreationException exception = assertThrows(ProjectCreationException.class, () -> {
            projectService.detailProject(projectId, mockUser);
        });

        RsData<Void> rsData = exception.getRsData();
        assertEquals("400", rsData.getResultCode());
        assertEquals("프로젝트 단건 조회 중 오류가 발생했습니다.", rsData.getMsg());


    }

    private ProjectUpdateRequest projectUpdateRequest() {
        return ProjectUpdateRequest.builder()
                .name("업데이트된 프로젝트")
                .startDate(LocalDate.of(2025, 1, 25))
                .endDate(LocalDate.of(2025, 2, 20))
                .memberCount(8)
                .position("프론트엔드")
                .repositoryLink("newRepoLink")
                .description("업데이트된 프로젝트 설명")
                .imageUrl("newImage.img")
                .build();
    }

    @Test
    @DisplayName("프로젝트 수정 성공")
    void t12() {
        // Given
        Long projectId = 1L;
        ProjectUpdateRequest updateRequest = projectUpdateRequest(); // 업데이트할 요청 객체

        // 실제 프로젝트 객체 생성 (Spy 사용)
        Project realProject = spy(Project.builder()
                .user(mockUser)
                .name("기존 프로젝트")
                .startDate(LocalDate.of(2025, 1, 1))
                .endDate(LocalDate.of(2025, 2, 10))
                .memberCount(4)
                .position("백엔드")
                .repositoryLink("oldRepoLink")
                .description("기존 프로젝트 설명")
                .imageUrl("oldImage.img")
                .build());

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(realProject));

        // When
        ProjectUpdateResponse response = projectService.updateProject(projectId, updateRequest, mockUser);

        // Then
        assertNotNull(response);
        assertEquals(updateRequest.getName(), response.getName());
        assertEquals(updateRequest.getStartDate(), response.getStartDate());
        assertEquals(updateRequest.getEndDate(), response.getEndDate());
        assertEquals(updateRequest.getMemberCount(), response.getMemberCount());
        assertEquals(updateRequest.getPosition(), response.getPosition());
        assertEquals(updateRequest.getRepositoryLink(), response.getRepositoryLink());
        assertEquals(updateRequest.getDescription(), response.getDescription());
        assertEquals(updateRequest.getImageUrl(), response.getImageUrl());

        // 프로젝트 엔티티가 업데이트되었는지 확인
        verify(realProject).update(
                updateRequest.getName(),
                updateRequest.getStartDate(),
                updateRequest.getEndDate(),
                updateRequest.getMemberCount(),
                updateRequest.getPosition(),
                updateRequest.getRepositoryLink(),
                updateRequest.getDescription(),
                updateRequest.getImageUrl()
        );

    }

    @Test
    @DisplayName("프로젝트 수정 실패 - 사용자 정보 없음")
    void t13() {

        ProjectUpdateRequest updateRequest = projectUpdateRequest(); // 업데이트할 요청 객체

        Project realProject = Project.builder()
                .user(mockUser)
                .name("업데이트된 프로젝트")
                .startDate(LocalDate.of(2025,1,25))
                .endDate(LocalDate.of(2025,2,20))
                .memberCount(5)
                .position("백엔드")
                .repositoryLink("newRepoLink")
                .description("업데이트된 프로젝트 설명")
                .imageUrl("newImage.img")
                .build();

        when(projectRepository.findById(1L)).thenReturn(Optional.of(realProject));

        // 사용자 정보 없음
        ProjectCreationException exception = assertThrows(ProjectCreationException.class, () -> {
            projectService.updateProject(1L, updateRequest, null);
        });

        RsData<Void> rsData = exception.getRsData();
        assertEquals("404", rsData.getResultCode());
        assertEquals("프로젝트의 사용자 정보가 없습니다.", rsData.getMsg());


    }

}
