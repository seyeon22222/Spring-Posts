package com.project.posts.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.posts.data.Users;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {

}
