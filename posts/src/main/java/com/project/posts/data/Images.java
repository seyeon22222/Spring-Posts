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

@Getter
@Entity
@NoArgsConstructor
public class Images extends BaseTimeEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String imagesUrl;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "posts_id", nullable = false)
	private Posts posts;

	private Boolean status;

	@Builder
	public Images(String imagesUrl, Posts posts) {
		this.imagesUrl = imagesUrl;
		this.posts = posts;
		this.status = true;
	}

	public void updateStatus(Boolean status) {
		this.status = status;
	}

	public void delete() {
		this.status = false;
	}


}
