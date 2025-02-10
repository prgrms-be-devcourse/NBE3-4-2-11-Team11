package com.pofo.backend.domain.project.service;

import com.pofo.backend.common.rsData.RsData;
import com.pofo.backend.domain.mapper.ProjectMapper;
import com.pofo.backend.domain.project.dto.request.ProjectCreateRequest;
import com.pofo.backend.domain.project.dto.response.ProjectCreateResponse;
import com.pofo.backend.domain.project.dto.response.ProjectDetailResponse;
import com.pofo.backend.domain.project.entity.Project;
import com.pofo.backend.domain.project.exception.ProjectCreationException;
import com.pofo.backend.domain.project.repository.ProjectRepository;
import com.pofo.backend.domain.skill.entity.ProjectSkill;
import com.pofo.backend.domain.skill.entity.Skill;
import com.pofo.backend.domain.skill.service.SkillService;
import com.pofo.backend.domain.tool.entity.ProjectTool;
import com.pofo.backend.domain.tool.entity.Tool;
import com.pofo.backend.domain.tool.service.ToolService;
import com.pofo.backend.domain.user.join.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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

    @Mock
    private SkillService skillService;

    @Mock
    private ToolService toolService;

    LocalDate startDate = LocalDate.of(2025, 1, 22);
    LocalDate endDate = LocalDate.of(2025, 2, 14);


    @BeforeEach
    void setUp() {
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


        // Mock Skill & Tool 객체 생성
        when(skillService.getSkillByName("Java")).thenReturn(new Skill("Java"));
        when(skillService.getSkillByName("Spring Boot")).thenReturn(new Skill("Spring Boot"));
        when(toolService.getToolByName("IntelliJ IDEA")).thenReturn(new Tool("IntelliJ IDEA"));
        when(toolService.getToolByName("Docker")).thenReturn(new Tool("Docker"));
    }

    private ProjectCreateRequest projectCreateRequest() {
        return ProjectCreateRequest.builder()
                .name("PoFo 프로젝트")
                .startDate(startDate)
                .endDate(endDate)
                .memberCount(5)
                .position("백엔드")
                .repositoryLink("testRepositoryLink")
                .description("프로젝트 설명")
                .imageUrl("sample.img")
                .skillNames(List.of("Java", "Spring Boot")) // 추가
                .toolNames(List.of("IntelliJ IDEA", "Docker")) // 추가
                .build();
    }

    @Test
    @DisplayName("프로젝트 등록 성공")
    void t1() {
        // Given
        ProjectCreateRequest request = projectCreateRequest();

        // Project 객체 생성 시 skills & tools 추가
        List<ProjectSkill> projectSkills = request.getSkillNames().stream()
                .map(skillName -> new ProjectSkill(null, skillService.getSkillByName(skillName)))
                .collect(Collectors.toList());

        List<ProjectTool> projectTools = request.getToolNames().stream()
                .map(toolName -> new ProjectTool(null, toolService.getToolByName(toolName)))
                .collect(Collectors.toList());

        Project realProject = spy(Project.builder()
                .user(mockUser)
                .name(request.getName())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .memberCount(request.getMemberCount())
                .position(request.getPosition())
                .repositoryLink(request.getRepositoryLink())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .projectSkills(projectSkills)
                .projectTools(projectTools)
                .build());

        when(projectRepository.save(any(Project.class))).thenReturn(realProject);

        // When
        ProjectCreateResponse response = projectService.createProject(request, mockUser);

        // ✅ Then (로직이 정상적으로 실행되었는지만 검증)
        assertNotNull(response); // 응답 객체가 null이 아니어야 함
        verify(projectRepository).save(any(Project.class)); // 저장 메서드 호출 확인

        assertEquals(request.getSkillNames().size(), realProject.getProjectSkills().size());
        assertEquals(request.getToolNames().size(), realProject.getProjectTools().size());

        // Skill 및 Tool이 요청한 값과 동일한지 확인
        assertTrue(realProject.getProjectSkills().stream()
                .map(ps -> ps.getSkill().getName())
                .collect(Collectors.toList())
                .containsAll(request.getSkillNames()));

        assertTrue(realProject.getProjectTools().stream()
                .map(pt -> pt.getTool().getName())
                .collect(Collectors.toList())
                .containsAll(request.getToolNames()));
    }

    @Test
    @DisplayName("프로젝트 등록 실패 - 예외 발생")
    void t3(){
        doThrow(new ProjectCreationException("400","프로젝트 등록 중 오류가 발생했습니다."))
                .when(mockUser).getId();
        ProjectCreateRequest request = projectCreateRequest();

        // when & then
        ProjectCreationException exception = assertThrows(ProjectCreationException.class, () -> {
            projectService.createProject(request, mockUser);
        });

        // RsData 확인
        RsData<Void> rsData = exception.getRsData();
        assertEquals("400", rsData.getResultCode());
        assertEquals("프로젝트 등록 중 오류가 발생했습니다.", rsData.getMessage());
    }
//
//    @Test
//    @DisplayName("프로젝트 전체 조회 성공")
//    void t4(){
//        //Given
//        when(mockProjectResponse.getName()).thenReturn("원두 주문 웹페이지");
//        when(mockProjectResponse.getStartDate()).thenReturn(startDate);
//        when(mockProjectResponse.getEndDate()).thenReturn(endDate);
//        when(mockProjectResponse.getMemberCount()).thenReturn(6);
//        when(mockProjectResponse.getPosition()).thenReturn("백엔드");
//        when(mockProjectResponse.getRepositoryLink()).thenReturn("programmers@github.com");
//        when(mockProjectResponse.getDescription()).thenReturn("커피 원두를 주문할 수 있는 웹페이지");
//        when(mockProjectResponse.getImageUrl()).thenReturn("test.img");
//
//        when(projectMapper.projectToProjectDetailResponse(mockProject)).thenReturn(mockProjectResponse);
//
//        List<Project> mockProjectList = List.of(mockProject);
//        when(projectRepository.findAllByOrderByIdDesc()).thenReturn(mockProjectList);
//
//        // When
//        List<ProjectDetailResponse> response = projectService.detailAllProject(mockUser);
//
//        // Then
//        assertEquals(1, response.size());
//        assertEquals("원두 주문 웹페이지", response.get(0).getName());
//        assertEquals(startDate, response.get(0).getStartDate());
//        assertEquals(endDate, response.get(0).getEndDate());
//        assertEquals(6, response.get(0).getMemberCount());
//        assertEquals("백엔드", response.get(0).getPosition());
//        assertEquals("programmers@github.com", response.get(0).getRepositoryLink());
//        assertEquals("커피 원두를 주문할 수 있는 웹페이지", response.get(0).getDescription());
//        assertEquals("test.img", response.get(0).getImageUrl());
//
//        // Verify
//        verify(projectRepository).findAllByOrderByIdDesc();
//        verify(projectMapper).projectToProjectDetailResponse(mockProject);
//    }
//
//    @Test
//    @DisplayName("프로젝트 전체 조회 실패 - 프로젝트 없는 경우")
//    void t5(){
//        // given
//        User mockUser = mock(User.class);
//        when(projectRepository.findAllByOrderByIdDesc()).thenReturn(Collections.emptyList());
//        //System.out.println("mockUser: " + mockUser);
//
//
//        // when & then
//        ProjectCreationException exception = assertThrows(ProjectCreationException.class, () -> {
//            projectService.detailAllProject(mockUser);
//        });
//
//        // 예외 메시지 확인
//        RsData<Void> rsData = exception.getRsData();
//        assertEquals("404", rsData.getResultCode());
//        assertEquals("프로젝트가 존재하지 않습니다.", rsData.getMessage());
//    }
//
//    @Test
//    @DisplayName("프로젝트 전체 조회 실패 - 예기치 않은 오류 발생")
//    void t6(){
//        //given
//        when(projectRepository.findAllByOrderByIdDesc()).thenThrow(new RuntimeException("Unexpected error"));
//
//        //when & then
//        ProjectCreationException exception = assertThrows(ProjectCreationException.class, () -> {
//            projectService.detailAllProject(mockUser);
//        });
//
//        // 예외 메시지 확인
//        RsData<Void> rsData = exception.getRsData();
//        assertEquals("400", rsData.getResultCode());
//        assertEquals("프로젝트 전체 조회 중 오류가 발생했습니다.", rsData.getMessage());
//    }
//
//
//
//    @Test
//    @DisplayName("프로젝트 단건 조회 성공")
//    void t7(){
//        Long projectId = 1L;
//        User ownUser = mock(User.class);
//        when(ownUser.getId()).thenReturn(3L);
//
//        Project mockProject = mock(Project.class);
//        ProjectDetailResponse mockResponse = mock(ProjectDetailResponse.class);
//
//        when(mockProject.getUser()).thenReturn(ownUser);
//        when(mockResponse.getName()).thenReturn("국내 여행 추천 웹페이지");
//        when(mockResponse.getStartDate()).thenReturn(startDate);
//        when(mockResponse.getEndDate()).thenReturn(endDate);
//        when(mockResponse.getMemberCount()).thenReturn(4);
//        when(mockResponse.getPosition()).thenReturn("백엔드");
//        when(mockResponse.getRepositoryLink()).thenReturn("koreaTravel@github.com");
//        when(mockResponse.getDescription()).thenReturn("국내 여행지 추천해주는 웹페이지입니다.");
//        when(mockResponse.getImageUrl()).thenReturn("travel.img");
//
//        when(projectRepository.findById(projectId)).thenReturn(Optional.of(mockProject));
//        when(projectMapper.projectToProjectDetailResponse(mockProject)).thenReturn(mockResponse);
//
//        // When
//        ProjectDetailResponse response = projectService.detailProject(projectId, ownUser);
//
//        // Then
//        assertNotNull(response);
//        assertEquals("국내 여행 추천 웹페이지", response.getName());
//        assertEquals(startDate, response.getStartDate());
//        assertEquals(endDate, response.getEndDate());
//        assertEquals(4, response.getMemberCount());
//        assertEquals("백엔드", response.getPosition());
//        assertEquals("koreaTravel@github.com", response.getRepositoryLink());
//        assertEquals("국내 여행지 추천해주는 웹페이지입니다.", response.getDescription());
//        assertEquals("travel.img", response.getImageUrl());
//
//        // Verify
//        verify(projectRepository).findById(projectId);
//        verify(projectMapper).projectToProjectDetailResponse(mockProject);
//    }
//
//    @Test
//    @DisplayName("프로젝트 단건 조회 실패 - 프로젝트 없음")
//    void t8(){
//        //Given
//        Long projectId = 1L;
//        User mockUser = mock(User.class);
//        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());
//
//        //when&Then
//        ProjectCreationException exception = assertThrows(ProjectCreationException.class, () -> {
//            projectService.detailProject(projectId, mockUser);
//        });
//
//        RsData<Void> rsData = exception.getRsData();
//        assertEquals("404", rsData.getResultCode());
//        assertEquals("해당 프로젝트를 찾을 수 없습니다.", rsData.getMessage());
//    }
//
//
//
//
//    @Test
//    @DisplayName("프로젝트 단건 조회 실패 - 예기치 못한 오류")
//    void t9(){
//        Long projectId = 1L;
//        User mockUser = mock(User.class);
//        when(projectRepository.findById(projectId)).thenThrow(new RuntimeException("Unexpected Error"));
//
//        //when & Then
//        ProjectCreationException exception = assertThrows(ProjectCreationException.class, () -> {
//            projectService.detailProject(projectId, mockUser);
//        });
//
//        RsData<Void> rsData = exception.getRsData();
//        assertEquals("400", rsData.getResultCode());
//        assertEquals("프로젝트 단건 조회 중 오류가 발생했습니다.", rsData.getMessage());
//
//
//    }
//
//    private ProjectUpdateRequest projectUpdateRequest() {
//        return ProjectUpdateRequest.builder()
//                .name("업데이트된 프로젝트")
//                .startDate(LocalDate.of(2025, 1, 25))
//                .endDate(LocalDate.of(2025, 2, 20))
//                .memberCount(8)
//                .position("프론트엔드")
//                .repositoryLink("newRepoLink")
//                .description("업데이트된 프로젝트 설명")
//                .imageUrl("newImage.img")
//                .build();
//    }
//
//    @Test
//    @DisplayName("프로젝트 수정 성공")
//    void t10() {
//        // Given
//        Long projectId = 1L;
//        ProjectUpdateRequest updateRequest = projectUpdateRequest(); // 업데이트할 요청 객체
//
//        // 실제 프로젝트 객체 생성 (Spy 사용)
//        Project realProject = spy(Project.builder()
//                .user(mockUser)
//                .name("기존 프로젝트")
//                .startDate(LocalDate.of(2025, 1, 1))
//                .endDate(LocalDate.of(2025, 2, 10))
//                .memberCount(4)
//                .position("백엔드")
//                .repositoryLink("oldRepoLink")
//                .description("기존 프로젝트 설명")
//                .imageUrl("oldImage.img")
//                .build());
//
//        when(projectRepository.findById(projectId)).thenReturn(Optional.of(realProject));
//
//        // When
//        ProjectUpdateResponse response = projectService.updateProject(projectId, updateRequest, mockUser);
//
//        // Then
//        assertNotNull(response);
//        assertEquals(updateRequest.getName(), response.getName());
//        assertEquals(updateRequest.getStartDate(), response.getStartDate());
//        assertEquals(updateRequest.getEndDate(), response.getEndDate());
//        assertEquals(updateRequest.getMemberCount(), response.getMemberCount());
//        assertEquals(updateRequest.getPosition(), response.getPosition());
//        assertEquals(updateRequest.getRepositoryLink(), response.getRepositoryLink());
//        assertEquals(updateRequest.getDescription(), response.getDescription());
//        assertEquals(updateRequest.getImageUrl(), response.getImageUrl());
//
//        // 프로젝트 엔티티가 업데이트되었는지 확인
//        verify(realProject).update(
//                updateRequest.getName(),
//                updateRequest.getStartDate(),
//                updateRequest.getEndDate(),
//                updateRequest.getMemberCount(),
//                updateRequest.getPosition(),
//                updateRequest.getRepositoryLink(),
//                updateRequest.getDescription(),
//                updateRequest.getImageUrl()
//        );
//
//    }
//
//
//
//
//    @Test
//    @DisplayName("프로젝트 수정 실패 - 프로젝트 없음")
//    void t11(){
//        ProjectUpdateRequest updateRequest = projectUpdateRequest(); // 업데이트할 요청 객체
//
//        when(projectRepository.findById(1L)).thenReturn(Optional.empty()); // 프로젝트 없음 처리
//
//        ProjectCreationException exception = assertThrows(ProjectCreationException.class, () -> {
//            projectService.updateProject(1L, updateRequest, mockUser);
//        });
//
//        RsData<Void> rsData = exception.getRsData();
//        assertEquals("404", rsData.getResultCode());
//        assertEquals("해당 프로젝트를 찾을 수 없습니다.", rsData.getMessage());
//    }
//
//    @Test
//    @DisplayName("프로젝트 수정 실패 - 권한 없음")
//    void t12() {
//        ProjectUpdateRequest updateRequest = projectUpdateRequest(); // 업데이트할 요청 객체
//        User differentUser = Mockito.mock(User.class); // 다른 사용자
//
//        // Project를 Mock 객체로 생성
//        Project mockProject = Mockito.mock(Project.class);
//
//        when(projectRepository.findById(1L)).thenReturn(Optional.of(mockProject));
//        when(mockProject.getUser()).thenReturn(differentUser); // mockUser와 다르게 설정
//
//        // 예외 발생 여부 확인
//        ProjectCreationException exception = assertThrows(ProjectCreationException.class, () -> {
//            projectService.updateProject(1L, updateRequest, mockUser);
//        });
//
//        RsData<Void> rsData = exception.getRsData();
//        assertEquals("403", rsData.getResultCode());
//        assertEquals("프로젝트 수정 할 권한이 없습니다.", rsData.getMessage());
//    }
//
//    @Test
//    @DisplayName("프로젝트 삭제 성공")
//    void t13() {
//        Long projectId=1L;
//
//        Project mockProject = Mockito.mock(Project.class);
//        when(projectRepository.findById(projectId)).thenReturn(Optional.of(mockProject));
//        when(mockProject.getUser()).thenReturn(mockUser); // 사용자 일치
//
//        // When
//        assertDoesNotThrow(() -> projectService.deleteProject(projectId, mockUser));
//
//        // Then
//        verify(projectRepository).delete(mockProject); // 삭제 확인
//
//    }
//
//
//    @Test
//    @DisplayName("프로젝트 삭제 실패 - 삭제 권한 없음")
//    void t14(){
//
//        Long projectId=1L;
//
//        Project mockProject = Mockito.mock(Project.class);
//        User differentUser = Mockito.mock(User.class); // 다른 사용자
//        when(projectRepository.findById(projectId)).thenReturn(Optional.of(mockProject));
//        when(mockProject.getUser()).thenReturn(differentUser); // 사용자 불일치
//
//        // When & Then
//        ProjectCreationException exception = assertThrows(ProjectCreationException.class, () -> {
//            projectService.deleteProject(projectId, mockUser);
//        });
//
//        RsData<Void> rsData = exception.getRsData();
//        assertEquals("403", rsData.getResultCode());
//        assertEquals("프로젝트 삭제 할 권한이 없습니다.", rsData.getMessage());
//    }
//
//    @Test
//    @DisplayName("프로젝트 삭제 실패 - 데이터베이스 오류 발생")
//    void t15(){
//
//        Long projectId=1L;
//
//        // Given
//        Project mockProject = Mockito.mock(Project.class);
//        when(projectRepository.findById(projectId)).thenReturn(Optional.of(mockProject));
//        when(mockProject.getUser()).thenReturn(mockUser);
//        doThrow(new DataAccessException("DB 오류"){}).when(projectRepository).delete(mockProject);
//
//        // When & Then
//        ProjectCreationException exception = assertThrows(ProjectCreationException.class, () -> {
//            projectService.deleteProject(projectId, mockUser);
//        });
//
//        RsData<Void> rsData = exception.getRsData();
//        assertEquals("500", rsData.getResultCode());
//        assertEquals("프로젝트 삭제 중 데이터베이스 오류가 발생했습니다.", rsData.getMessage());
//
//    }
}
