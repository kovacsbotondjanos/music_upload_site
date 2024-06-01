package com.musicUpload.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_ACCEPTABLE)
public class NotAcceptableException extends RuntimeException{
    public NotAcceptableException(String msg){
        super(msg);
    }

    public NotAcceptableException(){
        super();
    }
}
