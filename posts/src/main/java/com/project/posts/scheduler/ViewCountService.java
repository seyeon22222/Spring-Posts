package com.project.posts.scheduler;

import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.project.posts.repository.PostsRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ViewCountService {

	private final PostsRepository postsRepository;
	private final RedisTemplate<String, Object> redisTemplate;

	@Transactional
	public void syncViewCounts() {
		Set<String> keys = redisTemplate.keys("post:viewCount:*");

		if (keys == null)
			return;

		for (String key : keys) {
			Long postId = Long.parseLong(key.replace("post:viewCount:", ""));
			Integer views = (Integer)redisTemplate.opsForValue().get(key);

			if (views != null && views > 0) {
				postsRepository.increaseViewCount(postId, views);
				redisTemplate.delete(key); // 반영 후 초기화
			}
		}
	}
}

