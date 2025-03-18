package com.project.posts.domain.authUsers.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.project.posts.domain.authUsers.controller.request.JoinReqDto;
import com.project.posts.domain.authUsers.controller.response.JoinResDto;
import com.project.posts.domain.authUsers.service.AuthUsersService;
import com.project.posts.security.data.CustomUserDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthUsersController {

	private final AuthUsersService authUsersService;

	@PostMapping("/join")
	public ResponseEntity<JoinResDto> join(@RequestBody JoinReqDto joinReqDto) {
		JoinResDto joinResDto = authUsersService.createUsers(joinReqDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(joinResDto);
	}

	@GetMapping("/test")
	public String test(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
		System.out.println("customUserDetails.getUsername() = " + customUserDetails.getUsername());
		System.out.println("customUserDetails.getAuthorities() = " + customUserDetails.getAuthorities());
		System.out.println("customUserDetails.getPassword() = " + customUserDetails.getPassword());
		return "test";
	}

}
