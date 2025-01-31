package com.pofo.backend.domain.notice.controller;

import com.pofo.backend.common.exception.ServiceException;
import com.pofo.backend.domain.notice.dto.NoticeRequestDto;
import com.pofo.backend.domain.notice.dto.NoticeResponseDto;
import com.pofo.backend.domain.notice.repository.NoticeRepository;
import com.pofo.backend.domain.notice.service.NoticeService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

	private Long noticeId;

	@BeforeEach
	@Transactional
	void initData() throws Exception {
		NoticeRequestDto noticeRequestDto = new NoticeRequestDto();

		noticeRequestDto.setSubject("공지사항 테스트");
		noticeRequestDto.setContent("공지사항 테스트입니다.");

		this.noticeId = this.noticeService.create(noticeRequestDto).getId();
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

		resultActions.andExpect(handler().handlerType(NoticeController.class))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.subject").value("테스트 공지 생성"))
			.andExpect(jsonPath("$.data.content").value("공지사항 생성 테스트입니다."));
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

		resultActions.andExpect(handler().handlerType(NoticeController.class))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value("공지사항 수정이 완료되었습니다."))
			.andExpect(jsonPath("$.data.subject").value(noticeResponseDto.getSubject()))
			.andExpect(jsonPath("$.data.content").value(noticeResponseDto.getContent()));
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

		resultActions.andExpect(handler().handlerType(NoticeController.class))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value("공지사항 삭제가 완료되었습니다."));

		assertThrows(ServiceException.class, () -> this.noticeService.findById(noticeId));
	}

	@Test
	@DisplayName("공지 상세 조회 테스트")
	void t4() throws Exception {

		ResultActions resultActions = mockMvc.perform(
				get("/api/v1/common/notices/{id}", noticeId)
					.contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
					)
			)
			.andDo(print());

		resultActions.andExpect(handler().handlerType(NoticeController.class))
			.andExpect(jsonPath("$.message").value("공지사항 상세 조회가 완료되었습니다."))
			.andExpect(jsonPath("$.data.subject").value("공지사항 테스트"))
			.andExpect(jsonPath("$.data.content").value("공지사항 테스트입니다."));
	}

	@Test
	@DisplayName("공지 전체 조회 테스트")
	void t5() throws Exception {

		ResultActions resultActions = mockMvc.perform(
				get("/api/v1/common/notices")
					.contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
					)
			)
			.andDo(print());

		resultActions.andExpect(handler().handlerType(NoticeController.class))
			.andExpect(jsonPath("$.message").value("공지사항 조회가 완료되었습니다."))
			.andExpect(jsonPath("$.data").isArray());
	}
}
