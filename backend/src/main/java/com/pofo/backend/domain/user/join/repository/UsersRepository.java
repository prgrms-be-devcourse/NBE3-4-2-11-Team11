package com.pofo.backend.domain.user.join.repository;

import com.pofo.backend.domain.user.join.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {
}
