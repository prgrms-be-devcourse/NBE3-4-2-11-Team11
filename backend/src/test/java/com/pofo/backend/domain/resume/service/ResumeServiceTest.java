package com.pofo.backend.domain.resume.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.pofo.backend.domain.resume.activity.activity.service.ActivityService;
import com.pofo.backend.domain.resume.resume.dto.request.ResumeCreateRequest;
import com.pofo.backend.domain.resume.resume.entity.Resume;
import com.pofo.backend.domain.resume.resume.exception.ResumeCreationException;
import com.pofo.backend.domain.resume.resume.exception.UnauthorizedActionException;
import com.pofo.backend.domain.resume.resume.repository.ResumeRepository;
import com.pofo.backend.domain.resume.resume.service.ResumeService;
import com.pofo.backend.domain.user.join.entity.User;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@AutoConfigureWebMvc
class ResumeServiceTest {
    @Autowired
    private ResumeService resumeService;

    @MockitoBean
    private ResumeRepository resumeRepository;
    @Mock
    private ActivityService activityService;

    @Mock
    private User mockUser;
    @Mock
    private Resume mockResume;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(mockUser.getId()).thenReturn(1L);
        when(mockResume.getId()).thenReturn(1L);
        when(mockResume.getName()).thenReturn("김상진");
        when(mockResume.getEmail()).thenReturn("prgrms@naver.com");
        when(mockResume.getUser()).thenReturn(mockUser);
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
    @DisplayName("이력서 조회 성공")
    void getResumeByUser_success() {
        when(resumeRepository.findResumeWithDetails(mockUser)).thenReturn(Optional.of(mockResume));

        Resume result = resumeService.getResumeByUser(mockUser);

        assertEquals("김상진", result.getName());
        assertEquals("prgrms@naver.com", result.getEmail());
        verify(resumeRepository).findResumeWithDetails(mockUser);
    }

    @Test
    @DisplayName("이력서 조회 실패 - 이력서 없음")
    void getResumeByUser_notFound() {
        when(resumeRepository.findResumeWithDetails(mockUser)).thenReturn(Optional.empty());

        assertThrows(ResumeCreationException.class, () -> {
            resumeService.getResumeByUser(mockUser);
        });

        verify(resumeRepository).findResumeWithDetails(mockUser);
    }

    @Test
    @DisplayName("이력서 생성 성공")
    void createResume_success() {
        ResumeCreateRequest request = createResumeRequest();
        when(resumeRepository.save(any(Resume.class))).thenReturn(mockResume);

        Resume result = resumeService.createResume(request, mockUser);

        assertNotNull(result);
        verify(resumeRepository).save(any(Resume.class));
        verify(activityService, times(0)).updateActivities(any(), any());  // request.getActivities()가 null이므로
    }

    @Test
    @DisplayName("이력서 수정 성공")
    void updateResume_success() {
        ResumeCreateRequest request = createResumeRequest();
        when(resumeRepository.findById(1L)).thenReturn(Optional.of(mockResume));
        when(resumeRepository.save(any(Resume.class))).thenReturn(mockResume);
        when(mockResume.getUser()).thenReturn(mockUser);

        Resume result = resumeService.updateResume(1L, request, mockUser);

        assertNotNull(result);
        verify(resumeRepository).findById(1L);
        verify(resumeRepository).save(any(Resume.class));
    }

    @Test
    @DisplayName("이력서 수정 실패 - 권한 없음")
    void updateResume_noPermission() {
        ResumeCreateRequest request = createResumeRequest();
        User differentUser = mock(User.class);
        when(resumeRepository.findById(1L)).thenReturn(Optional.of(mockResume));
        when(mockResume.getUser()).thenReturn(differentUser);

        assertThrows(UnauthorizedActionException.class, () -> {
            resumeService.updateResume(1L, request, mockUser);
        });
    }

    @Test
    @DisplayName("이력서 삭제 성공")
    void deleteResume_success() {
        when(resumeRepository.findById(1L)).thenReturn(Optional.of(mockResume));
        when(mockResume.getUser()).thenReturn(mockUser);

        resumeService.deleteResume(1L, mockUser);

        verify(resumeRepository).delete(mockResume);
    }

    @Test
    @DisplayName("이력서 삭제 실패 - 권한 없음")
    void deleteResume_noPermission() {
        User differentUser = mock(User.class);
        when(resumeRepository.findById(1L)).thenReturn(Optional.of(mockResume));
        when(mockResume.getUser()).thenReturn(differentUser);

        assertThrows(UnauthorizedActionException.class, () -> {
            resumeService.deleteResume(1L, mockUser);
        });
    }
}