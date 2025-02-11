package com.pofo.backend.domain.notice.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pofo.backend.common.TestSecurityConfig;
import com.pofo.backend.domain.admin.login.entitiy.Admin;
import com.pofo.backend.domain.admin.login.repository.AdminRepository;
import com.pofo.backend.domain.notice.dto.reponse.NoticeDetailResponse;
import com.pofo.backend.domain.notice.dto.request.NoticeCreateRequest;
import com.pofo.backend.domain.notice.entity.Notice;
import com.pofo.backend.domain.notice.exception.NoticeException;
import com.pofo.backend.domain.notice.repository.NoticeRepository;
import com.pofo.backend.domain.notice.service.NoticeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
public class NoticeAdminControllerTest {

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private NoticeRepository noticeRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private MockMvc mockMvc;

    private Long noticeId;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    @Transactional
    void initData() throws Exception {

        Admin admin = Admin.builder()
                .username("admin")
                .password(passwordEncoder.encode("password"))
                .status(Admin.Status.ACTIVE)
                .failureCount(0)
                .build();
        this.adminRepository.save(admin);

        NoticeCreateRequest noticeCreateRequest = new NoticeCreateRequest("공지사항 테스트", "공지사항 테스트입니다.");
        this.noticeId = this.noticeService.create(noticeCreateRequest, admin).getId();
    }

    @Test
    @DisplayName("공지 생성 테스트")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void t1() throws Exception {

        ResultActions resultActions = mockMvc.perform(
                        post("/api/v1/admin/notice")
                                .content("""
                                        {
                                            "subject":"테스트 공지 생성",
                                            "content":"공지사항 생성 테스트입니다."
                                        }
                                        """)
                                .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                                )
                )
                .andDo(print());

        resultActions.andExpect(handler().handlerType(NoticeAdminController.class))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("공지사항 생성이 완료되었습니다."))
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.id").isNumber());

        // 응답에서 responseId 추출
        String content = resultActions.andReturn().getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(content);
        Long responseId = jsonNode.path("data").path("id").asLong();

        Notice notice = this.noticeRepository.findById(responseId)
                .orElseThrow(() -> new NoticeException("해당 공지사항을 찾을 수 없습니다."));

        assertThat(notice.getSubject()).isEqualTo("테스트 공지 생성");
        assertThat(notice.getContent()).isEqualTo("공지사항 생성 테스트입니다.");
    }

    @Test
    @DisplayName("공지 수정 테스트")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void t2() throws Exception {

        ResultActions resultActions = mockMvc.perform(
                        patch("/api/v1/admin/notices/{id}", noticeId)
                                .content("""
                                        {
                                            "subject":"테스트 공지 수정",
                                            "content":"공지사항 수정 테스트입니다."
                                        }
                                        """)
                                .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                                )
                )
                .andDo(print());

        NoticeDetailResponse noticeDetailResponse = this.noticeService.findById(this.noticeId);

        resultActions.andExpect(handler().handlerType(NoticeAdminController.class))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("공지사항 수정이 완료되었습니다."))
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.id").isNumber());

        Notice notice = this.noticeRepository.findById(noticeDetailResponse.getId())
                .orElseThrow(() -> new NoticeException("해당 공지사항을 찾을 수 없습니다."));

        assertThat(notice.getSubject()).isEqualTo("테스트 공지 수정");
        assertThat(notice.getContent()).isEqualTo("공지사항 수정 테스트입니다.");
    }

    @Test
    @DisplayName("공지 삭제 테스트")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void t3() throws Exception {

        ResultActions resultActions = mockMvc.perform(
                        delete("/api/v1/admin/notices/{id}", noticeId)
                                .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                                )
                )
                .andDo(print());

        resultActions.andExpect(handler().handlerType(NoticeAdminController.class))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("공지사항 삭제가 완료되었습니다."));

        assertThrows(NoticeException.class, () -> this.noticeService.findById(noticeId));
    }

}
