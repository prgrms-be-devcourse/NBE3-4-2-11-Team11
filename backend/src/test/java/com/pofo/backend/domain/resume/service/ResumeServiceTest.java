package com.pofo.backend.domain.resume.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.pofo.backend.domain.resume.resume.dto.request.ResumeRequest;
import com.pofo.backend.domain.resume.resume.dto.response.ResumeCreateResponse;
import com.pofo.backend.domain.resume.resume.dto.response.ResumeResponse;
import com.pofo.backend.domain.resume.resume.entity.Resume;
import com.pofo.backend.domain.resume.resume.exception.ResumeCreationException;
import com.pofo.backend.domain.resume.resume.exception.UnauthorizedActionException;
import com.pofo.backend.domain.resume.resume.mapper.ResumeMapper;
import com.pofo.backend.domain.resume.resume.repository.ResumeRepository;
import com.pofo.backend.domain.resume.resume.service.ResumeService;

import java.time.LocalDate;
import java.util.Optional;

import com.pofo.backend.domain.user.join.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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
        when(mockResume.getUser()).thenReturn(mockUser);
    }

    private ResumeRequest createResumeRequest() {
        ResumeRequest resumeRequest = new ResumeRequest();
        resumeRequest.setName("김상진");
        resumeRequest.setBirth(LocalDate.of(2000, 11, 18));
        resumeRequest.setNumber("010-1234-5678");
        resumeRequest.setEmail("prgrms@naver.com");
        resumeRequest.setAddress("서울시 강남구");
        resumeRequest.setGitAddress("https://github.com/kim");
        resumeRequest.setBlogAddress("https://kim.blog");
        return resumeRequest;
    }

    @Test
    @DisplayName("이력서 생성 성공")
    void createResume() {
        ResumeRequest resumeRequest = createResumeRequest();
        ResumeCreateResponse response = resumeService.createResume(resumeRequest, mockUser);
        assertEquals("이력서 생성이 완료되었습니다.", response.getMessage());
    }

    @Test
    @DisplayName("이력서 생성 실패 - 예외 발생")
    void createResumeThrowsException() {
        doThrow(new RuntimeException("서버 오류로 인해 이력서 생성에 실패했습니다."))
            .when(mockUser).getId();
        ResumeRequest resumeRequest = createResumeRequest();
        try {
            resumeService.createResume(resumeRequest, mockUser);
        } catch (RuntimeException e) {
            assertEquals("서버 오류로 인해 이력서 생성에 실패했습니다.", e.getMessage());
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
        when(resumeRepository.findByUser(mockUser)).thenReturn(Optional.empty());

        ResumeCreationException exception = assertThrows(ResumeCreationException.class, () -> {
            resumeService.getResumeByUser(mockUser);
        });
        assertEquals("이력서가 존재하지 않습니다.", exception.getMessage());
        verify(resumeRepository).findByUser(mockUser);
    }

    @Test
    @DisplayName("이력서 수정 성공")
    void updateResume_success() {
        ResumeRequest resumeRequest = createResumeRequest();
        Resume realResume = Resume.builder()
            .name("김상진")
            .email("prgrmsNo@naver.com")
            .user(mockUser)
            .build();

        when(resumeRepository.findById(1L)).thenReturn(Optional.of(realResume));

        ResumeCreateResponse response = resumeService.updateResume(1L, resumeRequest, mockUser);

        assertEquals("이력서 수정이 완료되었습니다.", response.getMessage());
        verify(resumeRepository).findById(1L);
        verify(resumeRepository).save(any(Resume.class));
    }

    @Test
    @DisplayName("이력서 수정 실패 - 이력서가 존재하지 않음")
    void updateResume_notFound() {
        ResumeRequest resumeRequest = createResumeRequest();

        when(resumeRepository.findById(1L)).thenReturn(Optional.empty());

        ResumeCreationException exception = assertThrows(ResumeCreationException.class, () -> {
            resumeService.updateResume(1L, resumeRequest, mockUser);
        });
        assertEquals("이력서가 존재하지 않습니다.", exception.getMessage());
        verify(resumeRepository).findById(1L);
    }

    @Test
    @DisplayName("이력서 수정 실패 - 권한 없음")
    void updateResume_noPermission() {
        ResumeRequest resumeRequest = createResumeRequest();
        User differentUser = Mockito.mock(User.class);

        when(resumeRepository.findById(1L)).thenReturn(Optional.of(mockResume));
        when(mockResume.getUser()).thenReturn(differentUser);

        UnauthorizedActionException exception = assertThrows(UnauthorizedActionException.class, () -> {
            resumeService.updateResume(1L, resumeRequest, mockUser);
        });
        assertEquals("이력서를 수정할 권한이 없습니다.", exception.getMessage());
        verify(resumeRepository).findById(1L);
    }

    @Test
    @DisplayName("이력서 삭제 성공")
    void deleteResume_success() {
        Long resumeId = 1L;
        when(resumeRepository.findById(resumeId)).thenReturn(Optional.of(mockResume));
        when(mockResume.getUser()).thenReturn(mockUser);

        resumeService.deleteResume(resumeId, mockUser);

        verify(resumeRepository).delete(mockResume);
    }

    @Test
    @DisplayName("이력서 삭제 실패 - 이력서 없음")
    void deleteResume_notFound() {
        Long resumeId = 1L;
        when(resumeRepository.findById(resumeId)).thenReturn(Optional.empty());

        ResumeCreationException exception = assertThrows(ResumeCreationException.class, () -> {
            resumeService.deleteResume(resumeId, mockUser);
        });
        assertEquals("이력서가 존재하지 않습니다.", exception.getMessage());
        verify(resumeRepository).findById(resumeId);
    }

    @Test
    @DisplayName("이력서 삭제 실패 - 권한 없음")
    void deleteResume_noPermission() {
        Long resumeId = 1L;
        User differentUser = Mockito.mock(User.class);

        when(resumeRepository.findById(resumeId)).thenReturn(Optional.of(mockResume));
        when(mockResume.getUser()).thenReturn(differentUser);

        UnauthorizedActionException exception = assertThrows(UnauthorizedActionException.class, () -> {
            resumeService.deleteResume(resumeId, mockUser);
        });
        assertEquals("이력서를 삭제할 권한이 없습니다.", exception.getMessage());
        verify(resumeRepository).findById(resumeId);
    }
}
