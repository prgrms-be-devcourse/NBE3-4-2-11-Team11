package com.pofo.backend.domain.resume.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.pofo.backend.domain.resume.activity.activity.entity.Activity;
import com.pofo.backend.domain.resume.activity.activity.service.ActivityService;
import com.pofo.backend.domain.resume.activity.award.entity.Award;
import com.pofo.backend.domain.resume.education.entity.Education;
import com.pofo.backend.domain.resume.experience.entity.Experience;
import com.pofo.backend.domain.resume.language.entity.Language;
import com.pofo.backend.domain.resume.resume.dto.request.ResumeCreateRequest;
import com.pofo.backend.domain.resume.resume.entity.Resume;
import com.pofo.backend.domain.resume.resume.exception.ResumeCreationException;
import com.pofo.backend.domain.resume.resume.exception.UnauthorizedActionException;
import com.pofo.backend.domain.resume.resume.repository.ResumeRepository;
import com.pofo.backend.domain.resume.resume.service.ResumeService;
import com.pofo.backend.domain.user.join.entity.User;
import java.time.LocalDate;
import java.util.List;
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

    @Test
    @DisplayName("이력서 조회 성공 - 교육 정보 포함")
    void getResumeByUser_withEducations() {
        List<Education> educations = List.of(
            Education.builder()
                .name("서울대학교")
                .major("컴퓨터 공학")
                .startDate(LocalDate.of(2018, 3, 1))
                .endDate(LocalDate.of(2022, 2, 28))
                .status(Education.Status.GRADUATED)
                .resume(mockResume)
                .build()
        );

        when(mockResume.getEducations()).thenReturn(educations);
        when(resumeRepository.findResumeWithDetails(mockUser)).thenReturn(Optional.of(mockResume));

        Resume result = resumeService.getResumeByUser(mockUser);

        assertEquals("김상진", result.getName());
        assertEquals("prgrms@naver.com", result.getEmail());
        assertEquals(1, result.getEducations().size());
        assertEquals("서울대학교", result.getEducations().get(0).getName());
        assertEquals("컴퓨터 공학", result.getEducations().get(0).getMajor());
        assertEquals(Education.Status.GRADUATED, result.getEducations().get(0).getStatus());

        verify(resumeRepository).findResumeWithDetails(mockUser);
    }
    @Test
    @DisplayName("이력서 조회 성공 - 교육, 언어, 경력 정보 포함")
    void getResumeByUser_withEducations_languages_and_experiences() {
        // 교육 정보
        List<Education> educations = List.of(
            Education.builder()
                .name("서울대학교")
                .major("컴퓨터 공학")
                .startDate(LocalDate.of(2018, 3, 1))
                .endDate(LocalDate.of(2022, 2, 28))
                .status(Education.Status.GRADUATED)
                .resume(mockResume)
                .build()
        );

        // 언어 정보
        List<Language> languages = List.of(
            Language.builder()
                .language("영어")
                .result("TOEIC 900")
                .certifiedDate(LocalDate.of(2020, 5, 20))
                .name("서울대학교")
                .resume(mockResume)
                .build()
        );

        // 경력 정보
        List<Experience> experiences = List.of(
            Experience.builder()
                .name("카카오")
                .department("개발팀")
                .position("백엔드 개발자")
                .responsibility("API 개발 및 운영")
                .startDate(LocalDate.of(2020, 6, 1))
                .endDate(LocalDate.of(2023, 5, 31))
                .resume(mockResume)
                .build()
        );

        // Mocking the methods
        when(mockResume.getEducations()).thenReturn(educations);
        when(mockResume.getLanguages()).thenReturn(languages);
        when(mockResume.getExperiences()).thenReturn(experiences);
        when(resumeRepository.findResumeWithDetails(mockUser)).thenReturn(Optional.of(mockResume));

        // Resume 서비스 호출
        Resume result = resumeService.getResumeByUser(mockUser);

        // Assert 교육 정보
        assertEquals("김상진", result.getName());
        assertEquals("prgrms@naver.com", result.getEmail());
        assertEquals(1, result.getEducations().size());
        assertEquals("서울대학교", result.getEducations().get(0).getName());
        assertEquals("컴퓨터 공학", result.getEducations().get(0).getMajor());
        assertEquals(Education.Status.GRADUATED, result.getEducations().get(0).getStatus());

        // Assert 언어 정보
        assertEquals(1, result.getLanguages().size());
        assertEquals("영어", result.getLanguages().get(0).getLanguage());
        assertEquals("TOEIC 900", result.getLanguages().get(0).getResult());
        assertEquals(LocalDate.of(2020, 5, 20), result.getLanguages().get(0).getCertifiedDate());

        // Assert 경력 정보
        assertEquals(1, result.getExperiences().size());
        assertEquals("카카오", result.getExperiences().get(0).getName());
        assertEquals("개발팀", result.getExperiences().get(0).getDepartment());
        assertEquals("백엔드 개발자", result.getExperiences().get(0).getPosition());
        assertEquals("API 개발 및 운영", result.getExperiences().get(0).getResponsibility());
        assertEquals(LocalDate.of(2020, 6, 1), result.getExperiences().get(0).getStartDate());
        assertEquals(LocalDate.of(2023, 5, 31), result.getExperiences().get(0).getEndDate());

        // Verify resumeRepository interaction
        verify(resumeRepository).findResumeWithDetails(mockUser);
    }

    @Test
    @DisplayName("이력서 조회 성공 - 활동 및 상 내역 포함")
    void getResumeByUser_withActivitiesAndAwards() {
        // 활동과 상을 함께 생성
        Award award = Award.builder()
            .name("최우수 봉사자상")
            .institution("서울시청")
            .awardDate(LocalDate.of(2021, 1, 15))
            .build();

        Activity activity = Activity.builder()
            .name("봉사활동")
            .history("2020년 10월부터 12월까지 서울시에서 진행한 봉사활동")
            .startDate(LocalDate.of(2020, 10, 1))
            .endDate(LocalDate.of(2020, 12, 31))
            .awards(List.of(award))
            .resume(mockResume)
            .build();

        List<Activity> activities = List.of(activity);

        when(mockResume.getActivities()).thenReturn(activities);
        when(resumeRepository.findResumeWithDetails(mockUser)).thenReturn(Optional.of(mockResume));

        Resume result = resumeService.getResumeByUser(mockUser);

        assertEquals("김상진", result.getName());
        assertEquals("prgrms@naver.com", result.getEmail());
        assertEquals(1, result.getActivities().size());
        assertEquals("봉사활동", result.getActivities().get(0).getName());
        assertEquals("2020년 10월부터 12월까지 서울시에서 진행한 봉사활동", result.getActivities().get(0).getHistory());
        assertEquals(1, result.getActivities().get(0).getAwards().size());
        assertEquals("최우수 봉사자상", result.getActivities().get(0).getAwards().get(0).getName());
        assertEquals("서울시청", result.getActivities().get(0).getAwards().get(0).getInstitution());

        verify(resumeRepository).findResumeWithDetails(mockUser);
    }





}