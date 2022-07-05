package com.roadmap.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(ItemNotFoundException.class)
    public ResponseEntity<?> handleNotFoundException(ItemNotFoundException exception, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails (new Date (), exception.getMessage (), request.getDescription (false));
        return new ResponseEntity<ErrorDetails> (errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<?> handleBadRequestException(BadRequestException exception, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails (new Date (), exception.getMessage (), request.getDescription (false));
        return new ResponseEntity<ErrorDetails> (errorDetails, HttpStatus.BAD_REQUEST);
    }

}
