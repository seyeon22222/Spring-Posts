package com.project.posts.security.filter;

import java.io.IOException;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.project.posts.security.service.AuthService;
import com.project.posts.security.util.JwtUtil;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class LogoutFilter extends OncePerRequestFilter {

	private final AuthService authService;
	private final JwtUtil jwtUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {

		String path = request.getServletPath();
		if (path.equals("/logout") && request.getMethod().equals("POST")) {
			String token = resolveToken(request);
			if (token != null) {
				String username = jwtUtil.getUsernameFromToken(token);
				authService.logout(username);
				log.info("User {} logged out successfully.", username);

				SecurityContextHolder.clearContext();
			}
			response.setStatus(HttpServletResponse.SC_NO_CONTENT);
			return;
		}
		filterChain.doFilter(request, response);
	}

	private String resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		} else if (bearerToken != null) {
			return bearerToken;
		}
		return null;
	}

}
