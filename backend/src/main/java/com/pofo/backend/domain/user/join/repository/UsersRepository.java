package com.pofo.backend.domain.user.join.repository;

import com.pofo.backend.domain.user.join.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    List<User> findByNameAndSexAndAgeAndNickname(
            @NotBlank(message = "이름을 입력 해 주세요.") String name,
            User.@NotNull(message = "성별을 선택 해 주세요.") Sex sex,
            LocalDate age,
            @NotBlank(message = "닉네임을 입력 해 주세요.") String nickname
    );
}
