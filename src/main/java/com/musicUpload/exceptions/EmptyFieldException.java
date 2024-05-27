package com.musicUpload.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_ACCEPTABLE)
public class EmptyFieldException extends RuntimeException{
    public EmptyFieldException(String msg){
        super(msg);
    }

    public EmptyFieldException(){
        super();
    }
}
