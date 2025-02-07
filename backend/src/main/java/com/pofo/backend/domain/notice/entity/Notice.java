package com.pofo.backend.domain.notice.entity;

import com.pofo.backend.common.jpa.entity.BaseTime;
import com.pofo.backend.domain.admin.login.entitiy.Admin;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "notices")
public class Notice extends BaseTime {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = true)
    private Admin admin;

    @Column(length = 100, nullable = false)
    private String subject;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    public void update(String subject, String content) {
        this.subject = subject;
        this.content = content;
    }
}
