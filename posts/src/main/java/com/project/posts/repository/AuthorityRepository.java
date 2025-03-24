package com.project.posts.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.posts.data.AuthUsers;
import com.project.posts.data.Authority;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, Long> {

	List<Authority> findAllByAuthUsers(AuthUsers authUsers);

	@Query("SELECT a FROM Authority a WHERE a.authUsers = :authUser AND a.role != 'USER'")
	Authority getCurrentAuthority(@Param("authUser") AuthUsers authUser);
}
