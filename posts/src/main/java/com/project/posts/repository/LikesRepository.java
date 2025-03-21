package com.project.posts.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.posts.data.Likes;
import com.project.posts.data.Posts;

@Repository
public interface LikesRepository extends JpaRepository<Likes, Long> {

	List<Likes> findAllByPosts(Posts post);
}
