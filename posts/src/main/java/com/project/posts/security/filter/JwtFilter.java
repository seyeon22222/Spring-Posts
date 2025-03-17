package com.project.posts.security.filter;

import java.io.IOException;
import java.util.Collection;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import com.project.posts.data.Authority;
import com.project.posts.data.RefreshToken;
import com.project.posts.repository.RefreshTokenRepository;
import com.project.posts.security.service.AuthService;
import com.project.posts.security.service.CustomUserDetailsService;
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
public class JwtFilter extends OncePerRequestFilter {

	private final CustomUserDetailsService customUserDetailsService;
	private final JwtUtil jwtUtil;
	private final AuthService authService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {

		String token = resolveToken(request);

		if (token != null) {
			try {
				if (jwtUtil.validateToken(token) && !authService.isRefreshTokenValid(token)) {
					setAuthentication(token);
				} else {
					response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Access token is invalid.");
					return;
				}
			} catch (ExpiredJwtException e) {
				handleExpiredAccessToken(token, response);
			}
		}
		filterChain.doFilter(request, response);
	}


	private String resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return null;
	}

	private void setAuthentication(String token) {
		String username = jwtUtil.getUsernameFromToken(token);
		UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
		UsernamePasswordAuthenticationToken authentication =
			new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	private void handleExpiredAccessToken(String token, HttpServletResponse response) throws IOException {
		String newAccessToken = authService.refreshAccessToken(token);

		if (newAccessToken != null) {
			response.setHeader("Authorization", "Bearer " + newAccessToken);
		} else {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Refresh token expired. Please login again.");
		}
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		String path = request.getServletPath();
		return path.equals("/login") || path.equals("/join") || path.equals("/logout");
	}
}
