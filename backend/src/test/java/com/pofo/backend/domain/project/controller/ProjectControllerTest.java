package com.pofo.backend.domain.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pofo.backend.domain.project.dto.request.ProjectCreateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ProjectControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;


    @Test
    @WithMockUser
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

        String body = mapper.writeValueAsString(projectCreateRequest);

        // when & then
        mvc.perform(post("/project")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.resultCode").value("201"))
                .andExpect(jsonPath("$.msg").value("프로젝트 등록이 완료되었습니다."));

    }

}
