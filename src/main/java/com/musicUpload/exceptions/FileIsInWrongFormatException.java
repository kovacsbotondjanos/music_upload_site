package com.musicUpload.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNPROCESSABLE_ENTITY)
public class FileIsInWrongFormatException extends RuntimeException {
    public FileIsInWrongFormatException(String msg){
        super(msg);
    }

    public FileIsInWrongFormatException(){
        super();
    }
}
