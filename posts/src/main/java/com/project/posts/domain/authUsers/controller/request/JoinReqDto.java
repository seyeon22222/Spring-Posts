package com.project.posts.domain.authUsers.controller.request;



import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JoinReqDto {

	@NotBlank
	@Size(max = 30)
	private String loginId;

	@NotBlank
	@Size(max = 30)
	private String password;


	@NotBlank
	@Size(max = 30)
	private String username;

}
