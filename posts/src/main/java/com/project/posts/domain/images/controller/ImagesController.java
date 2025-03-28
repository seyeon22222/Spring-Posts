package com.project.posts.domain.images.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.project.posts.domain.images.service.ImagesService;
import com.project.posts.security.data.CustomUserDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/images")
public class ImagesController {

	private final ImagesService imagesService;

	@PostMapping("/upload")
	public ResponseEntity<String> uploadImage(@AuthenticationPrincipal CustomUserDetails customUserDetails,
		@Valid @RequestParam("file") MultipartFile file) {
		String imageUrl = imagesService.uploadTempImage(file);
		return ResponseEntity.ok(imageUrl);
	}

	@GetMapping
	public ResponseEntity<String> getImages(@AuthenticationPrincipal CustomUserDetails customUserDetails,
		@ModelAttribute @Valid String imageUrl) {
		String responseImageUrl = imagesService.getImages(imageUrl);
		return ResponseEntity.ok(responseImageUrl);
	}

	@PutMapping
	public ResponseEntity<Void> updateImages(@AuthenticationPrincipal CustomUserDetails customUserDetails,
		@ModelAttribute @Valid String imageUrl) {
		imagesService.deleteImage(imageUrl);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@PatchMapping
	public ResponseEntity<Void> deleteImages(@AuthenticationPrincipal CustomUserDetails customUserDetails,
		@ModelAttribute @Valid String imageUrl) {
		imagesService.deleteImage(imageUrl);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}
