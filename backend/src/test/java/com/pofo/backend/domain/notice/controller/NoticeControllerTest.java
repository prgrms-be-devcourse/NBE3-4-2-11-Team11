package com.pofo.backend.domain.notice.controller;

import com.pofo.backend.common.TestSecurityConfig;
import com.pofo.backend.domain.admin.login.entitiy.Admin;
import com.pofo.backend.domain.admin.login.repository.AdminRepository;
import com.pofo.backend.domain.notice.dto.reponse.NoticeDetailResponse;
import com.pofo.backend.domain.notice.dto.request.NoticeCreateRequest;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
public class NoticeControllerTest {

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private AdminRepository adminsRepository;

    @Autowired
    private MockMvc mockMvc;

    private Long noticeId;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;


    @BeforeEach
    @Transactional
    void initData() throws Exception {

        Admin admin = Admin.builder()
                .username("adminUsername")
                .password(passwordEncoder.encode("adminPassword"))
                .status(Admin.Status.ACTIVE)
                .failureCount(0)
                .build();
        this.adminsRepository.save(admin);

        NoticeCreateRequest noticeCreateRequest = new NoticeCreateRequest("공지사항 테스트", "공지사항 테스트입니다.");
        this.noticeId = this.noticeService.create(noticeCreateRequest, admin).getId();
    }

    @Test
    @DisplayName("공지 상세 조회 테스트")
    void t1() throws Exception {

        ResultActions resultActions = mockMvc.perform(
                        get("/api/v1/common/notices/{id}", noticeId)
                                .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                                )
                )
                .andDo(print());

        NoticeDetailResponse noticeDetailResponse = this.noticeService.findById(this.noticeId);
        resultActions.andExpect(handler().handlerType(NoticeController.class))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("공지사항 상세 조회가 완료되었습니다."))
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.id").isNumber())
                .andExpect(jsonPath("$.data.subject").value("공지사항 테스트"))
                .andExpect(jsonPath("$.data.content").value("공지사항 테스트입니다."));
    }

    @Test
    @DisplayName("공지 전체 조회 테스트")
    void t2() throws Exception {

        ResultActions resultActions = mockMvc.perform(
                        get("/api/v1/common/notices")
                                .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                                )
                )
                .andDo(print());

        resultActions.andExpect(handler().handlerType(NoticeController.class))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("공지사항 조회가 완료되었습니다."))
                .andExpect(jsonPath("$.data").isArray());
    }
}
