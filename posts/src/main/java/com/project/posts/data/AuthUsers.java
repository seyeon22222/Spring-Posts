package com.project.posts.data;

import com.project.posts.data.type.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthUsers extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, columnDefinition = "VARCHAR(50)")
	private String loginId;

	@Column(nullable = false, columnDefinition = "VARCHAR(60)")
	private String password;

	@Builder
	public AuthUsers(String loginId, String password) {
		this.loginId = loginId;
		this.password = password;
	}

	public Authority createAuthority(Role role) {
		return Authority.builder()
			.role(role)
			.authUsers(this)
			.build();
	}

	public Users createUsers(String username) {
		return Users.builder()
			.username(username)
			.authUsers(this)
			.build();
	}
}
