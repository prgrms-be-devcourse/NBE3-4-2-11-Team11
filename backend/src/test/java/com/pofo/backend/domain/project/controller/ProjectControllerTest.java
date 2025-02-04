package com.pofo.backend.domain.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pofo.backend.domain.project.dto.request.ProjectCreateRequest;
import com.pofo.backend.domain.project.dto.response.ProjectCreateResponse;
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

import java.sql.Date;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
        projectCreateRequest.setStartDate(new java.util.Date(Date.valueOf("2025-01-22").getTime()));
        projectCreateRequest.setEndDate(new java.util.Date(Date.valueOf("2025-02-13").getTime()));
        projectCreateRequest.setMemberCount(5);
        projectCreateRequest.setPosition("백엔드");
        projectCreateRequest.setRepositoryLink("testRepositoryLink");
        projectCreateRequest.setDescription("개발자 직무를 희망하는 사람들의 포트폴리오 및 이력서를 아카이빙할 수 있습니다.");
        projectCreateRequest.setImageUrl("sample.img");

         given(projectService.createProject(any(ProjectCreateRequest.class),eq(testUser)))
                .willReturn(new ProjectCreateResponse(1L));

        String body = new ObjectMapper().writeValueAsString(projectCreateRequest);

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
}
