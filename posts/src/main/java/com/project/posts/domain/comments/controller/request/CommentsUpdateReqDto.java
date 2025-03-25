package com.project.posts.domain.comments.controller.request;

import lombok.Getter;

@Getter
public class CommentsUpdateReqDto {

	private String content;

	private Boolean status;
}
