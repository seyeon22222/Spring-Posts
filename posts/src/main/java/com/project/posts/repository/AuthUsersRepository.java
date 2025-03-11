package com.project.posts.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.posts.data.AuthUsers;

@Repository
public interface AuthUsersRepository extends JpaRepository<AuthUsers, Long> {

	Optional<AuthUsers> findByLoginId(String loginId);
}
