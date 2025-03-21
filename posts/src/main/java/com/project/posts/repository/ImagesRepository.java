package com.project.posts.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.project.posts.data.Images;
import com.project.posts.data.Posts;

@Repository
public interface ImagesRepository extends JpaRepository<Images, Long> {

	Optional<Images> findByImagesUrl(String imagesUrl);

	List<Images> findAllByImagesUrlIn(Set<String> imagesUrls);

	List<Images> findAllByPosts(Posts post);
}
