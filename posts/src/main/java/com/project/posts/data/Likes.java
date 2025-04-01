package com.project.posts.data;

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
public class Likes {

	@Id
	@GeneratedValue(strategy =  GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "posts_id", nullable = false)
	private Posts posts;

	@ManyToOne
	@JoinColumn(name = "users_id", nullable = false)
	private Users users;


	@Builder
	public Likes(Posts posts, Users users) {
		this.posts = posts;
		this.users = users;
	}
}
