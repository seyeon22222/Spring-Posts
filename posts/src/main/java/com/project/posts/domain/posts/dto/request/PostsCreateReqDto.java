package com.project.posts.domain.posts.dto.request;

import java.util.List;

import com.project.posts.data.Posts;
import com.project.posts.data.Users;
import com.project.posts.data.type.Role;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class PostsCreateReqDto {

	@NotNull
	@Column(columnDefinition = "VARCHAR(50)")
	private String title;

	@NotNull
	@Column(columnDefinition = "VARCHAR(500)")
	private String content;

	private List<String> tags;

	public static Posts toEntity(Users user, String title, String content, Role role) {
		return new Posts(title, content, user, role);
	}

}
