package com.project.posts.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ViewCountScheduler {

	private final ViewCountService viewCountService;

	@Scheduled(fixedRate = 60000)
	public void updateViewCounts() {
		viewCountService.syncViewCounts();
		log.info("View counts updated.");
	}
}