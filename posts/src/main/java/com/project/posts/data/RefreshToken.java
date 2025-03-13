package com.project.posts.data;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class RefreshToken {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true)
	private String token;

	@ManyToOne
	@JoinColumn(name = "auth_user_id", nullable = false)
	private AuthUsers user;

	private LocalDateTime expiryDate;

	@Builder
	public RefreshToken(String token, AuthUsers user, LocalDateTime expiryDate) {
		this.token = token;
		this.user = user;
		this.expiryDate = expiryDate;
	}
}

