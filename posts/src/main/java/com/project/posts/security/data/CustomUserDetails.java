package com.project.posts.security.data;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.project.posts.data.AuthUsers;
import com.project.posts.data.Authority;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

	private final AuthUsers authUsers;
	private final List<Authority> authorities;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities.stream()
			.map(auth -> new SimpleGrantedAuthority("ROLE_" + auth.getRole().name()))
			.collect(Collectors.toList());
	}

	@Override
	public String getPassword() {
		return authUsers.getPassword();
	}

	@Override
	public String getUsername() {
		return authUsers.getLoginId();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}


}
