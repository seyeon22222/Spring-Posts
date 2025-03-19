package com.project.posts.domain.tags.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.posts.data.Posts;
import com.project.posts.data.Tags;
import com.project.posts.repository.TagsRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TagsService {

	private final TagsRepository tagsRepository;

	@Transactional
	public void saveTags(List<String> tags, Posts post) {
		for (String tag : tags) {
			Tags makeTag = Tags.builder().value(tag).posts(post).build();
			tagsRepository.save(makeTag);
		}
	}


}
