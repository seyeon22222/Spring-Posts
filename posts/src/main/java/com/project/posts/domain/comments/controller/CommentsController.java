package com.project.posts.domain.comments.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.posts.data.Posts;
import com.project.posts.domain.comments.controller.request.CommentsCreateReqDto;
import com.project.posts.domain.comments.controller.request.CommentsGetReqDto;
import com.project.posts.domain.comments.controller.response.CommentsResponseDto;
import com.project.posts.domain.comments.service.CommentsService;
import com.project.posts.domain.helper.service.CommentHelperService;
import com.project.posts.domain.helper.service.PostHelperService;
import com.project.posts.security.data.CustomUserDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentsController {

	private final CommentsService commentsService;
	private final CommentHelperService commentHelperService;

	@PostMapping("/{role}")
	public ResponseEntity<Void> createComment(@AuthenticationPrincipal CustomUserDetails customUserDetails,
		@PathVariable("role") String role, @Valid @RequestBody CommentsCreateReqDto commentsCreateReqDto) {
		String loginId = customUserDetails.getUsername();
		commentsService.createComment(loginId, commentsCreateReqDto, role, commentHelperService.getSimplePost(commentsCreateReqDto.getPostId()));
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@GetMapping
	public ResponseEntity<Page<CommentsResponseDto>> getCommentsByPage(@AuthenticationPrincipal CustomUserDetails customUserDetails,
		@ModelAttribute @Valid CommentsGetReqDto commentsGetReqDto) {
		String loginId = customUserDetails.getUsername();
		Posts post = commentHelperService.getSimplePost(commentsGetReqDto.getPostId());
		Pageable pageable = PageRequest.of(commentsGetReqDto.getPage(), commentsGetReqDto.getSize());
		Page<CommentsResponseDto> response = commentsService.getCommentsByPage(loginId, post, pageable);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
}
