package com.project.posts.security.service;

import org.springframework.stereotype.Service;

import com.project.posts.data.RefreshToken;
import com.project.posts.repository.RefreshTokenRepository;
import com.project.posts.security.util.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final RefreshTokenRepository refreshTokenRepository;
	private final JwtUtil jwtUtil;

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
}
