package com.pofo.backend.domain.resume.resume.entity;

import com.pofo.backend.common.jpa.entity.BaseTime;
import com.pofo.backend.domain.user.entity.User;
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

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "resumes")
public class Resume extends BaseTime {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private LocalDate birth;
    @Column(nullable = false)
    private String number;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String address;
    private String gitAddress;
    private String blogAddress;


}
