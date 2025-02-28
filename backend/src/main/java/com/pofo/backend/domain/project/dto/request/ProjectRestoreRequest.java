package com.pofo.backend.domain.project.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ProjectRestoreRequest {
    private List<Long> projectIds;
}
