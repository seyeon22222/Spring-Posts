package com.project.posts.domain.tags.service;

import java.util.*;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.posts.data.Posts;
import com.project.posts.data.Tags;
import com.project.posts.repository.TagsRepository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TagsService {

	private final TagsRepository tagsRepository;
	private final EntityManager em;

	@Transactional
	public void saveTags(List<String> tags, Posts post) {
		for (String tag : tags) {
			Tags makeTag = Tags.builder().value(tag).posts(post).build();
			tagsRepository.save(makeTag);
		}
	}

	@Transactional
	public void updateTags(List<String> tags, Posts post) {
		List<Tags> existingTags = tagsRepository.findAllByPosts(post);

		Map<String, Tags> tagMap = existingTags.stream()
			.collect(Collectors.toMap(Tags::getValue, tag -> tag));

		for (Tags tag : existingTags) {
			if (!tags.contains(tag.getValue())) {
				tagsRepository.delete(tag);
			}
		}

		for (String newTagValue : tags) {
			if (!tagMap.containsKey(newTagValue)) {
				Tags newTag = Tags.builder()
					.value(newTagValue)
					.posts(post)
					.build();
				tagsRepository.save(newTag);
			}
		}
		em.flush();
	}

	@Transactional
	public void deleteTags(Posts post) {
		List<Tags> tags = tagsRepository.findAllByPosts(post);
		tagsRepository.deleteAll(tags);
	}

	public List<String> getTags(Posts post) {
		return tagsRepository.findAllByPosts(post).stream()
			.map(Tags::getValue)
			.collect(Collectors.toList());
	}

}
