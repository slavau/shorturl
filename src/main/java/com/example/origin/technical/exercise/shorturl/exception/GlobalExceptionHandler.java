package com.example.origin.technical.exercise.shorturl.exception;

import com.example.origin.technical.exercise.shorturl.model.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Global exception handler for the URL shortener application.
 * <p>
 * Handles validation errors, illegal arguments, and general exceptions,
 * returning consistent {@link ErrorResponse} objects.
 * </p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
		List<String> details = ex.getBindingResult()
			.getFieldErrors()
			.stream()
			.map(error -> error.getField() + ": " + error.getDefaultMessage())
			.collect(Collectors.toList());

		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.setError("Bad Request");
		errorResponse.setMessage("Validation failed");
		errorResponse.setDetails(details);
		errorResponse.setTimestamp(LocalDateTime.now());

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorResponse> handleValidationException(ConstraintViolationException ex) {
		List<String> details = ex.getConstraintViolations()
			.stream()
			.map(error -> error.getPropertyPath() +" : " + error.getMessage())
			.collect(Collectors.toList());

		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.setError("Bad Request");
		errorResponse.setMessage("Validation failed");
		errorResponse.setDetails(details);
		errorResponse.setTimestamp(LocalDateTime.now());

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.setError("Bad Request");
		errorResponse.setMessage(ex.getMessage());
		errorResponse.setTimestamp(LocalDateTime.now());

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.setError("Internal Server Error");
		errorResponse.setMessage(ex.getMessage());
		errorResponse.setTimestamp(LocalDateTime.now());

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
	}
}
