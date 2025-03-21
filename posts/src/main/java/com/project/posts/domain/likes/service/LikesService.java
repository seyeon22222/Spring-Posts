package com.project.posts.domain.likes.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.posts.data.Likes;
import com.project.posts.data.Posts;
import com.project.posts.data.Tags;
import com.project.posts.repository.LikesRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikesService {

	private final LikesRepository likesRepository;

	@Transactional
	public void deleteLikes(Posts post) {
		List<Likes> tags = likesRepository.findAllByPosts(post);
		likesRepository.deleteAll(tags);
	}
}
