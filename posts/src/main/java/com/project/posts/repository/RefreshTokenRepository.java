package com.project.posts.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.posts.data.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

	Optional<RefreshToken> findByUser_LoginId(String loginId);

	void deleteByUser_LoginId(String loginId);
}
