package com.pofo.backend.domain.user.join.repository;

import com.pofo.backend.domain.user.join.entity.Oauth;
import com.pofo.backend.domain.user.join.entity.User;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OauthRepository extends JpaRepository<Oauth, Long> {
    Optional<Oauth> findByProviderAndIdentify(Oauth.@NotNull(message = "provider 값이 없습니다. ") Provider provider, @NotNull(message = "identify 값이 필요합니다.") String identify);

    Optional<Oauth> findByUserAndProvider(User nowUser, Oauth.Provider provider);

    Optional<Oauth> findByUser(User user);

    Optional<Oauth> findByIdentifyAndProvider(String identify, Oauth.Provider provider);

    void deleteByUser(User user);
}
