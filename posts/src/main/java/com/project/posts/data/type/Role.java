package com.project.posts.data.type;

import lombok.Getter;

@Getter
public enum Role {
	ADMIN("관리자"),
	USER("사용자"),
	BEGINNER("초보"),
	SENIOR("중수"),
	PROFESSOR("고수");


	private final String value;

	Role(String value) {
		this.value = value;
	}
}
