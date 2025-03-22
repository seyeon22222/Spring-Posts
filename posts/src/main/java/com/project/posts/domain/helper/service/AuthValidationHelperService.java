package com.project.posts.domain.helper.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.posts.data.AuthUsers;
import com.project.posts.data.Authority;
import com.project.posts.data.Posts;
import com.project.posts.data.Users;
import com.project.posts.data.type.Role;
import com.project.posts.domain.authUsers.service.AuthUsersService;
import com.project.posts.domain.authority.service.AuthorityService;
import com.project.posts.domain.users.service.UsersService;
import com.project.posts.exception.CustomError;
import com.project.posts.exception.CustomException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthValidationHelperService {

	private final AuthUsersService authUsersService;
	private final AuthorityService authorityService;
	private final UsersService usersService;


	public Users getValidatedUser(String loginId) {
		AuthUsers authUser = authUsersService.getAuthUser(loginId);
		return usersService.getAuthUser(authUser);
	}

	public AuthUsers getAuthUser(String loginId) {
		return authUsersService.getAuthUser(loginId);
	}

	public Role checkRole(String role, String loginId) {

		List<Authority> roles = authorityService.getAuthorities(authUsersService.getAuthUser(loginId));
		if (!Role.contains(role))
			throw new CustomException(CustomError.INVALID_ROLE);

		Role targetRole = Role.valueOf(role.toUpperCase());

		for (Authority a : roles) {
			Role userRole = a.getRole();
			if (userRole == targetRole)
				return targetRole;
			if (userRole.canAccess(targetRole))
				return targetRole;
		}
		throw new CustomException(CustomError.NOT_HAVE_ACCESS);
	}
}
