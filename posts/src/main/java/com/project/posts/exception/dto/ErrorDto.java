package com.project.posts.exception.dto;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.project.posts.exception.CustomException;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorDto {
	private final String message;

	public static ResponseEntity<ErrorDto> toResponseEntity(final CustomException exception) {
		return ResponseEntity.status(exception.getError().getStatus())
			.body(new ErrorDto(exception.getError().getMessage()));
	}
}
