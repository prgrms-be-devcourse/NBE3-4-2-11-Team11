package com.pofo.backend.domain.notice.controller;

import com.pofo.backend.domain.notice.entity.Notice;
import com.pofo.backend.domain.notice.repository.NoticeRepository;
import com.pofo.backend.domain.notice.service.NoticeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class NoticeControllerTest {

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private NoticeController noticeController;

    @Autowired
    private NoticeRepository noticeRepository;

    @Autowired
    private MockMvc mockMvc;

    @Order(1)
    @Test
    @DisplayName("공지 생성 테스트")
    void createNotice() throws Exception {

        Notice notice = Notice.builder()
                .subject("테스트 공지")
                .content("공지사항 테스트입니다.")
                .createdAt(LocalDateTime.now())
                .build();

        mockMvc.perform(post("/api/v1/admin/notion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"subject\":\"테스트 공지\", \"content\":\"공지사항 테스트입니다.\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.subject").value("테스트 공지"));
    }
}
