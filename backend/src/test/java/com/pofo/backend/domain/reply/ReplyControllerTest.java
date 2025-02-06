package com.pofo.backend.domain.reply;

import com.pofo.backend.common.TestSecurityConfig;
import com.pofo.backend.domain.inquiry.dto.request.InquiryCreateRequest;
import com.pofo.backend.domain.inquiry.service.InquiryService;
import com.pofo.backend.domain.reply.controller.ReplyController;
import com.pofo.backend.domain.reply.repository.ReplyRepository;
import com.pofo.backend.domain.reply.service.ReplyService;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
public class ReplyControllerTest {

    @Autowired
    private ReplyService replyService;

    @Autowired
    private InquiryService inquiryService;

    @Autowired
    private ReplyRepository replyRepository;

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
    @DisplayName("답변 생성 테스트")
    void t1() throws Exception {

        ResultActions resultActions = mockMvc.perform(
                        post("/api/v1/admin/inquiries/{id}/reply", inquiryId)
                                .content("""
                                        {
                                            "content":"테스트 답변 생성"
                                        }
                                        """)
                                .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                                )
                )
                .andDo(print());

        resultActions.andExpect(handler().handlerType(ReplyController.class))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("문의사항 답변 생성이 완료되었습니다."))
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.id").isNumber());
    }
}
