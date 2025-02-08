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
import com.pofo.backend.domain.resume.course.service.CourseService;
import com.pofo.backend.domain.resume.education.entity.Education;
import com.pofo.backend.domain.resume.education.service.EducationService;
import com.pofo.backend.domain.resume.experience.entity.Experience;
import com.pofo.backend.domain.resume.experience.service.ExperienceService;
import com.pofo.backend.domain.resume.language.entity.Language;
import com.pofo.backend.domain.resume.language.service.LanguageService;
import com.pofo.backend.domain.resume.license.service.LicenseService;
import com.pofo.backend.domain.resume.resume.dto.request.ResumeCreateRequest;
import com.pofo.backend.domain.resume.resume.dto.response.ResumeResponse;
import com.pofo.backend.domain.resume.resume.entity.Resume;
import com.pofo.backend.domain.resume.resume.exception.ResumeCreationException;
import com.pofo.backend.domain.resume.resume.repository.ResumeRepository;
import com.pofo.backend.domain.resume.resume.service.ResumeService;
import com.pofo.backend.domain.user.join.entity.User;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
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
    private CourseService courseService;
    @Mock
    private ExperienceService experienceService;
    @Mock
    private EducationService educationService;
    @Mock
    private LicenseService licenseService;
    @Mock
    private LanguageService languageService;

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
        when(resumeRepository.findByUser(mockUser)).thenReturn(Optional.of(mockResume));

        ResumeResponse result = resumeService.getResumeResponse(mockUser);

        assertEquals("김상진", result.getName());
        assertEquals("prgrms@naver.com", result.getEmail());
        verify(resumeRepository).findByUser(mockUser);
    }

    @Test
    @DisplayName("이력서 조회 실패 - 이력서 없음")
    void getResumeByUser_notFound() {
        when(resumeRepository.findByUser(mockUser)).thenReturn(Optional.empty());

        assertThrows(ResumeCreationException.class, () -> {
            resumeService.getResumeResponse(mockUser);
        });

        verify(resumeRepository).findByUser(mockUser);
    }

    @Test
    @DisplayName("이력서 생성 성공")
    void createResume_success() {
        ResumeCreateRequest request = createResumeRequest();
        when(resumeRepository.save(any(Resume.class))).thenReturn(mockResume);

        Resume result = resumeService.createResume(request, mockUser);

        assertNotNull(result);
        verify(resumeRepository).save(any(Resume.class));
        verify(activityService, times(0)).updateActivities(any(), any());
        verify(courseService, times(0)).updateCourses(any(), any());
        verify(experienceService, times(0)).updateExperiences(any(), any());
        verify(educationService, times(0)).updateEducations(any(), any());
        verify(licenseService, times(0)).updateLicenses(any(), any());
        verify(languageService, times(0)).updateLanguages(any(), any());
    }

    @Test
    @DisplayName("이력서 수정 성공")
    void updateResume_success() {
        User mockUser = mock(User.class);
        Resume mockResume = mock(Resume.class);

        ResumeCreateRequest request = createResumeRequest();

        when(resumeRepository.findByUser(mockUser)).thenReturn(Optional.of(mockResume));
        when(resumeRepository.save(any(Resume.class))).thenReturn(mockResume);
        when(mockResume.getUser()).thenReturn(mockUser);

        Resume result = resumeService.updateResume(request, mockUser);

        assertNotNull(result);
        verify(resumeRepository).findByUser(mockUser);
        verify(resumeRepository).save(any(Resume.class));
        verify(activityService, times(0)).updateActivities(any(), any());
        verify(courseService, times(0)).updateCourses(any(), any());
        verify(experienceService, times(0)).updateExperiences(any(), any());
        verify(educationService, times(0)).updateEducations(any(), any());
        verify(licenseService, times(0)).updateLicenses(any(), any());
        verify(languageService, times(0)).updateLanguages(any(), any());
    }



    @Test
    @DisplayName("이력서 수정 실패")
    void updateResume_noPermission() {
        ResumeCreateRequest request = createResumeRequest();
        User differentUser = mock(User.class);
        when(resumeRepository.findByUser(differentUser)).thenReturn(Optional.of(mockResume));

        assertThrows(ResumeCreationException.class, () -> {
            resumeService.updateResume(request, mockUser);
        });
    }

    @Test
    @DisplayName("이력서 삭제 성공")
    void deleteResume_success() {
        when(resumeRepository.findByUser(mockUser)).thenReturn(Optional.of(mockResume));
        when(mockResume.getUser()).thenReturn(mockUser);

        resumeService.deleteResume(mockUser);

        verify(resumeRepository).delete(mockResume);
    }

    @Test
    @DisplayName("이력서 삭제 실패")
    void deleteResume_noPermission() {
        User differentUser = mock(User.class);
        when(resumeRepository.findByUser(differentUser)).thenReturn(Optional.of(mockResume));
        when(mockResume.getUser()).thenReturn(differentUser);

        assertThrows(ResumeCreationException.class, () -> {
            resumeService.deleteResume(mockUser);
        });
    }

    @Test
    @DisplayName("이력서 조회 성공 - 교육, 언어, 경력 정보 포함")
    void getResumeByUser_withEducations_languages_and_experiences() {
        Set<Education> educations = Set.of(
            Education.builder()
                .name("서울대학교")
                .major("컴퓨터 공학")
                .startDate(LocalDate.of(2018, 3, 1))
                .endDate(LocalDate.of(2022, 2, 28))
                .status(Education.Status.GRADUATED)
                .resume(mockResume)
                .build()
        );

        Set<Language> languages = Set.of(
            Language.builder()
                .language("영어")
                .result("TOEIC 900")
                .certifiedDate(LocalDate.of(2020, 5, 20))
                .resume(mockResume)
                .build()
        );

        Set<Experience> experiences = Set.of(
            Experience.builder()
                .name("Google")
                .department("Engineering")
                .position("Software Engineer")
                .responsibility("Developed backend services")
                .startDate(LocalDate.of(2020, 5, 1))
                .endDate(LocalDate.of(2022, 5, 1))
                .resume(mockResume)
                .build()
        );


        when(mockResume.getEducations()).thenReturn(educations);
        when(mockResume.getLanguages()).thenReturn(languages);
        when(mockResume.getExperiences()).thenReturn(experiences);
        when(resumeRepository.findByUser(mockUser)).thenReturn(Optional.of(mockResume));

        ResumeResponse result = resumeService.getResumeResponse(mockUser);

        assertNotNull(result);
        assertEquals(1, result.getEducations().size());
        assertEquals("서울대학교", result.getEducations().iterator().next().getName());
        assertEquals("영어", result.getLanguages().iterator().next().getLanguage());
        assertEquals("Google", result.getExperiences().iterator().next().getName());
    }

}
