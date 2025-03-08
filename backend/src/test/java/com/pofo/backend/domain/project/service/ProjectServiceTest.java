package com.pofo.backend.domain.project.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import com.pofo.backend.domain.skill.entity.ProjectSkill;
import com.pofo.backend.domain.skill.entity.Skill;
import com.pofo.backend.domain.skill.repository.ProjectSkillRepository;
import com.pofo.backend.domain.skill.service.SkillService;
import com.pofo.backend.domain.tool.entity.ProjectTool;
import com.pofo.backend.domain.tool.entity.Tool;
import com.pofo.backend.domain.tool.repository.ProjectToolRepository;
import com.pofo.backend.domain.tool.service.ToolService;
import com.pofo.backend.domain.user.join.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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

    @Mock
    private SkillService skillService;

    @Mock
    private ToolService toolService;

    @Mock
     private ProjectSkillRepository projectSkillRepository;

    @Mock
    private ProjectToolRepository projectToolRepository;

    @Mock
    private FileService fileService;

    LocalDate startDate = LocalDate.of(2025, 1, 22);
    LocalDate endDate = LocalDate.of(2025, 2, 14);

    private ObjectMapper objectMapper;

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
        when(mockProject.isDeleted()).thenReturn(false);

        // Mock Skill & Tool 객체 생성
        when(skillService.getSkillByName("Java")).thenReturn(new Skill("Java"));
        when(skillService.getSkillByName("Spring Boot")).thenReturn(new Skill("Spring Boot"));
        when(toolService.getToolByName("IntelliJ IDEA")).thenReturn(new Tool("IntelliJ IDEA"));
        when(toolService.getToolByName("Docker")).thenReturn(new Tool("Docker"));

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
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
                .skills(List.of("Java", "Spring Boot")) // 추가
                .tools(List.of("IntelliJ IDEA", "Docker")) // 추가
                .build();
    }

    @Test
    @DisplayName("✅ 프로젝트 등록 성공")
    void t1() throws JsonProcessingException {
        // Given
        ProjectCreateRequest request = projectCreateRequest();
        String projectRequestJson = objectMapper.writeValueAsString(request);

        // ✅ Mock MultipartFile 생성 (썸네일 파일 업로드 테스트를 위해 필요)
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getOriginalFilename()).thenReturn("mock_thumbnail.jpg");

        // ✅ 파일 업로드 Mock 설정 (실제 업로드 대신 경로 반환)
        String mockThumbnailPath = "mocked/path/to/thumbnail.jpg";
        when(fileService.uploadThumbnail(any(MultipartFile.class))).thenReturn(mockThumbnailPath);

        // ✅ 실제 저장될 프로젝트 객체 (spy로 감싸기)
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
                .thumbnailPath(mockThumbnailPath) // ✅ Mock 썸네일 경로 추가
                .isDeleted(false)
                .build());

        // ✅ 프로젝트 저장 Mocking
        when(projectRepository.save(any(Project.class))).thenReturn(realProject);

        // ✅ Service 메서드 호출
        ProjectCreateResponse response = projectService.createProject(mockUser, projectRequestJson, mockFile);

        // Then
        assertNotNull(response); // ✅ 응답이 null이 아닌지 확인
        assertEquals(realProject.getId(), response.getProjectId()); // ✅ 반환된 ID 검증
        verify(projectRepository).save(any(Project.class)); // ✅ 프로젝트 저장이 호출되었는지 검증
    }


    @Test
    @DisplayName("❌ 프로젝트 등록 실패 - 예외 발생")
    void t3() throws JsonProcessingException {
        // Given
        ProjectCreateRequest request = projectCreateRequest();
        String projectRequestJson = objectMapper.writeValueAsString(request);

        // ✅ Mock MultipartFile 생성 (실패 시에도 필요)
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getOriginalFilename()).thenReturn("mock_thumbnail.jpg");

        // ✅ 프로젝트 저장 시 강제 예외 발생하도록 설정
        doThrow(new ProjectCreationException("400", "프로젝트 등록 중 오류가 발생했습니다."))
                .when(projectRepository).save(any(Project.class));

        // When & Then
        ProjectCreationException exception = assertThrows(ProjectCreationException.class, () -> {
            projectService.createProject(mockUser, projectRequestJson, mockFile);
        });

        RsData<Void> rsData = exception.getRsData();
        assertEquals("400", rsData.getResultCode());
        assertEquals("프로젝트 등록 중 오류가 발생했습니다.", rsData.getMessage());
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

        when(mockProjectResponse.getSkills()).thenReturn(List.of("Java", "Spring Boot"));
        when(mockProjectResponse.getTools()).thenReturn(List.of("IntelliJ IDEA", "Docker"));

        when(projectMapper.projectToProjectDetailResponse(mockProject)).thenReturn(mockProjectResponse);

        List<Project> mockProjectList = List.of(mockProject);
        when(projectRepository.findByIsDeletedFalseOrderByIdDesc()).thenReturn(mockProjectList);

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

        assertEquals(2, response.get(0).getSkills().size());
        assertEquals(2, response.get(0).getTools().size());
        assertTrue(response.get(0).getSkills().containsAll(List.of("Java", "Spring Boot")));
        assertTrue(response.get(0).getTools().containsAll(List.of("IntelliJ IDEA", "Docker")));

        // Verify
        verify(projectRepository).findByIsDeletedFalseOrderByIdDesc();
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
        assertEquals("프로젝트가 존재하지 않습니다.", rsData.getMessage());
    }

    @Test
    @DisplayName("프로젝트 전체 조회 실패 - 예기치 않은 오류 발생")
    void t6(){
        //given
        when(projectRepository.findByIsDeletedFalseOrderByIdDesc()).thenThrow(new RuntimeException("Unexpected error"));

        //when & then
        ProjectCreationException exception = assertThrows(ProjectCreationException.class, () -> {
            projectService.detailAllProject(mockUser);
        });

        // 예외 메시지 확인
        RsData<Void> rsData = exception.getRsData();
        assertEquals("400", rsData.getResultCode());
        assertEquals("프로젝트 전체 조회 중 오류가 발생했습니다.", rsData.getMessage());
    }

    @Test
    @DisplayName("프로젝트 검색 성공 - 이름 또는 설명에 키워드 포함")
    void t16(){
        //Given
        String keyword = "커피";

        //when & Then
        when(mockProjectResponse.getName()).thenReturn("커피 원두 주문 시스템");
        when(mockProjectResponse.getDescription()).thenReturn("원두를 주문할 수 있는 편리한 웹 서비스");
        when(projectRepository.searchByKeyword(keyword))
                .thenReturn(List.of(mockProject));
        when(projectMapper.projectToProjectDetailResponse(mockProject)).thenReturn(mockProjectResponse);
        when(mockProject.getUser()).thenReturn(mockUser);

        // When
        List<ProjectDetailResponse> response = projectService.searchProjectsByKeyword(mockUser, keyword);

        // Then
        assertEquals(1, response.size());
        assertEquals("커피 원두 주문 시스템", response.get(0).getName());
        assertEquals("원두를 주문할 수 있는 편리한 웹 서비스", response.get(0).getDescription());

        // Verify
        verify(projectRepository).searchByKeyword(keyword);
        verify(projectMapper).projectToProjectDetailResponse(mockProject);
    }

    @Test
    @DisplayName("프로젝트 검색 실패 - 검색 결과 없음")
    void t17() {
        // Given
        String keyword = "없는키워드";
        when(projectRepository.searchByKeyword(keyword))
                .thenReturn(Collections.emptyList());

        // When & Then
        ProjectCreationException exception = assertThrows(ProjectCreationException.class, () -> {
            projectService.searchProjectsByKeyword(mockUser, keyword);
        });

        RsData<Void> rsData = exception.getRsData();
        assertEquals("404", rsData.getResultCode());
        assertEquals("검색된 프로젝트가 없습니다.", rsData.getMessage());
    }

    @Test
    @DisplayName("프로젝트 검색 실패 - 데이터베이스 오류 발생")
    void t18() {
        // Given
        String keyword = "커피";
        when(projectRepository.searchByKeyword(keyword))
                .thenThrow(new DataAccessException("Database error") {});

        // When & Then
        ProjectCreationException exception = assertThrows(ProjectCreationException.class, () -> {
            projectService.searchProjectsByKeyword(mockUser, keyword);
        });

        RsData<Void> rsData = exception.getRsData();
        assertEquals("500", rsData.getResultCode());
        assertEquals("프로젝트 검색 중 데이터베이스 오류가 발생했습니다.", rsData.getMessage());
    }

    @Test
    @DisplayName("프로젝트 검색 실패 - 예기치 않은 오류 발생")
    void t19() {
        // Given
        String keyword = "커피";
        when(projectRepository.searchByKeyword(keyword))
                .thenThrow(new RuntimeException("Unexpected error"));

        // When & Then
        ProjectCreationException exception = assertThrows(ProjectCreationException.class, () -> {
            projectService.searchProjectsByKeyword(mockUser, keyword);
        });

        RsData<Void> rsData = exception.getRsData();
        assertEquals("400", rsData.getResultCode());
        assertEquals("프로젝트 검색 중 오류가 발생했습니다.", rsData.getMessage());
    }



    @Test
    @DisplayName("프로젝트 단건 조회 성공")
    void t7() {
        Long projectId = 1L;
        User ownUser = mock(User.class);
        when(ownUser.getId()).thenReturn(3L);

        Project project = new Project();
        project.setUser(ownUser);
        project.setName("국내 여행 추천 웹페이지");
        project.setStartDate(startDate);
        project.setEndDate(endDate);
        project.setMemberCount(4);
        project.setPosition("백엔드");
        project.setRepositoryLink("koreaTravel@github.com");
        project.setDescription("국내 여행지 추천해주는 웹페이지입니다.");
        project.setImageUrl("travel.img");

        project.setProjectSkills(
                List.of(new ProjectSkill(project, new Skill("Java")),
                        new ProjectSkill(project, new Skill("Spring Boot")))
        );
        project.setProjectTools(
                List.of(new ProjectTool(project, new Tool("IntelliJ IDEA")),
                        new ProjectTool(project, new Tool("Docker")))
        );

        ProjectDetailResponse mockResponse = mock(ProjectDetailResponse.class);

        //  mockResponse의 필드 값 설정
        when(mockResponse.getName()).thenReturn("국내 여행 추천 웹페이지");
        when(mockResponse.getStartDate()).thenReturn(startDate);
        when(mockResponse.getEndDate()).thenReturn(endDate);
        when(mockResponse.getMemberCount()).thenReturn(4);
        when(mockResponse.getPosition()).thenReturn("백엔드");
        when(mockResponse.getRepositoryLink()).thenReturn("koreaTravel@github.com");
        when(mockResponse.getDescription()).thenReturn("국내 여행지 추천해주는 웹페이지입니다.");
        when(mockResponse.getImageUrl()).thenReturn("travel.img");

        when(mockResponse.getSkills()).thenReturn(List.of("Java", "Spring Boot"));
        when(mockResponse.getTools()).thenReturn(List.of("IntelliJ IDEA", "Docker"));

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(projectMapper.projectToProjectDetailResponse(project)).thenReturn(mockResponse);

        // When
        ProjectDetailResponse response = projectService.detailProject(projectId, ownUser);

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

        // Skill 및 Tool 검증
        assertEquals(2, response.getSkills().size());
        assertEquals(2, response.getTools().size());
        assertTrue(response.getSkills().containsAll(List.of("Java", "Spring Boot")));
        assertTrue(response.getTools().containsAll(List.of("IntelliJ IDEA", "Docker")));

        // Verify
        verify(projectRepository).findById(projectId);
        verify(projectMapper).projectToProjectDetailResponse(project);
    }



    @Test
    @DisplayName("프로젝트 단건 조회 실패 - 프로젝트 없음")
    void t8(){
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
        assertEquals("해당 프로젝트를 찾을 수 없습니다.", rsData.getMessage());
    }




    @Test
    @DisplayName("프로젝트 단건 조회 실패 - 예기치 못한 오류")
    void t9(){
        Long projectId = 1L;
        User mockUser = mock(User.class);
        when(projectRepository.findById(projectId)).thenThrow(new RuntimeException("Unexpected Error"));

        //when & Then
        ProjectCreationException exception = assertThrows(ProjectCreationException.class, () -> {
            projectService.detailProject(projectId, mockUser);
        });

        RsData<Void> rsData = exception.getRsData();
        assertEquals("400", rsData.getResultCode());
        assertEquals("프로젝트 단건 조회 중 오류가 발생했습니다.", rsData.getMessage());


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
                .skills(List.of("React", "TypeScript"))
                .tools(List.of("vs Code", "Figma"))
                .build();
    }

    @Test
    @Transactional
    @DisplayName("프로젝트 수정 성공")
    void t10() throws JsonProcessingException{
        Long projectId = 1L;
        ProjectUpdateRequest updateRequest = projectUpdateRequest();
        String projectRequestJson = objectMapper.writeValueAsString(updateRequest);
        MultipartFile mockFile = mock(MultipartFile.class);
        Boolean deleteThumbnail = true;

        Project realProject = Project.builder()
                .user(mockUser)
                .name("기존 프로젝트")
                .startDate(LocalDate.of(2025, 1, 1))
                .endDate(LocalDate.of(2025, 2, 10))
                .memberCount(4)
                .position("백엔드")
                .repositoryLink("oldRepoLink")
                .description("기존 프로젝트 설명")
                .imageUrl("oldImage.img")
                .build();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(realProject));
        when(projectRepository.save(any(Project.class))).thenReturn(realProject);
        when(skillService.getSkillByName(anyString())).thenReturn(new Skill("React"));
        when(toolService.getToolByName(anyString())).thenReturn(new Tool("VS Code"));

        // When
        ProjectUpdateResponse response = projectService.updateProject(projectId, projectRequestJson, mockUser, mockFile, deleteThumbnail);

        // Then
        assertNotNull(response);
        assertEquals(updateRequest.getName(), response.getName());
        verify(projectRepository, atLeastOnce()).save(any(Project.class));
    }


    @Test
    @DisplayName("프로젝트 수정 실패 - 프로젝트 없음")
    void t11() throws JsonProcessingException{
        ProjectUpdateRequest updateRequest = projectUpdateRequest();
        String projectRequestJson = objectMapper.writeValueAsString(updateRequest);
        MultipartFile mockFile = mock(MultipartFile.class);
        Boolean deleteThumbnail = false;

        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        ProjectCreationException exception = assertThrows(ProjectCreationException.class, () -> {
            projectService.updateProject(1L, projectRequestJson, mockUser, mockFile, deleteThumbnail);
        });

        RsData<Void> rsData = exception.getRsData();
        assertEquals("404", rsData.getResultCode());
        assertEquals("해당 프로젝트를 찾을 수 없습니다.", rsData.getMessage());
    }

    @Test
    @DisplayName("프로젝트 수정 실패 - 권한 없음")
    void t12() throws JsonProcessingException{
        ProjectUpdateRequest updateRequest = projectUpdateRequest();
        String projectRequestJson = objectMapper.writeValueAsString(updateRequest);
        MultipartFile mockFile = mock(MultipartFile.class);
        Boolean deleteThumbnail = false;

        User differentUser = mock(User.class);
        Project mockProject = mock(Project.class);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(mockProject));
        when(mockProject.getUser()).thenReturn(differentUser);

        ProjectCreationException exception = assertThrows(ProjectCreationException.class, () -> {
            projectService.updateProject(1L, projectRequestJson, mockUser, mockFile, deleteThumbnail);
        });

        RsData<Void> rsData = exception.getRsData();
        assertEquals("403", rsData.getResultCode());
        assertEquals("프로젝트 수정할 권한이 없습니다.", rsData.getMessage());
    }


    @Test
    @DisplayName("프로젝트 삭제 성공 - 휴지통으로 이동")
    void t13() {
        Long projectId=1L;

        Project mockProject = Mockito.mock(Project.class);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(mockProject));
        when(mockProject.getUser()).thenReturn(mockUser); // 사용자 일치

        // When
        assertDoesNotThrow(() -> projectService.deleteProject(projectId, mockUser));

        // Then
        verify(mockProject).setDeleted(true);
        verify(projectRepository).save(mockProject);

    }


    @Test
    @DisplayName("프로젝트 삭제 실패 - 삭제 권한 없음")
    void t14(){

        Long projectId=1L;

        Project mockProject = Mockito.mock(Project.class);
        User differentUser = Mockito.mock(User.class); // 다른 사용자
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(mockProject));
        when(mockProject.getUser()).thenReturn(differentUser); // 사용자 불일치

        // When & Then
        ProjectCreationException exception = assertThrows(ProjectCreationException.class, () -> {
            projectService.deleteProject(projectId, mockUser);
        });

        RsData<Void> rsData = exception.getRsData();
        assertEquals("403", rsData.getResultCode());
        assertEquals("프로젝트 삭제 할 권한이 없습니다.", rsData.getMessage());
    }

    @Test
    @DisplayName("프로젝트 삭제 실패 - 데이터베이스 오류 발생(휴지통 이동 실패)")
    void t15(){

        Long projectId=1L;

        // Given
        Project mockProject = Mockito.mock(Project.class);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(mockProject));
        when(mockProject.getUser()).thenReturn(mockUser);

        doThrow(new DataAccessException("DB 오류"){}).when(projectRepository).save(mockProject);

        // When & Then
        ProjectCreationException exception = assertThrows(ProjectCreationException.class, () -> {
            projectService.deleteProject(projectId, mockUser);
        });

        RsData<Void> rsData = exception.getRsData();
        assertEquals("500", rsData.getResultCode());
        assertEquals("프로젝트 삭제 중 데이터베이스 오류가 발생했습니다.", rsData.getMessage());

    }

    @Test
    @DisplayName("다중 프로젝트 삭제 성공 - 휴지통으로 이동")
    void t20() {
        List<Long> projectIds = List.of(1L, 2L, 3L);

        Project mockProject1 = Mockito.mock(Project.class);
        Project mockProject2 = Mockito.mock(Project.class);
        Project mockProject3 = Mockito.mock(Project.class);

        when(projectRepository.findAllById(projectIds)).thenReturn(List.of(mockProject1, mockProject2, mockProject3));
        when(mockProject1.getUser()).thenReturn(mockUser);
        when(mockProject2.getUser()).thenReturn(mockUser);
        when(mockProject3.getUser()).thenReturn(mockUser);

        // When
        assertDoesNotThrow(() -> projectService.moveToTrash(projectIds, mockUser));

        // Then
        verify(mockProject1).setDeleted(true);
        verify(mockProject2).setDeleted(true);
        verify(mockProject3).setDeleted(true);
        verify(projectRepository).saveAll(List.of(mockProject1, mockProject2, mockProject3));  // 저장 확인
    }

    @Test
    @DisplayName("휴지통 복원 성공")
    void t21() {
        List<Long> projectIds = List.of(1L, 2L);

        Project mockProject1 = Mockito.mock(Project.class);
        Project mockProject2 = Mockito.mock(Project.class);

        when(mockProject1.getId()).thenReturn(1L);
        when(mockProject2.getId()).thenReturn(2L);

        when(mockProject1.getUser()).thenReturn(mockUser);
        when(mockProject2.getUser()).thenReturn(mockUser);

        when(mockProject1.isDeleted()).thenReturn(true);
        when(mockProject2.isDeleted()).thenReturn(true);

        when(projectRepository.findByIdInAndIsDeletedTrue(projectIds)).thenReturn(List.of(mockProject1, mockProject2));

        // When
        doNothing().when(mockProject1).setDeleted(false);
        doNothing().when(mockProject2).setDeleted(false);

        assertDoesNotThrow(() -> projectService.restoreProjects(projectIds, mockUser));

        // Then
        verify(mockProject1).setDeleted(false);
        verify(mockProject2).setDeleted(false);
        verify(projectRepository).saveAll(List.of(mockProject1, mockProject2));  // 저장 확인
    }

    @Test
    @DisplayName("프로젝트 영구 삭제 성공")
    void t22() {
        List<Long> projectIds = List.of(1L, 2L);

        Project mockProject1 = Mockito.mock(Project.class);
        Project mockProject2 = Mockito.mock(Project.class);

        when(mockProject1.getId()).thenReturn(1L);
        when(mockProject2.getId()).thenReturn(2L);

        when(mockProject1.getUser()).thenReturn(mockUser);
        when(mockProject2.getUser()).thenReturn(mockUser);

        when(mockProject1.isDeleted()).thenReturn(true);
        when(mockProject2.isDeleted()).thenReturn(true);

        when(projectRepository.findByIdInAndIsDeletedTrue(projectIds)).thenReturn(List.of(mockProject1, mockProject2));

        // 스킬 및 툴 삭제 Mock 설정
        doNothing().when(skillService).deleteProjectSkills(projectIds);
        doNothing().when(toolService).deleteProjectTools(projectIds);

        // When
        assertDoesNotThrow(() -> projectService.permanentlyDeleteProjects(projectIds, mockUser));

        // Then
        verify(skillService).deleteProjectSkills(projectIds);
        verify(toolService).deleteProjectTools(projectIds);
        verify(projectRepository).deleteAll(List.of(mockProject1, mockProject2));  // 실제 삭제 확인
    }

    @Test
    @DisplayName("다중 프로젝트 삭제 실패 - 삭제 권한 없음")
    void t23() {
        List<Long> projectIds = List.of(1L, 2L);

        Project mockProject1 = Mockito.mock(Project.class);
        Project mockProject2 = Mockito.mock(Project.class);

        User differentUser = Mockito.mock(User.class);

        when(mockProject1.getId()).thenReturn(1L);
        when(mockProject2.getId()).thenReturn(2L);


        when(projectRepository.findAllById(projectIds)).thenReturn(List.of(mockProject1, mockProject2));
        when(mockProject1.getUser()).thenReturn(differentUser); // 다른 사용자
        when(mockProject2.getUser()).thenReturn(mockUser);      // 일치 사용자

        // When & Then
        ProjectCreationException exception = assertThrows(ProjectCreationException.class, () -> {
            projectService.moveToTrash(projectIds, mockUser);
        });

        RsData<Void> rsData = exception.getRsData();
        assertEquals("403", rsData.getResultCode());
        assertEquals("프로젝트 삭제 할 권한이 없습니다.", rsData.getMessage());
    }

    @Test
    @DisplayName("다중 프로젝트 삭제 실패 - 데이터베이스 오류 발생")
    void t24() {
        List<Long> projectIds = List.of(1L, 2L);

        Project mockProject1 = Mockito.mock(Project.class);
        Project mockProject2 = Mockito.mock(Project.class);

        when(mockProject1.getId()).thenReturn(1L);
        when(mockProject2.getId()).thenReturn(2L);


        when(projectRepository.findAllById(projectIds)).thenReturn(List.of(mockProject1, mockProject2));
        when(mockProject1.getUser()).thenReturn(mockUser);
        when(mockProject2.getUser()).thenReturn(mockUser);

        // DB 삭제 실패 발생
        doThrow(new DataAccessException("DB 오류"){}).when(projectRepository).saveAll(anyList());

        // When & Then
        ProjectCreationException exception = assertThrows(ProjectCreationException.class, () -> {
            projectService.moveToTrash(projectIds, mockUser);
        });

        RsData<Void> rsData = exception.getRsData();
        assertEquals("500", rsData.getResultCode());
        assertEquals("프로젝트 삭제 중 데이터베이스 오류가 발생했습니다.", rsData.getMessage());
    }


}