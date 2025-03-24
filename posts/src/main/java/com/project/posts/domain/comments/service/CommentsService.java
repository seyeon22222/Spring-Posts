package com.project.posts.domain.comments.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.project.posts.data.Comments;
import com.project.posts.data.Posts;
import com.project.posts.data.Users;
import com.project.posts.domain.comments.controller.request.CommentsCreateReqDto;
import com.project.posts.domain.comments.controller.response.CommentsResponseDto;
import com.project.posts.domain.helper.service.AuthValidationHelperService;
import com.project.posts.repository.CommentsRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentsService {

	private final CommentsRepository commentsRepository;
	private final AuthValidationHelperService authValidationHelperService;

	@Transactional
	public void deleteComments(Posts post) {
		List<Comments> commentsList = commentsRepository.findAllByPosts(post);
		for (Comments comment : commentsList) {
			comment.delete();
		}
	}

	@Transactional
	public void createComment(String loginId, CommentsCreateReqDto commentsCreateReqDto, String role, Posts post) {
		Users user = authValidationHelperService.getValidatedUser(loginId, role);
		Comments comments;
		if(commentsCreateReqDto.getLevel().equals(1L)){ //대댓글이라면
			Long indexing = commentsRepository.findMaxCommentOrder(post, commentsCreateReqDto.getAffiliation());
			comments = Comments.builder()
				.content(commentsCreateReqDto.getContent())
				.level(commentsCreateReqDto.getLevel())
				.affiliation(commentsCreateReqDto.getAffiliation())
				.indexing(indexing+1)
				.posts(post)
				.users(user)
				.build();
		} else{ //댓글이라면
			comments = Comments.builder()
				.content(commentsCreateReqDto.getContent())
				.level(commentsCreateReqDto.getLevel())
				.affiliation(commentsCreateReqDto.getAffiliation())
				.indexing(commentsCreateReqDto.getIndexing())
				.posts(post)
				.users(user)
				.build();
		}
		commentsRepository.save(comments);
	}

	public Page<CommentsResponseDto> getFirstPageComments(Posts post, Pageable pageable) {
		Page<Comments> commentsList = commentsRepository.findAllByPostsPage(post, pageable);
		List<CommentsResponseDto> commentsResponseDtoList = commentsList.stream()
			.map(CommentsResponseDto::new)
			.collect(Collectors.toList());
		return new PageImpl<>(commentsResponseDtoList, PageRequest.of(0, 10), commentsResponseDtoList.size());
	}
}
