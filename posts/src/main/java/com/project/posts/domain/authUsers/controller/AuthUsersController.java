package com.project.posts.domain.authUsers.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.project.posts.domain.authUsers.controller.request.JoinReqDto;
import com.project.posts.domain.authUsers.controller.response.JoinResDto;
import com.project.posts.domain.authUsers.service.AuthUsersService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AuthUsersController {

	private final AuthUsersService authUsersService;

	@PostMapping("/join")
	public ResponseEntity<JoinResDto> join(@RequestBody JoinReqDto joinReqDto) {
		JoinResDto joinResDto = authUsersService.createUsers(joinReqDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(joinResDto);
	}
}
