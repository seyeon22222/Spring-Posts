package com.project.posts.domain.posts.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.posts.domain.posts.dto.request.PostsCreateReqDto;
import com.project.posts.domain.posts.dto.request.PostsUpdateReqDto;
import com.project.posts.domain.posts.dto.response.PostsResponseDto;
import com.project.posts.domain.posts.service.PostsService;
import com.project.posts.security.data.CustomUserDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
@Slf4j
public class PostsController {

	private final PostsService postsService;

	@PostMapping("/{role}")
	public ResponseEntity<Void> createPost(@AuthenticationPrincipal CustomUserDetails customUserDetails,
		@RequestBody @Valid PostsCreateReqDto postsCreateReqDto, @PathVariable("role") String role) {
		String loginId = customUserDetails.getUsername();
		postsService.createPost(loginId, postsCreateReqDto, role);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PutMapping("/{id}")
	public ResponseEntity<PostsResponseDto> updatePost(@AuthenticationPrincipal CustomUserDetails customUserDetails,
		@RequestBody @Valid PostsUpdateReqDto postsUpdateReqDto, @PathVariable Long id) {
		String loginId = customUserDetails.getUsername();
		PostsResponseDto response = postsService.updatePosts(loginId, postsUpdateReqDto, id);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deletePost(@AuthenticationPrincipal CustomUserDetails customUserDetails,
		@PathVariable Long id) {
		String loginId = customUserDetails.getUsername();
		postsService.deletePosts(loginId, id);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}
