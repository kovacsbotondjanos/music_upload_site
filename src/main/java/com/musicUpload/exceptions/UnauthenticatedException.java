package com.musicUpload.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNAUTHORIZED)
public class UnauthenticatedException extends RuntimeException {
    public UnauthenticatedException(String msg) {
        super(msg);
    }

    public UnauthenticatedException() {
        super();
    }
}
