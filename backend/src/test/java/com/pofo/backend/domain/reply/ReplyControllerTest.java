package com.pofo.backend.domain.reply;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pofo.backend.common.TestSecurityConfig;
import com.pofo.backend.domain.inquiry.dto.request.InquiryCreateRequest;
import com.pofo.backend.domain.inquiry.service.InquiryService;
import com.pofo.backend.domain.notice.exception.NoticeException;
import com.pofo.backend.domain.reply.controller.ReplyController;
import com.pofo.backend.domain.reply.dto.request.ReplyCreateRequest;
import com.pofo.backend.domain.reply.dto.response.ReplyDetailResponse;
import com.pofo.backend.domain.reply.entity.Reply;
import com.pofo.backend.domain.reply.repository.ReplyRepository;
import com.pofo.backend.domain.reply.service.ReplyService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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

    private Long replyId;

    @BeforeEach
    void initData() throws Exception {
        InquiryCreateRequest inquiryCreateRequest = new InquiryCreateRequest("문의사항 테스트", "문의사항 테스트입니다.");
        inquiryId = this.inquiryService.create(inquiryCreateRequest).getId();

        ReplyCreateRequest replyCreateRequest = new ReplyCreateRequest("답변 테스트");
        replyId = this.replyService.create(inquiryId, replyCreateRequest).getId();
    }

    @Test
    @DisplayName("답변 생성 테스트")
    void t1() throws Exception {

        InquiryCreateRequest inquiryCreateRequest = new InquiryCreateRequest("문의사항 테스트", "문의사항 테스트입니다.");
        inquiryId = this.inquiryService.create(inquiryCreateRequest).getId();

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
                .andExpect(jsonPath("$.message").value("답변 생성이 완료되었습니다."))
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.id").isNumber());

        // 응답에서 responseId 추출
        String content = resultActions.andReturn().getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(content);
        Long replyId = jsonNode.path("data").path("id").asLong();

        Reply reply = this.replyRepository.findById(replyId)
                .orElseThrow(() -> new NoticeException("해당 답변을 찾을 수 없습니다."));

        assertThat(reply.getContent()).isEqualTo("테스트 답변 생성");
    }

    @Test
    @DisplayName("답변 수정 테스트")
    @Rollback(false)
    void t2() throws Exception {

        ResultActions resultActions = mockMvc.perform(
                        patch("/api/v1/admin/inquiries/{inquiryId}/reply/{replyId}", inquiryId, replyId)
                                .content("""
                                        {
                                            "content":"답변 수정 테스트입니다."
                                        }
                                        """)
                                .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                                )
                )
                .andDo(print());

        ReplyDetailResponse replyDetailResponse = this.replyService.findById(replyId);

        resultActions.andExpect(handler().handlerType(ReplyController.class))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("답변 수정이 완료되었습니다."))
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.id").isNumber());

        Reply reply = this.replyRepository.findById(replyDetailResponse.getId())
                .orElseThrow(() -> new NoticeException("해당 답변을 찾을 수 없습니다."));

        Assertions.assertThat(reply.getContent()).isEqualTo("답변 수정 테스트입니다.");
    }
}
