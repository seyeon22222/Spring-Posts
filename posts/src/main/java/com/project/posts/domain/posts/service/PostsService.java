package com.project.posts.domain.posts.service;

import static com.project.posts.domain.images.service.ImagesService.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.posts.data.AuthUsers;
import com.project.posts.data.Authority;
import com.project.posts.data.Images;
import com.project.posts.data.Posts;
import com.project.posts.data.Tags;
import com.project.posts.data.Users;
import com.project.posts.data.type.Role;
import com.project.posts.domain.authUsers.service.AuthUsersService;
import com.project.posts.domain.authority.service.AuthorityService;
import com.project.posts.domain.comments.service.CommentsService;
import com.project.posts.domain.images.service.ImagesService;
import com.project.posts.domain.likes.service.LikesService;
import com.project.posts.domain.posts.dto.request.PostsCreateReqDto;
import com.project.posts.domain.posts.dto.request.PostsUpdateReqDto;
import com.project.posts.domain.posts.dto.response.PostsResponseDto;
import com.project.posts.domain.tags.service.TagsService;
import com.project.posts.domain.users.service.UsersService;
import com.project.posts.exception.CustomError;
import com.project.posts.exception.CustomException;
import com.project.posts.repository.AuthUsersRepository;
import com.project.posts.repository.AuthorityRepository;
import com.project.posts.repository.ImagesRepository;
import com.project.posts.repository.PostsRepository;
import com.project.posts.repository.TagsRepository;
import com.project.posts.repository.UsersRepository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostsService {

	private final PostsRepository postsRepository;
	private final AuthUsersService authUsersService;
	private final AuthorityService authorityService;
	private final UsersService usersService;
	private final TagsService tagsService;
	private final ImagesService imagesService;
	private final CommentsService commentsService;
	private final LikesService likesService;
	private final EntityManager em;


	@Transactional
	public void createPost(String loginId, PostsCreateReqDto dto, String role) {
		AuthUsers authUser = authUsersService.getAuthUser(loginId);
		Users user = usersService.getAuthUser(authUser);

		moveImagesToFinalStorage(dto.getContent());

		String contentWithImages = processImageUrls(dto.getContent());
		Posts post = PostsCreateReqDto.toEntity(user, dto.getTitle(), contentWithImages,
			checkRole(role, authorityService.getAuthorities(authUser)));
		postsRepository.save(post);
		em.flush();

		tagsService.saveTags(dto.getTags(), post);
		imagesService.saveImages(contentWithImages, post); // 이미지 저장
	}

	@Transactional
	public void moveImagesToFinalStorage(String content) {

		Pattern pattern = Pattern.compile("\\(http://localhost:8080/SpringPosts/images/temp/([^\\s]+)\\)");
		Matcher matcher = pattern.matcher(content);

		while (matcher.find()) {
			String tempImageUrl = matcher.group(1);
			String imageName = tempImageUrl.substring(tempImageUrl.lastIndexOf("/") + 1);

			File tempFile = new File(TEMP_DIR, imageName);
			File finalFile = new File(FINAL_DIR, imageName);

			try {
				Files.move(tempFile.toPath(), finalFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				throw new RuntimeException("Failed to move image file: " + imageName, e);
			}
		}
	}

	@Transactional
	public PostsResponseDto updatePosts(String loginId, PostsUpdateReqDto dto, Long postId) {
		Posts post = getPostWithAuthorValidation(loginId, postId);

		moveImagesToFinalStorage(dto.getContent());
		String contentWithImages = processImageUrls(dto.getContent());
		imagesService.updateImages(post.getContent(), contentWithImages, post, dto.getStatus());
		post.update(dto.getTitle(), contentWithImages, dto.getStatus());
		em.flush();
		List<Tags> tags = tagsService.updateTags(dto.getTags(), post);

		return PostsResponseDto.toDto(post, tags);
	}

	@Transactional
	public void deletePosts(String loginId, Long postId) {
		Posts post = getPostWithAuthorValidation(loginId, postId);

		imagesService.deleteImages(post);
		tagsService.deleteTags(post);
		commentsService.deleteComments(post);
		likesService.deleteLikes(post);
		post.delete();
		postsRepository.save(post);
	}

	private Posts getPostWithAuthorValidation(String loginId, Long postId) {
		AuthUsers authUser = authUsersService.getAuthUser(loginId);
		Users user = usersService.getAuthUser(authUser);

		Posts post = postsRepository.findById(postId)
			.orElseThrow(() -> new CustomException(CustomError.POST_NOT_FOUND));

		checkAuthor(user, post);

		return post;
	}

	private String processImageUrls(String content) {
		return content.replace("http://localhost:8080/SpringPosts/images/temp/",
			"http://localhost:8080/SpringPosts/images/");
	}

	private Role checkRole(String role, List<Authority> roles) {
		if (!Role.contains(role))
			throw new CustomException(CustomError.INVALID_ROLE);

		Role targetRole = Role.valueOf(role.toUpperCase());

		for (Authority a : roles) {
			Role userRole = a.getRole();
			if (userRole == targetRole)
				return targetRole;
			if (userRole.canAccess(targetRole))
				return targetRole;
		}
		throw new CustomException(CustomError.NOT_HAVE_ACCESS);
	}

	private void checkAuthor(Users user, Posts post) {
		if (!post.getAuthor().equals(user.getUsername()))
			throw new CustomException(CustomError.USER_NOT_MATCH);
	}
}
