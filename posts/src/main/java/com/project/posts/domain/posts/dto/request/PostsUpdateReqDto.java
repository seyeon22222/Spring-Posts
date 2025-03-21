package com.project.posts.domain.posts.dto.request;

import java.util.List;

import lombok.Getter;

@Getter
public class PostsUpdateReqDto {

	private String title;
	private String content;
	private List<String> tags;
	private Boolean status;


}
