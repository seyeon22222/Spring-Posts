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
import com.project.posts.domain.posts.dto.request.PostsCreateReqDto;
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

	private final AuthUsersRepository authUsersRepository;
	private final AuthorityRepository authorityRepository;
	private final UsersRepository usersRepository;
	private final PostsRepository postsRepository;
	private final TagsRepository tagsRepository;
	private final ImagesRepository imagesRepository;
	private final EntityManager em;

	@Transactional
	public void createPost(String loginId, PostsCreateReqDto dto, String role) {

		AuthUsers authUser = authUsersRepository.findByLoginId(loginId)
			.orElseThrow(() -> new CustomException(CustomError.AUTH_USER_NOT_FOUND_ID));
		Users user = usersRepository.findByAuthUsers(authUser)
			.orElseThrow(() -> new CustomException(CustomError.USER_NOT_FOUND));

		List<Authority> roles = authorityRepository.findByAuthUsers(authUser);

		Role userRole = checkRole(role, roles);
		moveImagesToFinalStorage(dto.getContent());

		String contentWithImages = processImageUrls(dto.getContent());
		Posts post = PostsCreateReqDto.toEntity(user, dto.getTitle(), contentWithImages, userRole);
		postsRepository.save(post);
		em.flush();

		for (String tag : dto.getTags()) {
			Tags makeTag = Tags.builder().value(tag).posts(post).build();
			tagsRepository.save(makeTag);
		}
		em.flush();

		List<String> imageUrls = parseImageUrls(contentWithImages);
		for (String imageUrl : imageUrls) {
			Images image = Images.builder().imagesUrl(imageUrl).posts(post).build();
			imagesRepository.save(image);
		}
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

	private List<String> parseImageUrls(String content) {
		Pattern pattern = Pattern.compile("\\(http://localhost:8080/SpringPosts/images/([^\\s]+)\\)");
		Matcher matcher = pattern.matcher(content);

		List<String> imageUrls = new ArrayList<>();
		while (matcher.find()) {
			String imageName = matcher.group(1);
			String finalImageUrl = "http://localhost:8080/SpringPosts/images/" + imageName;  // 최종 URL로 수정
			imageUrls.add(finalImageUrl);
		}
		return imageUrls;
	}

	private String processImageUrls(String content) {
		return content.replace("http://localhost:8080/SpringPosts/images/temp/",
			"http://localhost:8080/SpringPosts/images/");
	}

	public Role checkRole(String role, List<Authority> roles) {
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

}
