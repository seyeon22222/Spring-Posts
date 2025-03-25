package com.project.posts.domain.comments.controller.request;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CommentsGetReqDto {

	@NotNull
	private Long postId;

	@NotNull
	private Integer page;

	private final Integer size = 10;

}
