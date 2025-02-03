package com.pofo.backend.domain.resume.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.pofo.backend.domain.resume.resume.dto.request.ResumeCreateRequest;
import com.pofo.backend.domain.resume.resume.dto.response.ResumeCreateResponse;
import com.pofo.backend.domain.resume.resume.dto.response.ResumeResponse;
import com.pofo.backend.domain.resume.resume.entity.Resume;
import com.pofo.backend.domain.resume.resume.exception.ResumeCreationException;
import com.pofo.backend.domain.resume.resume.mapper.ResumeMapper;
import com.pofo.backend.domain.resume.resume.repository.ResumeRepository;
import com.pofo.backend.domain.resume.resume.service.ResumeService;
import com.pofo.backend.domain.user.entity.User;
import java.time.LocalDate;
import java.util.Optional;
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
    private ResumeRepository resumeRepository;

    @Mock
    private ResumeMapper resumeMapper;

    @Mock
    private User mockUser;

    @Mock
    private Resume mockResume;

    @Mock
    private ResumeResponse mockResumeResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(mockUser.getId()).thenReturn(1L);
        when(mockResume.getId()).thenReturn(1L);
        when(mockResume.getName()).thenReturn("김상진");
        when(mockResume.getEmail()).thenReturn("prgrms@naver.com");
    }

    private ResumeCreateRequest createResumeRequest() {
        ResumeCreateRequest resumeCreateRequest = new ResumeCreateRequest();
        resumeCreateRequest.setName("김상진");
        resumeCreateRequest.setBirth(LocalDate.of(2000, 11, 18));
        resumeCreateRequest.setNumber("010-1234-5678");
        resumeCreateRequest.setEmail("prgrms@naver.com");
        resumeCreateRequest.setAddress("서울시 강남구");
        resumeCreateRequest.setGitAddress("https://github.com/kim");
        resumeCreateRequest.setBlogAddress("https://kim.blog");
        return resumeCreateRequest;
    }

    @Test
    @DisplayName("이력서 생성 성공")
    void createResume() {
        ResumeCreateRequest resumeCreateRequest = createResumeRequest();
        ResumeCreateResponse response = resumeService.createResume(resumeCreateRequest, mockUser);
        assertEquals("이력서 생성이 완료되었습니다.", response.getMessage());
    }

    @Test
    @DisplayName("이력서 생성 실패 - 사용자 정보 없음")
    void createResumeWithNullUser() {
        User nullUser = null;
        ResumeCreateRequest resumeCreateRequest = createResumeRequest();
        ResumeCreationException exception = assertThrows(ResumeCreationException.class, () -> {
            resumeService.createResume(resumeCreateRequest, nullUser);
        });
        assertEquals("사용자 정보가 존재하지 않습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("이력서 생성 실패 - 예외 발생")
    void createResumeThrowsException() {
        doThrow(new RuntimeException("이력서 생성 중 오류가 발생했습니다."))
            .when(mockUser).getId();
        ResumeCreateRequest resumeCreateRequest = createResumeRequest();
        try {
            resumeService.createResume(resumeCreateRequest, mockUser);
        } catch (RuntimeException e) {
            assertEquals("이력서 생성 중 오류가 발생했습니다.", e.getMessage());
        }
    }

    @Test
    @DisplayName("이력서 조회 성공")
    void getResumeByUser_success() {
        when(resumeRepository.findByUser(mockUser)).thenReturn(Optional.of(mockResume));

        when(mockResumeResponse.getName()).thenReturn("김상진");
        when(mockResumeResponse.getEmail()).thenReturn("prgrms@naver.com");

        when(resumeMapper.resumeToResumeResponse(mockResume)).thenReturn(mockResumeResponse);

        ResumeResponse response = resumeService.getResumeByUser(mockUser);

        assertEquals("김상진", response.getName());
        assertEquals("prgrms@naver.com", response.getEmail());

        verify(resumeRepository).findByUser(mockUser);
        verify(resumeMapper).resumeToResumeResponse(mockResume);
    }

    @Test
    @DisplayName("이력서 조회 실패 - 이력서 없음")
    void getResumeByUser_notFound() {
        // Given
        when(resumeRepository.findByUser(mockUser)).thenReturn(Optional.empty());

        // When & Then
        ResumeCreationException exception = assertThrows(ResumeCreationException.class, () -> {
            resumeService.getResumeByUser(mockUser);
        });
        assertEquals("이력서가 존재하지 않습니다.", exception.getMessage());
        verify(resumeRepository).findByUser(mockUser);
    }

    @Test
    @DisplayName("이력서 조회 실패 - 사용자 정보 없음")
    void getResumeByUser_nullUser() {
        // Given
        User nullUser = null;

        // When & Then
        ResumeCreationException exception = assertThrows(ResumeCreationException.class, () -> {
            resumeService.getResumeByUser(nullUser);
        });
        assertEquals("사용자 정보가 존재하지 않습니다.", exception.getMessage());
    }
}
