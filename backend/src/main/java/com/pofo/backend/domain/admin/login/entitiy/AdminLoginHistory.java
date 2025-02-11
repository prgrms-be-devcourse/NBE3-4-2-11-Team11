package com.pofo.backend.domain.admin.login.entitiy;

import com.pofo.backend.common.jpa.entity.BaseTime;
import jakarta.persistence.*;
import lombok.*;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "admin_login_histories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminLoginHistory extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "admin_id", referencedColumnName = "id", nullable = false)
    private Admin admin; // admin_id 외래 키 필드

    @Column(name = "failure_count", nullable = false, columnDefinition = "int default 0")
    private int failureCount = 0;

    @Column(name = "login_status", nullable = false)
    private byte loginStatus; // TINYINT로 저장

    public static final byte FAILED = 0;
    public static final byte SUCCESS = 1;

}