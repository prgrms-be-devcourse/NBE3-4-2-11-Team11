package com.pofo.backend.domain.resume.activity.award.entity;

import com.pofo.backend.common.jpa.entity.BaseTime;
import com.pofo.backend.domain.resume.activity.activity.entity.Activity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "awards")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(toBuilder = true)
public class Award extends BaseTime {
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String institution;
    @Column(nullable = false)
    private LocalDate awardDate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id", nullable = false)
    private Activity activity;
}
