package com.musicUpload.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNPROCESSABLE_ENTITY)
public class ImageCannotBeSavedException extends RuntimeException {
    public ImageCannotBeSavedException(String msg){
        super(msg);
    }

    public ImageCannotBeSavedException(){
        super();
    }
}
