package com.pofo.backend.domain.admin.login.entitiy;

import jakarta.persistence.*;
import lombok.*;
import com.pofo.backend.common.jpa.entity.BaseTime;

import java.util.List;

@Entity
@Table(name = "admins")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Admin extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @Column(name = "failure_count", nullable = false, columnDefinition = "int default 0")
    private int failureCount = 0;

//
//    @OneToMany(mappedBy = "admin", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private List<AdminLoginHistory> loginHistories;  // 로그인 기록 목록

    public enum Status {
        ACTIVE,
        INACTIVE
    }
}
