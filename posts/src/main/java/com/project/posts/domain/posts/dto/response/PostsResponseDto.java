package com.project.posts.domain.posts.dto.response;

import java.util.List;

import com.project.posts.data.Posts;
import com.project.posts.data.type.Role;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PostsResponseDto {

	private final Long id;

	private final String title;

	private final String content;

	private final String author;

	private final Boolean status;

	private final Long views;

	private final Long likes;

	private final Role role;

	private final List<String> tags;

	@Builder
	public PostsResponseDto(Long id, String title, String content, String author, Boolean status, Long views, Long likes, Role role, List<String> tags) {
		this.id = id;
		this.title = title;
		this.content = content;
		this.author = author;
		this.status = status;
		this.views = views;
		this.likes = likes;
		this.role = role;
		this.tags = tags;
	}


	public static PostsResponseDto toDto(Posts post, List<String> tags) {
		return PostsResponseDto.builder()
			.id(post.getId())
			.title(post.getTitle())
			.content(post.getContent())
			.author(post.getAuthor())
			.status(post.getStatus())
			.views(post.getViews())
			.likes(post.getLikes())
			.role(post.getRole())
			.tags(tags)
			.build();
	}
}
