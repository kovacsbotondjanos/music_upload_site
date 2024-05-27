package com.musicUpload.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_ACCEPTABLE)
public class PasswordInWrongFormatException extends RuntimeException{
    public PasswordInWrongFormatException(String msg){
        super(msg);
    }

    public PasswordInWrongFormatException(){
        super();
    }
}
