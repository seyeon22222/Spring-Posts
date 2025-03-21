package com.project.posts.domain.comments.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.project.posts.data.Comments;
import com.project.posts.data.Posts;
import com.project.posts.repository.CommentsRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentsService {

	private final CommentsRepository commentsRepository;

	@Transactional
	public void deleteComments(Posts post) {
		List<Comments> commentsList = commentsRepository.findAllByPosts(post);
		for (Comments comment : commentsList) {
			comment.delete();
		}
	}
}
