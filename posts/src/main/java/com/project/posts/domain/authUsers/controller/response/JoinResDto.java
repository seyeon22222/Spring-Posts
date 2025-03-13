package com.project.posts.domain.authUsers.controller.response;

import com.project.posts.data.AuthUsers;
import com.project.posts.data.Users;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JoinResDto {

	private String id;
	private String username;

	@Builder
	public JoinResDto(String id, String username) {
		this.id = id;
		this.username = username;
	}

	public static JoinResDto toDto(Users users, AuthUsers authUsers) {
		return JoinResDto.builder()
			.id(authUsers.getLoginId())
			.username(users.getUsername())
			.build();
	}
}
