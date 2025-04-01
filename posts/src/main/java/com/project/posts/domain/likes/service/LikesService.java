package com.project.posts.domain.likes.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.posts.data.Likes;
import com.project.posts.data.Posts;
import com.project.posts.data.Tags;
import com.project.posts.data.Users;
import com.project.posts.domain.helper.service.AuthValidationHelperService;
import com.project.posts.repository.LikesRepository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikesService {

	private final LikesRepository likesRepository;
	private final AuthValidationHelperService authValidationHelperService;
	private final EntityManager em;

	@Transactional
	public void deleteLikes(Posts post) {
		List<Likes> tags = likesRepository.findAllByPosts(post);
		likesRepository.deleteAll(tags);
	}

	@Transactional
	public Boolean createOrCancelLikes(String loginId, Posts post, String role) {
		Users user = authValidationHelperService.getValidatedUser(loginId, role);

		if (likesRepository.findByPostsAndUsers(post, user).isPresent()) {
			likesRepository.findByPostsAndUsers(post, user).ifPresent(likes -> {
				likesRepository.delete(likes);
				post.deleteLikes();
			});
			return false;
		}
		Likes likes = Likes.builder()
			.posts(post)
			.users(user)
			.build();

		post.addLikes();
		em.flush();
		likesRepository.save(likes);
		return true;
	}
}
