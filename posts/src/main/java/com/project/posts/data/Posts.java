package com.project.posts.data;

import com.project.posts.data.type.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
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
public class Posts extends BaseTimeEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(columnDefinition = "VARCHAR(50)")
	private String title;

	@Column(columnDefinition = "VARCHAR(500)")
	private String content;

	private String author;

	private Boolean status;

	private Long views;

	private Long likes;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "users_id", nullable = false)
	private Users users;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, columnDefinition = "VARCHAR(20)")
	private Role role;

	@Builder
	public Posts(String title, String content, Users users, Role role) {
		this.title = title;
		this.content = content;
		this.author = users.getUsername();
		this.users = users;
		this.status = true;
		this.views = 0L;
		this.likes = 0L;
		this.role = role;
	}
}
