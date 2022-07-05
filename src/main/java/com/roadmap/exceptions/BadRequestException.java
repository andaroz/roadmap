package com.roadmap.exceptions;

import com.roadmap.utility.CommonConstants;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message, boolean suppressStacktrace) {
        super (message, null, suppressStacktrace, !suppressStacktrace);
    }

    public BadRequestException() {
        super (CommonConstants.MESSAGE_BAD_REQUEST, null, true, false);
    }
}
