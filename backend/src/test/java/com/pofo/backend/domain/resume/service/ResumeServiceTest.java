package com.pofo.backend.domain.resume.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.pofo.backend.domain.resume.dto.response.ResumeCreateResponse;
import com.pofo.backend.domain.resume.dto.request.ResumeCreateRequest;
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
    @DisplayName("이력서 생성 테스트")
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
}
