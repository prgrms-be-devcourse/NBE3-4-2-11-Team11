package com.pofo.backend.domain.comment.entity;

import com.pofo.backend.common.jpa.entity.BaseTime;
import com.pofo.backend.domain.inquiry.entity.Inquiry;
import com.pofo.backend.domain.user.join.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "comments")
public class Comment extends BaseTime {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inquiry_id", nullable = false)
    private Inquiry inquiry;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    public void update(String content) {
        this.content = content;
    }
}