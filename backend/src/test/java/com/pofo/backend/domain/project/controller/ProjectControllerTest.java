package com.pofo.backend.domain.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pofo.backend.domain.project.dto.request.ProjectCreateRequest;
import com.pofo.backend.domain.project.dto.response.ProjectCreateResponse;
import com.pofo.backend.domain.project.service.ProjectService;
import com.pofo.backend.domain.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@WebAppConfiguration
@Transactional
public class ProjectControllerTest {

    @Mock
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ProjectService projectService;

    @Mock
    private User mockUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // mockUser 설정
        when(mockUser.getId()).thenReturn(1L);
    }

    @Test
    @WithMockUser(username = "testUser", roles = {"USER"})
    @DisplayName("프로젝트 등록 테스트")
    void t1() throws Exception{

        //given
        ProjectCreateRequest projectCreateRequest = new ProjectCreateRequest();

        projectCreateRequest.setName("PoFo : 포트폴리오 아카이빙 프로젝트");
        projectCreateRequest.setStartDate(new java.util.Date(Date.valueOf("2025-01-22").getTime()));
        projectCreateRequest.setEndDate(new java.util.Date(Date.valueOf("2025-02-13").getTime()));
        projectCreateRequest.setMemberCount(5);
        projectCreateRequest.setPosition(" 백엔드");
        projectCreateRequest.setRepositoryLink("testRepositoryLink");
        projectCreateRequest.setDescription("개발자 직무를 희망하는 사람들의 포트폴리오 및 이력서를 아카이빙할 수 있습니다.");
        projectCreateRequest.setImageUrl("sample.img");

//        List<Skill> skills = List.of(
//                new Skill(1L, "Java"),
//                new Skill(2L, "Spring Boot"),
//                new Skill(3L, "MySQL")
//        );
//
//        projectCreateRequest.setSkills(skills);


        given(projectService.createProject(any(ProjectCreateRequest.class), any(User.class)))
                .willReturn(new ProjectCreateResponse(1L, "201", "프로젝트 등록이 완료되었습니다."));


        String body = mapper.writeValueAsString(projectCreateRequest);
        System.out.println("Request Body: " + body);

        // when & then
        mvc.perform(post("/api/v1/user/project")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.resultCode").value("201"))
                .andExpect(jsonPath("$.message").value("프로젝트 등록이 완료되었습니다."));

    }


}
