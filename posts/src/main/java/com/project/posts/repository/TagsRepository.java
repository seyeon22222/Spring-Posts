package com.project.posts.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.posts.data.Tags;

@Repository
public interface TagsRepository extends JpaRepository<Tags, Long> {
}
