package com.project.posts.domain.images.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.project.posts.data.Images;
import com.project.posts.data.Posts;
import com.project.posts.repository.ImagesRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ImagesService {

	public static final String TEMP_DIR = "/Users/seyeon/postimages/temp/upload";
	public static final String FINAL_DIR = "/Users/seyeon/postimages/upload";

	private final ImagesRepository imagesRepository;

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
		List<String> imageUrls = parseImageUrls(contentWithImages);
		for (String imageUrl : imageUrls) {
			Images image = Images.builder().imagesUrl(imageUrl).posts(post).build();
			imagesRepository.save(image);
		}
	}

	private List<String> parseImageUrls(String content) {
		Pattern pattern = Pattern.compile("\\(http://localhost:8080/SpringPosts/images/([^\\s]+)\\)");
		Matcher matcher = pattern.matcher(content);

		List<String> imageUrls = new ArrayList<>();
		while (matcher.find()) {
			String imageName = matcher.group(1);
			String finalImageUrl = "http://localhost:8080/SpringPosts/images/" + imageName;  // 최종 URL로 수정
			imageUrls.add(finalImageUrl);
		}
		return imageUrls;
	}

	public void moveToFinalStorage(String fileName) {
		try {
			Path tempFilePath = Paths.get(TEMP_DIR, fileName);
			Path finalFilePath = Paths.get(FINAL_DIR, fileName);

			// ✅ 정식 저장소로 이동
			Files.move(tempFilePath, finalFilePath, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new RuntimeException("파일 이동 실패", e);
		}
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
