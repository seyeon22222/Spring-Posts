package com.project.posts.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CustomError {
	//인증 유저 에러
	AUTH_USER_NOT_FOUND_ID(HttpStatus.NOT_FOUND, "AU100", "존재하지 않는 아이디 입니다."),

	//유저 에러
	USER_DUPLICATION_ID(HttpStatus.FORBIDDEN, "UR100", "중복된 아이디 입니다."),
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "UR101", "존재하지 않는 유저 입니다."),

	//권한 에러
	INVALID_ROLE(HttpStatus.BAD_REQUEST, "AU101", "잘못된 권한 입니다."),
	NOT_HAVE_ACCESS(HttpStatus.FORBIDDEN, "AU102", "접근 권한이 없습니다.");

	private final HttpStatus status;
	private final String code;
	private final String message;
}
