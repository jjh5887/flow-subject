package me.powerarc.flowsubject.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import me.powerarc.flowsubject.exception.response.ErrorResponse;

@RestControllerAdvice("me.powerarc.flowsubject")
public class ExceptionController {

	@ExceptionHandler
	public ResponseEntity errorHandle(ExtensionException exception) {
		return ResponseEntity
			.status(exception.getStatus())
			.body(ErrorResponse.builder().status(exception.getStatus())
				.message(exception.getMessage()).build());
	}
}
