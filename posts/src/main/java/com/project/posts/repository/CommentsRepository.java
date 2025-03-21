package com.project.posts.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.posts.data.Comments;
import com.project.posts.data.Posts;

@Repository
public interface CommentsRepository extends JpaRepository<Comments, Long> {

	List<Comments> findAllByPosts(Posts post);
}
