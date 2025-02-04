package com.pofo.backend.domain.inquiry.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pofo.backend.common.TestSecurityConfig;
import com.pofo.backend.domain.inquiry.dto.request.InquiryCreateRequest;
import com.pofo.backend.domain.inquiry.entity.Inquiry;
import com.pofo.backend.domain.inquiry.repository.InquiryRepository;
import com.pofo.backend.domain.inquiry.service.InquiryService;
import com.pofo.backend.domain.notice.exception.NoticeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
public class InquiryControllerTest {

    @Autowired
    private InquiryService inquiryService;

    @Autowired
    private InquiryRepository inquiryRepository;
    @Autowired
    private MockMvc mockMvc;

    private Long inquiryId;

    @BeforeEach
    @Transactional
    void initData() throws Exception {
        InquiryCreateRequest inquiryCreateRequest = new InquiryCreateRequest("문의사항 테스트", "문의사항 테스트입니다.");
        this.inquiryId = this.inquiryService.create(inquiryCreateRequest).getId();
    }

    @Test
    @DisplayName("문의 생성 테스트")
    void t1() throws Exception {

        ResultActions resultActions = mockMvc.perform(
                        post("/api/v1/user/inquiry")
                                .content("""
                                        {
                                            "subject":"테스트 문의 생성",
                                            "content":"문의사항 생성 테스트입니다."
                                        }
                                        """)
                                .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                                )
                )
                .andDo(print());

        resultActions.andExpect(handler().handlerType(InquiryController.class))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("문의사항 생성이 완료되었습니다."))
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.id").isNumber());

        // 응답에서 responseId 추출
        String content = resultActions.andReturn().getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(content);
        Long responseId = jsonNode.path("data").path("id").asLong();

        Inquiry inquiry = this.inquiryRepository.findById(responseId)
                .orElseThrow(() -> new NoticeException("해당 문의사항을 찾을 수 없습니다."));

        assertThat(inquiry.getSubject()).isEqualTo("테스트 문의 생성");
        assertThat(inquiry.getContent()).isEqualTo("문의사항 생성 테스트입니다.");
    }
}
