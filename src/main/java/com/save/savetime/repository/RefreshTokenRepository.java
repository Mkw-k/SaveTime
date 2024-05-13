package com.save.savetime.repository;

import com.save.savetime.model.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    Optional<RefreshToken> findByKeyEmail(String keyEmail);
    Optional<RefreshToken> findByRefreshToken(String refreshToken);
    int deleteByKeyEmail(String keyEmail);
}
