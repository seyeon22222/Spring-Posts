package com.project.posts.domain.users.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.posts.data.AuthUsers;
import com.project.posts.data.Users;
import com.project.posts.exception.CustomError;
import com.project.posts.exception.CustomException;
import com.project.posts.repository.UsersRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UsersService {

	private final UsersRepository usersRepository;

	public Users getAuthUser(AuthUsers authUser) {
		return usersRepository.findByAuthUsers(authUser)
			.orElseThrow(() -> new CustomException(CustomError.USER_NOT_FOUND));
	}
}
