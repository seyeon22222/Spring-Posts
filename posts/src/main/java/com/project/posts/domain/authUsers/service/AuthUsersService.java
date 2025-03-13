package com.project.posts.domain.authUsers.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.posts.data.AuthUsers;
import com.project.posts.data.Users;
import com.project.posts.data.type.Role;
import com.project.posts.domain.authUsers.controller.request.JoinReqDto;
import com.project.posts.domain.authUsers.controller.response.JoinResDto;
import com.project.posts.exception.CustomError;
import com.project.posts.exception.CustomException;
import com.project.posts.repository.AuthUsersRepository;
import com.project.posts.repository.AuthorityRepository;
import com.project.posts.repository.RefreshTokenRepository;
import com.project.posts.repository.UsersRepository;
import com.project.posts.security.util.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthUsersService {

	private final AuthUsersRepository authUsersRepository;
	private final UsersRepository usersRepository;
	private final AuthorityRepository authorityRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final JwtUtil jwtUtil;
	private final RefreshTokenRepository refreshTokenRepository;

	@Transactional
	public JoinResDto createUsers(JoinReqDto joinReqDto) {
		validateDuplicationId(joinReqDto.getLoginId());

		AuthUsers authUsers = authUsersRepository.save(
			AuthUsers.builder()
				.loginId(joinReqDto.getLoginId())
				.password(bCryptPasswordEncoder.encode(joinReqDto.getPassword()))
				.build()
		);

		authorityRepository.save(authUsers.createAuthority(Role.USER));
		authorityRepository.save(authUsers.createAuthority(Role.BEGINNER));

		Users users = usersRepository.save(authUsers.createUsers(joinReqDto.getUsername()));

		return JoinResDto.toDto(users, authUsers);
	}

	@Transactional
	public void logout(HttpServletRequest request) {
		String token = resolveToken(request);
		System.out.println(token);
		if (token != null) {
			String username = jwtUtil.getUsernameFromToken(token);
			refreshTokenRepository.deleteByUser_LoginId(username);
		}
	}

	private String resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		} else
			return bearerToken;
	}

	private void validateDuplicationId(String loginId) {
		if (authUsersRepository.existsByLoginId(loginId)) {
			throw new CustomException(CustomError.USER_DUPLICATION_ID);
		}
	}
}
