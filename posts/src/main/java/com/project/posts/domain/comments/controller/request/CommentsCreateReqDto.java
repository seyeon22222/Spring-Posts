package com.project.posts.domain.comments.controller.request;

import lombok.Getter;

@Getter
public class CommentsCreateReqDto {

	private Long postId;
	private String content;
	private Long level;
	private Long affiliation;
}
