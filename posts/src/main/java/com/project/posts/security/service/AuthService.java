package com.project.posts.security.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;

import com.project.posts.data.AuthUsers;
import com.project.posts.data.RefreshToken;
import com.project.posts.exception.CustomError;
import com.project.posts.exception.CustomException;
import com.project.posts.repository.AuthUsersRepository;
import com.project.posts.repository.RefreshTokenRepository;
import com.project.posts.security.util.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final RefreshTokenRepository refreshTokenRepository;
	private final AuthUsersRepository authUsersRepository;
	private final JwtUtil jwtUtil;

	@Value("${spring.jwt.expiration.refresh}") // ✅ 프로퍼티 값 주입
	private long refreshTokenExpirationMs;

	public String refreshAccessToken(String expiredAccessToken) {
		String username = jwtUtil.getUsernameFromToken(expiredAccessToken);

		RefreshToken refreshToken = refreshTokenRepository.findByUser_LoginId(username)
			.orElse(null);

		// Refresh Token이 유효하면 새로운 Access Token 발급
		if (refreshToken != null && jwtUtil.validateToken(refreshToken.getToken())) {
			return jwtUtil.generateToken(username);
		}

		// Refresh Token이 유효하지 않으면 null 반환 (401 응답을 위해)
		return null;
	}

	@Transactional
	public void checkRefreshToken(String username) {
		RefreshToken storedToken = refreshTokenRepository.findByUser_LoginId(username)
			.orElse(null);

		if (storedToken == null || !jwtUtil.validateToken(storedToken.getToken())) {
			String newRefreshToken = jwtUtil.generateRefreshToken(username);
			LocalDateTime expiryDate = LocalDateTime.now().plusSeconds(refreshTokenExpirationMs / 1000);


			AuthUsers user = authUsersRepository.findByLoginId(username)
				.orElseThrow(() -> new CustomException(CustomError.USER_NOT_FOUND));

			if (storedToken != null) {
				refreshTokenRepository.delete(storedToken);
			}

			RefreshToken refreshTokenEntity = RefreshToken.builder()
				.token(newRefreshToken)
				.user(user)
				.expiryDate(expiryDate)
				.build();

			refreshTokenRepository.save(refreshTokenEntity);
		}
	}
}
