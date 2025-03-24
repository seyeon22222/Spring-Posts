package com.project.posts.domain.comments.controller.response;

import java.time.LocalDateTime;

import com.project.posts.data.Comments;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentsResponseDto {

	private Long id;
	private String content;
	private String author;
	private LocalDateTime createdAt;
	private Long level;
	private Long indexing;
	private Long affiliation;

	@Builder
	public CommentsResponseDto(Comments comment) {
		this.id = comment.getId();
		this.content = comment.getContent();
		this.author = comment.getUsers().getUsername();
		this.createdAt = comment.getCreatedAt();
		this.level = comment.getLevel();
		this.indexing = comment.getIndexing();
		this.affiliation = comment.getAffiliation();
	}
}
