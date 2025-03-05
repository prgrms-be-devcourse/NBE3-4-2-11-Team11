package com.pofo.backend.domain.user.join.repository;

import com.pofo.backend.domain.user.join.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    List<User> findByNameAndSexAndAgeAndNickname(
            @NotBlank(message = "이름을 입력 해 주세요.") String name,
            User.@NotNull(message = "성별을 선택 해 주세요.") Sex sex,
            LocalDate age,
            @NotBlank(message = "닉네임을 입력 해 주세요.") String nickname
    );


    List<User> findByLastLoginAtBeforeAndDormantFlgIn(LocalDateTime time, List<String> dormantFlags);

    // 새로 추가된 메서드: lastLoginAt이 threshold보다 이전이고, dormantFlg가 "N", null 또는 "N/A"인 사용자 조회
    @Query("SELECT u FROM User u WHERE u.lastLoginAt < :threshold AND (u.dormantFlg = 'N' OR u.dormantFlg IS NULL OR u.dormantFlg = 'N/A')")
    List<User> findInactiveUsers(@Param("threshold") LocalDateTime threshold);

}
