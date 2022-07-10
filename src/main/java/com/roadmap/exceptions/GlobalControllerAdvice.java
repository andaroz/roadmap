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
    public ResponseEntity<ErrorDetails> handleNotFoundException(ItemNotFoundException exception, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails (new Date (), exception.getMessage (), request.getDescription (false));
        return new ResponseEntity<> (errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorDetails> handleBadRequestException(BadRequestException exception, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails (new Date (), exception.getMessage (), request.getDescription (false));
        return new ResponseEntity<> (errorDetails, HttpStatus.BAD_REQUEST);
    }

}
