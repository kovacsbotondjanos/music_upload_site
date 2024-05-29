package com.musicUpload.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_ACCEPTABLE)
public class NameInWrongFormatException extends RuntimeException{
    public NameInWrongFormatException(String msg){
        super(msg);
    }

    public NameInWrongFormatException(){
        super();
    }
}
