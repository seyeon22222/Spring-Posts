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
import com.project.posts.repository.UsersRepository;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthUsersService {

	private final AuthUsersRepository authUsersRepository;
	private final UsersRepository usersRepository;
	private final AuthorityRepository authorityRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;

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

	private void validateDuplicationId(String loginId) {
		if (authUsersRepository.existsByLoginId(loginId)) {
			throw new CustomException(CustomError.USER_DUPLICATION_ID);
		}
	}
}
