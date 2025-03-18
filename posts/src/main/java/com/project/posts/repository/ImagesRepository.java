package com.project.posts.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.project.posts.data.Images;

@Repository
public interface ImagesRepository extends JpaRepository<Images, Long> {
}
