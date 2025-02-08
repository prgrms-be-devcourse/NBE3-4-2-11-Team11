package com.pofo.backend.domain.resume.activity.activity.service;

import com.pofo.backend.domain.resume.activity.activity.dto.ActivityRequest;
import com.pofo.backend.domain.resume.activity.activity.entity.Activity;
import com.pofo.backend.domain.resume.activity.activity.repository.ActivityRepository;
import com.pofo.backend.domain.resume.activity.award.dto.AwardRequest;
import com.pofo.backend.domain.resume.activity.award.service.AwardService;
import com.pofo.backend.domain.resume.resume.entity.Resume;
import com.pofo.backend.domain.resume.resume.exception.ResumeCreationException;
import com.pofo.backend.domain.resume.resume.repository.ResumeRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final ResumeRepository resumeRepository;
    private final AwardService awardService;

    public void addActivities(Long resumeId, List<ActivityRequest> activities) {
        Resume resume = resumeRepository.findById(resumeId)
            .orElseThrow(() -> new ResumeCreationException("이력서를 찾을 수 없습니다."));

        List<Activity> activityEntities = activities.stream()
            .map(activityRequest -> Activity.builder()
                .name(activityRequest.getName())
                .history(activityRequest.getHistory())
                .startDate(activityRequest.getStartDate())
                .endDate(activityRequest.getEndDate())
                .resume(resume)
                .build())
            .collect(Collectors.toList());

        activityRepository.saveAll(activityEntities);

        for (int i = 0; i < activityEntities.size(); i++) {
            Activity activity = activityEntities.get(i);
            List<AwardRequest> awards = activities.get(i).getAwards();
            if (awards != null) {
                awardService.addAwards(activity.getId(), awards);
            }
        }
    }

    public void updateActivities(Long resumeId, List<ActivityRequest> activities) {
        activityRepository.deleteByResumeId(resumeId);
        addActivities(resumeId, activities);
    }
}
