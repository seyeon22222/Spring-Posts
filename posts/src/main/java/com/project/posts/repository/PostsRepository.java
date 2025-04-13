package com.project.posts.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.posts.data.Posts;

@Repository
public interface PostsRepository extends JpaRepository<Posts, Long> {

	@Modifying
	@Query("UPDATE Posts p SET p.views = p.views + :views WHERE p.id = :postId")
	void increaseViewCount(@Param("postId") Long postId, @Param("views") int views);
}
