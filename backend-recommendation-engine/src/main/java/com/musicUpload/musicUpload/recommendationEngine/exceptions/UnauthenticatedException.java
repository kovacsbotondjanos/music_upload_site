package com.musicUpload.musicUpload.recommendationEngine.exceptions;

public class UnauthenticatedException extends RuntimeException {
    public UnauthenticatedException(String msg) {
        super(msg);
    }

    public UnauthenticatedException() {
        super();
    }
}
