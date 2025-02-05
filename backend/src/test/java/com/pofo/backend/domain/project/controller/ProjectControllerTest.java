package com.pofo.backend.domain.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pofo.backend.domain.project.dto.request.ProjectCreateRequest;
import com.pofo.backend.domain.project.dto.response.ProjectCreateResponse;
import com.pofo.backend.domain.project.dto.response.ProjectDetailResponse;
import com.pofo.backend.domain.project.service.ProjectService;
import com.pofo.backend.domain.user.entity.User;
import com.pofo.backend.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProjectControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private ProjectService projectService;

    @MockitoBean
    private UserRepository userRepository;

    private User testUser;  // 전역 변수로 선언

    LocalDate startDate = LocalDate.of(2025, 1, 22);
    LocalDate endDate = LocalDate.of(2025, 2, 14);


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // userRepository도 mock객체로 생성
        userRepository.deleteAll();  // 기존 데이터 삭제

        // 사용자 객체 생성
        testUser = new User();
        testUser.setName("testUser");
        testUser.setEmail("test@example.com");
        given(userRepository.findById(null)).willReturn(Optional.of(testUser));
        // 사용자 DB에 저장
//        testUser = userRepository.save(testUser);
        System.out.println("테스트용 유저 ID: " + testUser.getId());  // 저장된 ID 출력
    }

    @Test
    @WithMockUser(username = "testUser", roles = {"USER"})
    @DisplayName("프로젝트 등록 테스트")
    void t1() throws Exception {
        // given
        ProjectCreateRequest projectCreateRequest = new ProjectCreateRequest();
        projectCreateRequest.setName("PoFo : 포트폴리오 아카이빙 프로젝트");
        projectCreateRequest.setStartDate(startDate);
        projectCreateRequest.setEndDate(endDate);
        projectCreateRequest.setMemberCount(5);
        projectCreateRequest.setPosition("백엔드");
        projectCreateRequest.setRepositoryLink("testRepositoryLink");
        projectCreateRequest.setDescription("개발자 직무를 희망하는 사람들의 포트폴리오 및 이력서를 아카이빙할 수 있습니다.");
        projectCreateRequest.setImageUrl("sample.img");

         given(projectService.createProject(any(ProjectCreateRequest.class),eq(testUser)))
                .willReturn(new ProjectCreateResponse(1L));

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        String body = objectMapper.writeValueAsString(projectCreateRequest);

        // when & then
        mvc.perform(post("/api/v1/user/project")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.resultCode").value("201"))
                .andExpect(jsonPath("$.msg").value("프로젝트 등록이 완료되었습니다."));
    }

    @Test
    @WithMockUser(username = "testUser", roles = {"USER"})
    @DisplayName("프로젝트 전체 조회 테스트")
    void t2() throws Exception{
        //given
        ProjectDetailResponse project1 = new ProjectDetailResponse(1L,"프로젝트1" ,startDate, endDate, 5, "백엔드", "testLinkA", "프로젝트1 입니다.", "project1.img" );
        ProjectDetailResponse project2 = new ProjectDetailResponse(2L,"프로젝트2" ,startDate, endDate, 4, "백엔드", "testLinkB", "프로젝트2 입니다.", "project2.img" );
        ProjectDetailResponse project3 = new ProjectDetailResponse(3L,"프로젝트3" ,startDate, endDate, 6, "백엔드", "testLinkC", "프로젝트3 입니다.", "project3.img" );

        List<ProjectDetailResponse> projects = Arrays.asList(project1,project2,project3);

        given(projectService.detailAllProject(any(User.class))).willReturn(projects);

        //when&Then
        mvc.perform(get("/api/v1/user/projects")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200"))
                .andExpect(jsonPath("$.msg").value("프로젝트 전체 조회가 완료되었습니다."))
                .andExpect(jsonPath("$.data[0].projectId").value(1))
                .andExpect(jsonPath("$.data[0].name").value("프로젝트1"))
                .andExpect(jsonPath("$.data[0].startDate").value(startDate.toString()))
                .andExpect(jsonPath("$.data[0].endDate").value(endDate.toString()))
                .andExpect(jsonPath("$.data[0].memberCount").value(5))
                .andExpect(jsonPath("$.data[0].position").value("백엔드"))
                .andExpect(jsonPath("$.data[0].repositoryLink").value("testLinkA"))
                .andExpect(jsonPath("$.data[0].description").value("프로젝트1 입니다."))
                .andExpect(jsonPath("$.data[0].imageUrl").value("project1.img"))

                .andExpect(jsonPath("$.data[1].projectId").value(2))
                .andExpect(jsonPath("$.data[1].name").value("프로젝트2"))
                .andExpect(jsonPath("$.data[1].startDate").value(startDate.toString()))
                .andExpect(jsonPath("$.data[1].endDate").value(endDate.toString()))
                .andExpect(jsonPath("$.data[1].memberCount").value(4))
                .andExpect(jsonPath("$.data[1].position").value("백엔드"))
                .andExpect(jsonPath("$.data[1].repositoryLink").value("testLinkB"))
                .andExpect(jsonPath("$.data[1].description").value("프로젝트2 입니다."))
                .andExpect(jsonPath("$.data[1].imageUrl").value("project2.img"))

                .andExpect(jsonPath("$.data[2].projectId").value(3))
                .andExpect(jsonPath("$.data[2].name").value("프로젝트3"))
                .andExpect(jsonPath("$.data[2].startDate").value(startDate.toString()))
                .andExpect(jsonPath("$.data[2].endDate").value(endDate.toString()))
                .andExpect(jsonPath("$.data[2].memberCount").value(6))
                .andExpect(jsonPath("$.data[2].position").value("백엔드"))
                .andExpect(jsonPath("$.data[2].repositoryLink").value("testLinkC"))
                .andExpect(jsonPath("$.data[2].description").value("프로젝트3 입니다."))
                .andExpect(jsonPath("$.data[2].imageUrl").value("project3.img"));

    }
}
