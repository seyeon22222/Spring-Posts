package com.project.posts.domain.helper.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.posts.data.Posts;
import com.project.posts.domain.posts.service.PostsService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentHelperService {

	private final PostsService postsService;

	public Posts getSimplePost(Long postId) {
		return postsService.getSimplePost(postId);
	}
}
