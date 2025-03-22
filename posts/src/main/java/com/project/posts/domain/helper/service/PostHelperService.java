package com.project.posts.domain.helper.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.posts.data.Posts;
import com.project.posts.domain.comments.service.CommentsService;
import com.project.posts.domain.images.service.ImagesService;
import com.project.posts.domain.likes.service.LikesService;
import com.project.posts.domain.tags.service.TagsService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostHelperService {

	private final TagsService tagsService;
	private final ImagesService imagesService;
	private final CommentsService commentsService;
	private final LikesService likesService;


	public void moveImagesFile(String content) {
		imagesService.moveImagesToFinalStorage(content);
	}

	public void saveTags(List<String> tags, Posts post) {
		tagsService.saveTags(tags, post);
	}

	public void saveImages(String contentWithImages, Posts post) {
		imagesService.saveImages(contentWithImages, post); // 이미지 저장
	}

	public void updateImages(Posts post, String contentWithImages, Boolean status) {
		imagesService.updateImages(post.getContent(), contentWithImages, post, status);
	}

	public void updateTags(Posts post, List<String> tags) {
		tagsService.updateTags(tags, post);
	}

	@Transactional
	public void deleteCascade(Posts post) {
		imagesService.deleteImages(post);
		tagsService.deleteTags(post);
		commentsService.deleteComments(post);
		likesService.deleteLikes(post);
	}

}
