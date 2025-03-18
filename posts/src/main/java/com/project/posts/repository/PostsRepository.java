package com.project.posts.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.posts.data.Posts;

@Repository
public interface PostsRepository extends JpaRepository<Posts, Long> {
}
