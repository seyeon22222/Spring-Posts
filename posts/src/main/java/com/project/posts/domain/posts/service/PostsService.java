package com.project.posts.domain.posts.service;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.redis.core.RedisTemplate;

import com.project.posts.data.Posts;
import com.project.posts.data.Users;
import com.project.posts.domain.comments.controller.response.CommentsResponseDto;
import com.project.posts.domain.helper.service.AuthValidationHelperService;
import com.project.posts.domain.helper.service.PostHelperService;
import com.project.posts.domain.posts.dto.request.PostsCreateReqDto;
import com.project.posts.domain.posts.dto.request.PostsUpdateReqDto;
import com.project.posts.domain.posts.dto.response.PostsDetailResDto;
import com.project.posts.domain.posts.dto.response.PostsUpdateResponseDto;
import com.project.posts.exception.CustomError;
import com.project.posts.exception.CustomException;
import com.project.posts.repository.PostsRepository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostsService {

	private final AuthValidationHelperService authValidationHelperService;
	private final PostHelperService postHelperService;
	private final PostsRepository postsRepository;
	private final EntityManager em;
	private final RedisTemplate<String, Object> redisTemplate;

	private static final String VIEWED_USERS_KEY = "post:viewed:";
	private static final String VIEW_COUNT_KEY = "post:viewCount:";


	@Transactional
	public void createPost(String loginId, PostsCreateReqDto dto, String role) {
		Users user = authValidationHelperService.getValidatedUser(loginId, role);
		postHelperService.moveImagesFile(dto.getContent());
		String contentWithImages = processImageUrls(dto.getContent());
		Posts post = PostsCreateReqDto.toEntity(user, dto.getTitle(), contentWithImages,
			authValidationHelperService.checkRole(role, loginId));
		postsRepository.save(post);
		em.flush();

		postHelperService.saveTags(dto.getTags(), post);
		postHelperService.saveImages(contentWithImages, post); // 이미지 저장
	}

	@Transactional
	public PostsUpdateResponseDto updatePosts(String loginId, PostsUpdateReqDto dto, Long postId) {
		Posts post = getPostWithAuthorValidation(loginId, postId);
		postHelperService.moveImagesFile(dto.getContent());
		String contentWithImages = processImageUrls(dto.getContent());

		postHelperService.updateImages(post, contentWithImages, dto.getStatus());
		post.update(dto.getTitle(), contentWithImages, dto.getStatus());
		postHelperService.updateTags(post, dto.getTags());

		return PostsUpdateResponseDto.toDto(post, dto.getTags());
	}

	@Transactional
	public void deletePosts(String loginId, Long postId) {
		Posts post = getPostWithAuthorValidation(loginId, postId);

		postHelperService.deleteCascade(post);
		post.delete();
		postsRepository.save(post);
	}

	public PostsDetailResDto getPosts(Long postId, String loginId) {
		Posts post = postsRepository.findById(postId)
			.orElseThrow(() -> new CustomException(CustomError.POST_NOT_FOUND));
		authValidationHelperService.validateRole(loginId, post.getRole());
		List<String> tags = postHelperService.getTags(post);
		Page<CommentsResponseDto> comments = postHelperService.getFirstPageComments(post);
		return PostsDetailResDto.toDto(post, tags, comments);
	}

	public Posts getSimplePost(Long postId) {
		return postsRepository.findById(postId)
			.orElseThrow(() -> new CustomException(CustomError.POST_NOT_FOUND));
	}

	@Transactional
	public void increaseViews(Long postId, String userName) {
		String viewedKey = VIEWED_USERS_KEY + postId;
		String viewCountKey = VIEW_COUNT_KEY + postId;

		// 중복 조회 여부 확인
		Boolean hasViewed = redisTemplate.opsForSet().isMember(viewedKey, userName);

		if (Boolean.FALSE.equals(hasViewed)) {
			// ✅ Redis에 유저 저장 (중복 방지용)
			redisTemplate.opsForSet().add(viewedKey, userName);
			redisTemplate.expire(viewedKey, 24, TimeUnit.HOURS);

			// ✅ Redis에 조회수 +1 누적
			redisTemplate.opsForValue().increment(viewCountKey);
			redisTemplate.expire(viewCountKey, 1, TimeUnit.HOURS); // 너무 오래 안가게 TTL
		}
	}

	private Posts getPostWithAuthorValidation(String loginId, Long postId) {
		Users user = authValidationHelperService.getSimpleUser(loginId);

		Posts post = postsRepository.findById(postId)
			.orElseThrow(() -> new CustomException(CustomError.POST_NOT_FOUND));

		if (!post.getAuthor().equals(user.getUsername()))
			throw new CustomException(CustomError.USER_NOT_MATCH);

		return post;
	}

	private String processImageUrls(String content) {
		return content.replace("http://localhost:8080/SpringPosts/images/temp/",
			"http://localhost:8080/SpringPosts/images/");
	}
}
