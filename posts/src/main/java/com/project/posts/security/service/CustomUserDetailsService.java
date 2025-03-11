package com.project.posts.security.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.project.posts.data.AuthUsers;
import com.project.posts.data.Authority;
import com.project.posts.repository.AuthUsersRepository;
import com.project.posts.repository.AuthorityRepository;
import com.project.posts.security.data.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private final AuthUsersRepository authUsersRepository;
	private final AuthorityRepository authorityRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		AuthUsers authUser = authUsersRepository.findByLoginId(username)
			.orElseThrow(() -> new UsernameNotFoundException("User not found"));

		List<Authority> authorities = authorityRepository.findByAuthUsers(authUser);

		return new CustomUserDetails(authUser, authorities);
	}
}
