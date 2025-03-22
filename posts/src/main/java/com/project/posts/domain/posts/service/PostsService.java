package com.project.posts.domain.posts.service;

import static com.project.posts.domain.images.service.ImagesService.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.posts.data.AuthUsers;
import com.project.posts.data.Authority;
import com.project.posts.data.Posts;
import com.project.posts.data.Tags;
import com.project.posts.data.Users;
import com.project.posts.data.type.Role;
import com.project.posts.domain.authUsers.service.AuthUsersService;
import com.project.posts.domain.authority.service.AuthorityService;
import com.project.posts.domain.comments.service.CommentsService;
import com.project.posts.domain.helper.service.AuthValidationHelperService;
import com.project.posts.domain.helper.service.PostHelperService;
import com.project.posts.domain.images.service.ImagesService;
import com.project.posts.domain.likes.service.LikesService;
import com.project.posts.domain.posts.dto.request.PostsCreateReqDto;
import com.project.posts.domain.posts.dto.request.PostsUpdateReqDto;
import com.project.posts.domain.posts.dto.response.PostsResponseDto;
import com.project.posts.domain.tags.service.TagsService;
import com.project.posts.domain.users.service.UsersService;
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


	@Transactional
	public void createPost(String loginId, PostsCreateReqDto dto, String role) {
		Users user = authValidationHelperService.getValidatedUser(loginId);
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
	public PostsResponseDto updatePosts(String loginId, PostsUpdateReqDto dto, Long postId) {
		Posts post = getPostWithAuthorValidation(loginId, postId);
		postHelperService.moveImagesFile(dto.getContent());
		String contentWithImages = processImageUrls(dto.getContent());

		postHelperService.updateImages(post, contentWithImages, dto.getStatus());
		post.update(dto.getTitle(), contentWithImages, dto.getStatus());
		postHelperService.updateTags(post, dto.getTags());

		return PostsResponseDto.toDto(post, dto.getTags());
	}

	@Transactional
	public void deletePosts(String loginId, Long postId) {
		Posts post = getPostWithAuthorValidation(loginId, postId);

		postHelperService.deleteCascade(post);
		post.delete();
		postsRepository.save(post);
	}

	private Posts getPostWithAuthorValidation(String loginId, Long postId) {
		Users user = authValidationHelperService.getValidatedUser(loginId);

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
