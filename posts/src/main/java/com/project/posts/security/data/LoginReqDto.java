package com.project.posts.security.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LoginReqDto {

	private String loginId;
	private String password;

}
