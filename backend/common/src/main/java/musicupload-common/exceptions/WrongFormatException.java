package com.musicUpload.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_ACCEPTABLE)
public class WrongFormatException extends RuntimeException {
    public WrongFormatException(String msg) {
        super(msg);
    }

    public WrongFormatException() {
        super();
    }
}