package com.project.posts.domain.likes.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.posts.data.Posts;
import com.project.posts.domain.helper.service.AnotherHelperService;
import com.project.posts.domain.likes.service.LikesService;
import com.project.posts.security.data.CustomUserDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping
public class LikesController {

	private final LikesService likesService;
	private final AnotherHelperService anotherHelperService;


	@PutMapping("/{role}")
	public ResponseEntity<Boolean> createOrCancelLikes(@AuthenticationPrincipal CustomUserDetails customUserDetails,
		@PathVariable("role") String role, @Valid @RequestParam("postId") Long postId){
		String loginId = customUserDetails.getUsername();
		Posts post = anotherHelperService.getSimplePost(postId);
		Boolean status = likesService.createOrCancelLikes(loginId, post, role);
		return ResponseEntity.status(HttpStatus.OK).body(status);
	}
}
