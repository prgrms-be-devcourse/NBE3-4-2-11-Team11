package com.pofo.backend.domain.notice.controller;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;

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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pofo.backend.common.TestSecurityConfig;
import com.pofo.backend.domain.notice.dto.NoticeRequestDto;
import com.pofo.backend.domain.notice.dto.NoticeResponseDto;
import com.pofo.backend.domain.notice.entity.Notice;
import com.pofo.backend.domain.notice.exception.NoticeNotFoundException;
import com.pofo.backend.domain.notice.repository.NoticeRepository;
import com.pofo.backend.domain.notice.service.NoticeService;

import jakarta.persistence.EntityNotFoundException;

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
	private MockMvc mockMvc;

	private Long noticeId;

	@BeforeEach
	@Transactional
	void initData() throws Exception {
		NoticeRequestDto noticeRequestDto = new NoticeRequestDto();

		noticeRequestDto.setSubject("공지사항 테스트");
		noticeRequestDto.setContent("공지사항 테스트입니다.");

		this.noticeId = this.noticeService.create(noticeRequestDto).getResponseId();
	}

	@Test
	@DisplayName("공지 생성 테스트")
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
			.andExpect(jsonPath("$.message").value("공지사항 생성이 완료되었습니다."))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.responseId").exists())
			.andExpect(jsonPath("$.data.responseId").isNumber());

		// 응답에서 responseId 추출
		String content = resultActions.andReturn().getResponse().getContentAsString();
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = objectMapper.readTree(content);
		Long responseId = jsonNode.path("data").path("responseId").asLong();

		Notice notice = this.noticeRepository.findById(responseId)
			.orElseThrow(() -> new NoticeNotFoundException("해당 공지사항을 찾을 수 없습니다."));

		assertThat(notice.getSubject()).isEqualTo("테스트 공지 생성");
		assertThat(notice.getContent()).isEqualTo("공지사항 생성 테스트입니다.");
	}

	@Test
	@DisplayName("공지 수정 테스트")
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

		NoticeResponseDto noticeResponseDto = this.noticeService.findById(this.noticeId);

		resultActions.andExpect(handler().handlerType(NoticeAdminController.class))
			.andExpect(jsonPath("$.message").value("공지사항 수정이 완료되었습니다."))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.responseId").exists())
			.andExpect(jsonPath("$.data.responseId").isNumber());

		Notice notice = this.noticeRepository.findById(noticeResponseDto.getResponseId())
			.orElseThrow(() -> new NoticeNotFoundException("해당 공지사항을 찾을 수 없습니다."));

		assertThat(notice.getSubject()).isEqualTo("테스트 공지 수정");
		assertThat(notice.getContent()).isEqualTo("공지사항 수정 테스트입니다.");
	}

	@Test
	@DisplayName("공지 삭제 테스트")
	void t3() throws Exception {

		ResultActions resultActions = mockMvc.perform(
				delete("/api/v1/admin/notices/{id}", noticeId)
					.contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
					)
			)
			.andDo(print());

		resultActions.andExpect(handler().handlerType(NoticeAdminController.class))
			.andExpect(jsonPath("$.message").value("공지사항 삭제가 완료되었습니다."))
			.andExpect(status().isOk());

		assertThrows(NoticeNotFoundException.class, () -> this.noticeService.findById(noticeId));
	}

}
