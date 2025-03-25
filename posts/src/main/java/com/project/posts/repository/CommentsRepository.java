package com.project.posts.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.posts.data.Comments;
import com.project.posts.data.Posts;

@Repository
public interface CommentsRepository extends JpaRepository<Comments, Long> {

	List<Comments> findAllByPosts(Posts post);

	@Query("SELECT c FROM Comments c WHERE c.posts = :post ORDER BY c.affiliation, c.indexing")
	List<Comments> findAllComments(@Param("post") Posts post);

	@Query("SELECT MAX(c.indexing) FROM Comments c WHERE c.posts = :post AND c.affiliation = :affiliation")
	Long findMaxCommentindexing(@Param("post") Posts post, @Param("affiliation") Long affiliation);

	@Query("SELECT MAX(c.affiliation) FROM Comments c WHERE c.posts = :post")
	Optional<Long> findMaxCommentAffiliation(@Param("post") Posts post);

	@Query("SELECT c FROM Comments c WHERE c.posts = :post And c.status = true ORDER BY c.affiliation, c.indexing")
	Page<Comments> findAllByPostsPage(@Param("post")Posts post, Pageable pageable);

	@Query("SELECT c FROM Comments c WHERE c.affiliation = :affiliation")
	List<Comments> getRelateComments(Long affiliation);
}
