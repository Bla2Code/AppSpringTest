package com.appspring.rest;

import com.appspring.exception.BadRequestException;
import com.appspring.exception.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ErrorControllerAdvice {

    @ExceptionHandler(value = {BadRequestException.class})
    public void badRequestExceptionHandler(Exception exception, HttpServletResponse response) throws IOException {
        log.error("#badRequestExceptionHandler: Сервер не смог обработать запрос {}", exception.getMessage());
        response.sendError(HttpStatus.BAD_REQUEST.value(), exception.getLocalizedMessage());
    }

    @ExceptionHandler(value = {UserNotFoundException.class, ResponseStatusException.class})
    public void entityNotFoundExceptionHandler(Exception exception, HttpServletResponse response) throws IOException {
        log.error("#entityNotFoundExceptionHandler: Сущность не найдена {}", exception.getMessage());
        response.sendError(HttpStatus.NOT_FOUND.value(), exception.getLocalizedMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public void validationExceptionHandler(
            MethodArgumentNotValidException exception,
            HttpServletResponse response) throws IOException {
        String description = exception.getBindingResult().getFieldErrors()
                .stream()
                .map(FieldError::getField)
                .collect(Collectors.joining(", "));
        log.error("#validatorExceptionHandler: Данные введены некорректно {} {}", description, HttpStatus.BAD_REQUEST);
        response.sendError(HttpStatus.BAD_REQUEST.value(), exception.getLocalizedMessage());
    }

}


