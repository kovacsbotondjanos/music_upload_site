package com.musicUpload.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_ACCEPTABLE)
public class EmailInWrongFormatException extends RuntimeException{
    public EmailInWrongFormatException(String msg){
        super(msg);
    }

    public EmailInWrongFormatException(){
        super();
    }
}
