package com.pofo.backend.domain.resume.activity.award.service;

import com.pofo.backend.domain.resume.activity.activity.entity.Activity;
import com.pofo.backend.domain.resume.activity.activity.repository.ActivityRepository;
import com.pofo.backend.domain.resume.activity.award.dto.AwardRequest;
import com.pofo.backend.domain.resume.activity.award.entity.Award;
import com.pofo.backend.domain.resume.activity.award.repository.AwardRepository;
import com.pofo.backend.domain.resume.resume.exception.ResumeCreationException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AwardService {

    private final AwardRepository awardRepository;
    private final ActivityRepository activityRepository;

    public void addAwards(Long activityId, List<AwardRequest> awards) {
        Activity activity = activityRepository.findById(activityId)
            .orElseThrow(() -> new ResumeCreationException("대외활동 내역을 찾을 수 없습니다."));

        List<Award> awardEntities = awards.stream()
            .map(awardRequest -> Award.builder()
                .name(awardRequest.getName())
                .institution(awardRequest.getInstitution())
                .awardDate(awardRequest.getAwardDate())
                .activity(activity)
                .build())
            .collect(Collectors.toList());

        awardRepository.saveAll(awardEntities);
    }
}
