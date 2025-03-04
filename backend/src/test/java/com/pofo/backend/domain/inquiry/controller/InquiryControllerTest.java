package com.pofo.backend.domain.inquiry.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pofo.backend.common.TestSecurityConfig;
import com.pofo.backend.domain.inquiry.dto.response.InquiryDetailResponse;
import com.pofo.backend.domain.inquiry.dto.request.InquiryCreateRequest;
import com.pofo.backend.domain.inquiry.entity.Inquiry;
import com.pofo.backend.domain.inquiry.exception.InquiryException;
import com.pofo.backend.domain.inquiry.repository.InquiryRepository;
import com.pofo.backend.domain.inquiry.service.InquiryService;
import com.pofo.backend.domain.notice.exception.NoticeException;
import com.pofo.backend.domain.user.join.entity.User;
import com.pofo.backend.domain.user.join.repository.UserRepository;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

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
    private UserRepository usersRepository;

    @Autowired
    private MockMvc mockMvc;

    private Long inquiryId;

    @BeforeEach
    @Transactional
    void initData() throws Exception {

        User user = User.builder()
                .email("dev@dev.com")
                .name("user")
                .sex(User.Sex.MALE)
                .nickname("닉네임")
                .age(LocalDate.of(2000, 1, 1))
                .build();
        this.usersRepository.save(user);

        InquiryCreateRequest inquiryCreateRequest = new InquiryCreateRequest("문의사항 테스트", "문의사항 테스트입니다.");
        this.inquiryId = this.inquiryService.create(inquiryCreateRequest, user).getId();
    }

    @Test
    @DisplayName("문의 생성 테스트")
    @WithMockUser(username = "user", roles = {"USER"})
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

    @Test
    @DisplayName("문의 수정 테스트")
    @WithMockUser(username = "user", roles = {"USER"})
    void t2() throws Exception {

        ResultActions resultActions = mockMvc.perform(
                        patch("/api/v1/user/inquiries/{id}", inquiryId)
                                .content("""
                                        {
                                            "subject":"테스트 문의 수정",
                                            "content":"문의사항 수정 테스트입니다."
                                        }
                                        """)
                                .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                                )
                )
                .andDo(print());

        InquiryDetailResponse inquiryDetailResponse = this.inquiryService.findById(this.inquiryId);

        resultActions.andExpect(handler().handlerType(InquiryController.class))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("문의사항 수정이 완료되었습니다."))
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.id").isNumber());

        Inquiry inquiry = this.inquiryRepository.findById(inquiryDetailResponse.getId())
                .orElseThrow(() -> new InquiryException("해당 문의사항을 찾을 수 없습니다."));

        Assertions.assertThat(inquiry.getSubject()).isEqualTo("테스트 문의 수정");
        Assertions.assertThat(inquiry.getContent()).isEqualTo("문의사항 수정 테스트입니다.");
    }

    @Test
    @DisplayName("문의 삭제 테스트")
    @WithMockUser(username = "user", roles = {"USER"})
    void t3() throws Exception {

        ResultActions resultActions = mockMvc.perform(
                        delete("/api/v1/common/inquiries/{id}", inquiryId)
                                .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                                )
                )
                .andDo(print());

        resultActions.andExpect(handler().handlerType(InquiryController.class))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("문의사항 삭제가 완료되었습니다."));

        assertThrows(InquiryException.class, () -> this.inquiryService.findById(inquiryId));
    }

    @Test
    @DisplayName("문의 상세 조회 테스트")
    void t4() throws Exception {

        ResultActions resultActions = mockMvc.perform(
                        get("/api/v1/common/inquiries/{id}", 168L) // 테스트 용이를 위해 답변이 존재하는 문의글 get
                                .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                                )
                )
                .andDo(print());

        InquiryDetailResponse inquiryDetailResponse = this.inquiryService.findById(this.inquiryId);
        resultActions.andExpect(handler().handlerType(InquiryController.class))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("문의사항 상세 조회가 완료되었습니다."))
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.id").isNumber())
                .andExpect(jsonPath("$.data.subject").value("문의사항 테스트"))
                .andExpect(jsonPath("$.data.content").value("문의사항 테스트입니다."))
                .andExpect(jsonPath("$.data.reply").exists()); // 답변 존재하는지 확인
    }

    @Test
    @DisplayName("문의 전체 조회 테스트")
    void t5() throws Exception {

        ResultActions resultActions = mockMvc.perform(
                        get("/api/v1/common/inquiries")
                                .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                                )
                )
                .andDo(print());

        resultActions.andExpect(handler().handlerType(InquiryController.class))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("문의사항 조회가 완료되었습니다."))
                .andExpect(jsonPath("$.data").isArray());
    }
}
