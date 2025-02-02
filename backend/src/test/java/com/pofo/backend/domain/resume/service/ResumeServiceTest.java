package com.pofo.backend.domain.resume.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.pofo.backend.domain.resume.dto.ResumeRequest;
import com.pofo.backend.domain.resume.dto.ResumeResponse;
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
        // Given
        ResumeRequest resumeRequest = new ResumeRequest();
        resumeRequest.setName("김상진");
        resumeRequest.setBirth(LocalDate.of(2000, 11, 18));
        resumeRequest.setNumber("010-1234-5678");
        resumeRequest.setEmail("prgrms@naver.com");
        resumeRequest.setAddress("서울시 강남구");
        resumeRequest.setGitAddress("https://github.com/kim");
        resumeRequest.setBlogAddress("https://kim.blog");

        ResumeResponse response = resumeService.createResume(resumeRequest,mockUser);
        assertEquals("이력서 생성이 완료되었습니다.", response.getMessage());
    }
}
