package com.pofo.backend.domain.reply.entity;

import com.pofo.backend.common.jpa.entity.BaseTime;
import com.pofo.backend.domain.admin.login.entitiy.Admin;
import com.pofo.backend.domain.inquiry.entity.Inquiry;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "replies")
public class Reply extends BaseTime {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private Admin admin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inquiry_id", nullable = false)
    private Inquiry inquiry;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    public void update(String content) {
        this.content = content;
    }
}
