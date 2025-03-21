package com.project.posts.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.posts.data.Posts;
import com.project.posts.data.Tags;

@Repository
public interface TagsRepository extends JpaRepository<Tags, Long> {

	List<Tags> findAllByPosts(Posts post);

	Optional<Tags> findByValue(String value);
}
