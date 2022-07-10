package com.roadmap.exceptions;

import com.roadmap.utility.CommonConstants;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ItemNotFoundException extends RuntimeException {

    public ItemNotFoundException() {
        super (CommonConstants.MESSAGE_NOT_FOUND, null, true, false);
    }

    public ItemNotFoundException(String message, boolean suppressStacktrace) {
        super (message, null, suppressStacktrace, !suppressStacktrace);
    }
}
