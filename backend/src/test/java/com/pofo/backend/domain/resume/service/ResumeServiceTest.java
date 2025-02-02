package com.pofo.backend.domain.resume.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.pofo.backend.domain.resume.dto.request.ResumeCreateRequest;
import com.pofo.backend.domain.resume.dto.response.ResumeCreateResponse;
import com.pofo.backend.domain.resume.exception.ResumeCreationException;
import com.pofo.backend.domain.user.entity.User;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class ResumeServiceTest {

    @InjectMocks
    private ResumeService resumeService;

    @Mock
    private User mockUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(mockUser.getId()).thenReturn(1L);
    }

    @Test
    @DisplayName("이력서 생성 성공")
    void createResume() {
        ResumeCreateRequest resumeCreateRequest = new ResumeCreateRequest();
        resumeCreateRequest.setName("김상진");
        resumeCreateRequest.setBirth(LocalDate.of(2000, 11, 18));
        resumeCreateRequest.setNumber("010-1234-5678");
        resumeCreateRequest.setEmail("prgrms@naver.com");
        resumeCreateRequest.setAddress("서울시 강남구");
        resumeCreateRequest.setGitAddress("https://github.com/kim");
        resumeCreateRequest.setBlogAddress("https://kim.blog");

        ResumeCreateResponse response = resumeService.createResume(resumeCreateRequest, mockUser);
        assertEquals("이력서 생성이 완료되었습니다.", response.getMessage());
    }

    @Test
    @DisplayName("이력서 생성 실패 - 사용자 정보 없음")
    void createResumeWithNullUser() {
        when(mockUser.getId()).thenReturn(null);

        ResumeCreateRequest resumeCreateRequest = new ResumeCreateRequest();
        resumeCreateRequest.setName("김상진");
        resumeCreateRequest.setBirth(LocalDate.of(2000, 11, 18));
        resumeCreateRequest.setNumber("010-1234-5678");
        resumeCreateRequest.setEmail("prgrms@naver.com");
        resumeCreateRequest.setAddress("서울시 강남구");
        resumeCreateRequest.setGitAddress("https://github.com/kim");
        resumeCreateRequest.setBlogAddress("https://kim.blog");

        try {
            resumeService.createResume(resumeCreateRequest, mockUser);
        } catch (ResumeCreationException e) {
            assertEquals("사용자 정보가 존재하지 않습니다.", e.getMessage());
        }
    }
}
