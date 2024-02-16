package cards.alice.monolith.common.web.controllers;

import cards.alice.monolith.common.web.exceptions.DtoProcessingException;
import cards.alice.monolith.common.web.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.net.ConnectException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@ControllerAdvice
public class CommonErrorHandler {
    @ExceptionHandler(NoSuchElementException.class)
    ResponseEntity handleResourceNotFound(NoSuchElementException e) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    ResponseEntity handleResourceNotFound(ResourceNotFoundException e) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(EntityNotFoundException.class)
    ResponseEntity handleResourceNotFound() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity handleBindErrors(MethodArgumentNotValidException e) {
        List<Map<String, String>> errorList = e.getFieldErrors().stream()
                .map(fieldError -> {
                    Map<String, String> errorMap = new HashMap<>();
                    errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
                    return errorMap;
                }).collect(Collectors.toList());
        return ResponseEntity.badRequest().body(errorList);
    }

    @ExceptionHandler(ConnectException.class)
    ResponseEntity handleConnectionRefused(ConnectException e) {
        return ResponseEntity.internalServerError().build();
    }

/*
    @ExceptionHandler(TransactionSystemException.class)
    ResponseEntity handleJpaViolations(TransactionSystemException e) {
        ResponseEntity.BodyBuilder badRequestBuilder = ResponseEntity.badRequest();
        return badRequestBuilder.build();
    }
*/

    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity handleJpaViolations(DataIntegrityViolationException e) {
        ResponseEntity.BodyBuilder badRequestBuilder = ResponseEntity.badRequest();
        return badRequestBuilder.build();
    }

    @ExceptionHandler(ConstraintViolationException.class)
    ResponseEntity handleBeanValidationViolations(ConstraintViolationException e) {
        List<Map<String, String>> errorList = e.getConstraintViolations().stream()
                .map(constraintViolation -> {
                    Map<String, String> errorMap = new HashMap<>();
                    errorMap.put(
                            constraintViolation.getPropertyPath().toString(),
                            constraintViolation.getMessage()
                    );
                    return errorMap;
                }).collect(Collectors.toList());
        return ResponseEntity.badRequest().body(errorList);
    }

    @ExceptionHandler(DtoProcessingException.class)
    ResponseEntity handleBeanValidationViolations(DtoProcessingException e) {
        return ResponseEntity.badRequest().body(e.getViolationMessages());
    }
}
