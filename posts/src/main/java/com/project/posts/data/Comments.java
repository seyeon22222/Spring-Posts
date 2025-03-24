package com.project.posts.data;

import jakarta.persistence.Entity;
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
public class Comments extends BaseTimeEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "users_id", nullable = false)
	private Users users;

	private String content;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "posts_id", nullable = false)
	private Posts posts;

	private Long level;

	private Long indexing;

	private Long affiliation;

	private Boolean status;

	@Builder
	public Comments(Users users, String content, Posts posts, Long level, Long indexing, Long affiliation) {
		this.users = users;
		this.content = content;
		this.posts = posts;
		this.level = level;
		this.indexing = indexing;
		this.affiliation = affiliation;
		this.status = true;
	}

	public void delete() {
		this.status = false;
	}

}
