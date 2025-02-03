package com.pofo.backend.domain.user.join.repository;

import com.pofo.backend.domain.user.join.entity.Oauths;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OauthsRepository extends JpaRepository<Oauths, Long> {
    Optional<Oauths> findByProviderAndIdentify(Oauths.@NotNull(message = "provider 값이 없습니다. ") Provider provider, @NotNull(message = "identify 값이 필요합니다.") String identify);
}
