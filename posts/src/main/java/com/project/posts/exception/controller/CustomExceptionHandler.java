package com.project.posts.exception.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.project.posts.exception.CustomException;
import com.project.posts.exception.dto.ErrorDto;

@ControllerAdvice
public class CustomExceptionHandler {
	@ExceptionHandler(CustomException.class)
	protected ResponseEntity<ErrorDto> handleCustomException(final CustomException exception) {
		return ErrorDto.toResponseEntity(exception);
	}
}
