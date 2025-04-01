package com.project.posts.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.posts.data.Likes;
import com.project.posts.data.Posts;
import com.project.posts.data.Users;

@Repository
public interface LikesRepository extends JpaRepository<Likes, Long> {

	List<Likes> findAllByPosts(Posts post);

	Optional<Likes>  findByPostsAndUsers(Posts post, Users user);
}
