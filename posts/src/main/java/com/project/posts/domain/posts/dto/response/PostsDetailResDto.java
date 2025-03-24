package com.project.posts.domain.posts.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;

import com.project.posts.data.Comments;
import com.project.posts.data.Posts;
import com.project.posts.data.type.Role;
import com.project.posts.domain.comments.controller.response.CommentsResponseDto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PostsDetailResDto {

	private final Long id;

	private final String title;

	private final String content;

	private final String author;

	private final Boolean status;

	private final Long views;

	private final Long likes;

	private final Role role;

	private final List<String> tags;

	private final LocalDateTime createdDate;

	private final Page<CommentsResponseDto> comments;

	@Builder
	public PostsDetailResDto(Long id, String title, String content, String author, Boolean status, Long views, Long likes, Role role, List<String> tags, LocalDateTime createdDate, Page<CommentsResponseDto> comments) {
		this.id = id;
		this.title = title;
		this.content = content;
		this.author = author;
		this.status = status;
		this.views = views;
		this.likes = likes;
		this.role = role;
		this.tags = tags;
		this.createdDate = createdDate;
		this.comments = comments;
	}

	public static PostsDetailResDto toDto(Posts post, List<String> tags, Page<CommentsResponseDto> comments) {
		return PostsDetailResDto.builder()
			.id(post.getId())
			.title(post.getTitle())
			.content(post.getContent())
			.author(post.getAuthor())
			.status(post.getStatus())
			.views(post.getViews())
			.likes(post.getLikes())
			.role(post.getRole())
			.tags(tags)
			.createdDate(post.getCreatedAt())
			.comments(comments)
			.build();
	}
}
