package com.project.posts.domain.authority.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.posts.data.AuthUsers;
import com.project.posts.data.Authority;
import com.project.posts.data.type.Role;
import com.project.posts.repository.AuthorityRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthorityService {

	private final AuthorityRepository authorityRepository;

	public List<Authority> getAuthorities(AuthUsers authUser) {
		return authorityRepository.findAllByAuthUsers(authUser);
	}

	public Authority getAuthority(AuthUsers authUser) {
		return authorityRepository.getCurrentAuthority(authUser);
	}
}
