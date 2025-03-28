package com.project.posts.domain.images.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.regex.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.project.posts.data.Images;
import com.project.posts.data.Posts;
import com.project.posts.exception.CustomError;
import com.project.posts.exception.CustomException;
import com.project.posts.repository.ImagesRepository;

import jakarta.persistence.EntityManager;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ImagesService {

	public final String TEMP_DIR = "/Users/seyeon/postimages/temp/upload";
	public final String FINAL_DIR = "/Users/seyeon/postimages/upload";

	private final ImagesRepository imagesRepository;
	private final EntityManager em;

	@Transactional
	public String uploadTempImage(MultipartFile file) {
		try {
			String originalFilename = file.getOriginalFilename();
			String extension = StringUtils.getFilenameExtension(originalFilename);
			String fileName = UUID.randomUUID() + "_images" + (extension != null ? "." + extension : "");

			Path filePath = Paths.get(TEMP_DIR, fileName);
			file.transferTo(filePath.toFile());

			return "(http://localhost:8080/SpringPosts/images/temp/" + fileName + ")";
		} catch (IOException e) {
			throw new RuntimeException("파일 업로드 실패", e);
		}
	}

	@Transactional
	public void saveImages(String contentWithImages, Posts post) {
		Map<String, String> imageUrlMap = parseImageUrls(contentWithImages);

		for (Map.Entry<String, String> entry : imageUrlMap.entrySet()) {
			String imageUrl = entry.getKey();
			Images image = Images.builder().imagesUrl(imageUrl).posts(post).build();
			imagesRepository.save(image);
		}
	}

	@Transactional
	public void updateImages(String contentWithImagesBefore, String contentWithImagesAfter, Posts post, Boolean status) {
		Map<String, String> imageUrlsBefore = parseImageUrls(contentWithImagesBefore);
		Map<String, String> imageUrlsAfter = parseImageUrls(contentWithImagesAfter);

		List<String> allImageUrls = new ArrayList<>();
		allImageUrls.addAll(imageUrlsBefore.keySet());
		allImageUrls.addAll(imageUrlsAfter.keySet());

		Set<String> uniqueImageUrls = new HashSet<>(allImageUrls);

		List<Images> existingImages = imagesRepository.findAllByImagesUrlIn(uniqueImageUrls);

		Map<String, Images> imageMap = existingImages.stream()
			.collect(Collectors.toMap(Images::getImagesUrl, image -> image));

		for (Map.Entry<String, String> entry : imageUrlsBefore.entrySet()) {
			String imageUrl = entry.getKey();
			String imageName = entry.getValue();

			if (!imageUrlsAfter.containsKey(imageUrl)) {
				Images image = imageMap.get(imageUrl);
				if (image != null) {
					imagesRepository.delete(image);

					File finalFile = new File(FINAL_DIR, imageName);
					if (finalFile.exists()) {
						try {
							Files.delete(finalFile.toPath());
						} catch (IOException e) {
							throw new CustomException(CustomError.FILE_DELETE_FAILED);
						}
					} else {
						throw new CustomException(CustomError.FILE_NOT_FOUND);
					}
				}
			}
		}

		List<Images> newImages = new ArrayList<>();

		for (String imageUrl : imageUrlsAfter.keySet()) {
			Images image = imageMap.get(imageUrl);

			if (image == null) {
				Images newImage = Images.builder()
					.imagesUrl(imageUrl)
					.posts(post)
					.build();
				newImage.updateStatus(status);
				newImages.add(newImage);
			} else {
				image.updateStatus(status);
			}
		}

		if (!newImages.isEmpty()) {
			imagesRepository.saveAll(newImages);
		}
	}

	@Transactional
	public void deleteImages(Posts post) {
		List<Images> images = imagesRepository.findAllByPosts(post);

		for (Images image : images) {
			image.delete();
		}
	}

	@Transactional
	public void deleteImage(String imageUrl) {
		Images image = imagesRepository.findByImagesUrl(imageUrl)
			.orElseThrow(() -> new CustomException(CustomError.IMAGE_NOT_FOUND));
		image.delete();
	}

	public String getImages(String imageUrl) {
		return imagesRepository.findByImagesUrl(imageUrl)
			.orElseThrow(() -> new CustomException(CustomError.IMAGE_NOT_FOUND))
			.getImagesUrl();
	}

	@Transactional
	public void moveImagesToFinalStorage(String content) {

		Pattern pattern = Pattern.compile("\\(http://localhost:8080/SpringPosts/images/temp/([^\\s]+)\\)");
		Matcher matcher = pattern.matcher(content);

		while (matcher.find()) {
			String tempImageUrl = matcher.group(1);
			String imageName = tempImageUrl.substring(tempImageUrl.lastIndexOf("/") + 1);

			File tempFile = new File(TEMP_DIR, imageName);
			File finalFile = new File(FINAL_DIR, imageName);

			try {
				Files.move(tempFile.toPath(), finalFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				throw new RuntimeException("Failed to move image file: " + imageName, e);
			}
		}
	}


	private Map<String, String> parseImageUrls(String content) {
		Pattern pattern = Pattern.compile("\\(http://localhost:8080/SpringPosts/images/([^\\s]+)\\)");
		Matcher matcher = pattern.matcher(content);

		Map<String, String> imageUrls = new HashMap<>();
		while (matcher.find()) {
			String tempImageUrl = matcher.group(1);
			String imageName = tempImageUrl.substring(tempImageUrl.lastIndexOf("/") + 1);
			String finalImageUrl = "http://localhost:8080/SpringPosts/images/" + imageName;  // 최종 URL로 수정
			imageUrls.put(finalImageUrl, imageName);
		}
		return imageUrls;
	}

	public void deleteExpiredTempImages() {
		File tempDir = new File(TEMP_DIR);
		File[] files = tempDir.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.lastModified() < System.currentTimeMillis() - 3600000) { // ✅ 1시간 이상된 파일 삭제
					file.delete();
				}
			}
		}
	}
}
