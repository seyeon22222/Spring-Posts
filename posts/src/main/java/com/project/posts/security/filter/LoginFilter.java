package com.project.posts.security.filter;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.posts.security.data.CustomUserDetails;
import com.project.posts.security.data.LoginReqDto;
import com.project.posts.security.service.AuthService;
import com.project.posts.security.util.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

	private final AuthenticationManager authenticationManager;
	private final JwtUtil jwtUtil;
	private final AuthService authService;

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws
		AuthenticationException {

		String username;
		String password;

		if (request.getContentType().equals("application/json")) {
			try {
				LoginReqDto loginRequest = new ObjectMapper().readValue(request.getInputStream(), LoginReqDto.class);
				username = loginRequest.getLoginId();
				password = loginRequest.getPassword();
			} catch (IOException e) {
				throw new RuntimeException("Failed to parse login request", e);
			}
		} else {
			username = obtainUsername(request);
			password = obtainPassword(request);
		}
		System.out.println("username = " + username);
		System.out.println("password = " + password);
		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);
		return authenticationManager.authenticate(authToken);
	}

	@Override
	@Transactional
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
		Authentication authentication) throws IOException, ServletException {

		CustomUserDetails customUserDetails = (CustomUserDetails)authentication.getPrincipal();
		String username = customUserDetails.getUsername();

		authService.checkRefreshToken(username);
		String token = jwtUtil.generateToken(username);

		response.addHeader("Authorization", "Bearer " + token);

	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException failed) throws IOException, ServletException {
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
	}

}
