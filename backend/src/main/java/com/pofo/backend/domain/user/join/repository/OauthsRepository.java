package com.pofo.backend.domain.user.join.repository;

import com.pofo.backend.domain.user.join.entity.Oauths;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OauthsRepository extends JpaRepository<Oauths, Long> {
}
