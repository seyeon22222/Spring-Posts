package com.project.posts.domain.comments.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.project.posts.data.Comments;
import com.project.posts.data.Posts;
import com.project.posts.data.Users;
import com.project.posts.data.type.Role;
import com.project.posts.domain.comments.controller.request.CommentsCreateReqDto;
import com.project.posts.domain.comments.controller.request.CommentsUpdateReqDto;
import com.project.posts.domain.comments.controller.response.CommentsResponseDto;
import com.project.posts.domain.helper.service.AuthValidationHelperService;
import com.project.posts.exception.CustomError;
import com.project.posts.exception.CustomException;
import com.project.posts.repository.CommentsRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentsService {

	private final CommentsRepository commentsRepository;
	private final AuthValidationHelperService authValidationHelperService;

	@Transactional
	public void deleteAllComments(Posts post) {
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
			Long indexing = commentsRepository.findMaxCommentindexing(post, commentsCreateReqDto.getAffiliation());
			comments = Comments.builder()
				.content(commentsCreateReqDto.getContent())
				.level(commentsCreateReqDto.getLevel())
				.affiliation(commentsCreateReqDto.getAffiliation())
				.indexing(indexing+1)
				.posts(post)
				.users(user)
				.build();
		} else{ //댓글이라면
			Long affiliation = commentsRepository.findMaxCommentAffiliation(post).orElse(0L);
			comments = Comments.builder()
				.content(commentsCreateReqDto.getContent())
				.level(commentsCreateReqDto.getLevel())
				.affiliation(affiliation + 1)
				.indexing(0L)
				.posts(post)
				.users(user)
				.build();
		}
		commentsRepository.save(comments);
	}

	public Page<CommentsResponseDto> getFirstPageComments(Posts post, Pageable pageable) {
		Page<Comments> commentsList = commentsRepository.findAllByPostsPage(post, pageable);
		List<CommentsResponseDto> commentsResponseDtoList = commentsList.getContent().stream()
			.map(CommentsResponseDto::new)
			.collect(Collectors.toList());

		return new PageImpl<>(commentsResponseDtoList, pageable, commentsList.getTotalElements());
	}


	public Page<CommentsResponseDto> getCommentsByPage(String loginId, Posts post, Pageable pageable) {
		String role = post.getRole().toString();
		authValidationHelperService.getValidatedUser(loginId, role);
		Page<Comments> commentsList = commentsRepository.findAllByPostsPage(post, pageable);
		List<CommentsResponseDto> commentsResponseDtoList = commentsList.getContent().stream()
			.map(CommentsResponseDto::new)
			.collect(Collectors.toList());

		return new PageImpl<>(commentsResponseDtoList, pageable, commentsList.getTotalElements());
	}

	@Transactional
	public CommentsResponseDto updateComments(String loginId, Long commentsId, CommentsUpdateReqDto commentsUpdateReqDto) {
		Users user = authValidationHelperService.getSimpleUser(loginId);
		Comments comments = commentsRepository.findById(commentsId)
			.orElseThrow(() -> new CustomException(CustomError.COMMENT_NOT_FOUND));
		validComments(comments, user);
		comments.update(commentsUpdateReqDto.getContent(), commentsUpdateReqDto.getStatus());
		return new CommentsResponseDto(comments);
	}

	@Transactional
	public void deleteComments(String loginId, Long commentsId) {
		Users user = authValidationHelperService.getSimpleUser(loginId);
		Comments comments = commentsRepository.findById(commentsId)
			.orElseThrow(() -> new CustomException(CustomError.COMMENT_NOT_FOUND));
		if (comments.getLevel().equals(1L)) { // 대댓글 삭제 -> 해당 대댓글만 지움
			validComments(comments, user);
			comments.delete();
		} else { // 댓글일 경우 댓글에 관련된 대댓글 모두 삭제
			List<Comments> commentsChlidList = commentsRepository.getRelateComments(comments.getAffiliation());
			for (Comments commentsChild : commentsChlidList) {
				commentsChild.delete();
			}
		}
		comments.delete();
	}


	private void validComments(Comments comments, Users user) {
		if (!comments.getUsers().equals(user)) {
			throw new CustomException(CustomError.USER_NOT_MATCH);
		}
	}
}
